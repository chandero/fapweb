<template>
  <el-container>
    <el-header>
      <h1>Comprobantes Contables</h1>
    </el-header>
    <el-main>
        <el-row>
          <el-col :span="24">
            <query-builder :labels="qlabels" :rules="qrules" :styled="qstyled" :maxDepth="3" v-model="qbquery"></query-builder>
            <el-button type="warning" icon="el-icon-search" circle @click="actualizar()" title="Actualizar Aplicando el Filtro"></el-button>
        </el-col>
      </el-row>
      <el-row :gutter="4">
        <el-col :span="2">
          <el-button type="warning" circle mini icon="el-icon-refresh" @click="listaComprobante()" />
        </el-col>
        <el-col :span="2">
          <el-button type="primary" circle mini icon="el-icon-plus" @click="handleNew()" />
        </el-col>
      </el-row>
      <el-row :gutter="4">
        <el-col :span="24">
          <el-table :data="tableData" style="width: 100%;" height="600" :row-class-name="tableRowClassName">
            <el-table-column prop="fecha" label="Fecha" width="100">
              <template slot-scope="scope">
                <span>{{ scope.row.FECHADIA | moment('YYYY-MM-DD')}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="tipo" label="Tipo Comprobante" width="230">
              <template slot-scope="scope">
                <span>{{ tipo(scope.row.TIPO_COMPROBANTE) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="numero" label="Número" width="100">
              <template slot-scope="scope">
                <span>{{ scope.row.ID_COMPROBANTE | pad(6)}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="descripcion" label="Descripción" width="400">
              <template slot-scope="scope">
                <span :title="scope.row.DESCRIPCION">{{ scope.row.DESCRIPCION | fm_truncate(52) }}</span>
              </template>
            </el-table-column>   
            <el-table-column prop="debito" label="V.Débito" width="120">
              <template slot-scope="scope">
                <span>{{ scope.row.TOTAL_DEBITO | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="credito" label="V.Crédito" width="120">
              <template slot-scope="scope">
                <span style="text-align: right;">{{ scope.row.TOTAL_CREDITO | currency }}</span>
              </template>
            </el-table-column> 
            <el-table-column prop="estado" label="Estado" width="100">
              <template slot-scope="scope">
                <span>{{ estado(scope.row.ESTADO) }}</span>
              </template>
            </el-table-column>
            <el-table-column
              fixed="right"
              label="Ver"
              width="140">
              <template slot-scope="scope">
                <el-dropdown :split-button="true" size="mini" type="primary" trigger="click">
                  Info
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item><div @click="handleView(scope.row)"><i class="el-icon-view" title="Dirección y Teléfono" />Consultar</div></el-dropdown-item>
                    <el-dropdown-item><div @click="handlePrint(scope.row)"><i class="el-icon-printer" title="Garantías" />Imprimir</div></el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>            
          </el-table>
        </el-col>
      </el-row>
      <el-row :gutter="4">
        <el-col>
          <el-switch v-model="bhidepage"></el-switch>
          <el-pagination
            :hide-on-single-page="bhidepage"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page.sync="current_page"
            :page-sizes="[10, 20, 300, 400]"
            :page-size="page_size"
            :total="total"
          ></el-pagination>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import VueQueryBuilder from 'vue-query-builder';
import {
  obtenerTiposComprobante,
  obtenerComprobantesPage,
} from "@/api2/contabilidad";
import { obtenerTiposDocumento } from '@/api2/persona'

export default {
  components: {
    'query-builder': VueQueryBuilder
  },  
  data() {
    return {
      tableData: [],
      current_page: 1,
      page_size: 20,
      total: 0,
      bhidepage: true,
      tipos_comprobante: [],
      tipos_identificacion: [],
      orderby: "cc.FECHADIA, cc.TIPO_COMPROBANTE, cc.ID_COMPROBANTE",
      qbquery: {},
      qrules: [
        {
          type: 'select',
          id: 'CAST(TRIM(cc.TIPO_COMPROBANTE) AS INTEGER)',
          label: 'Tipo de Comprobante',
          choices: [],
          operators: ['=']
        },
        {
          type: 'text',
          id: 'cc.ID_COMPROBANTE',
          label: 'Número Comprobante',
          operators: ['igual a']
        },
        {
          type: 'select',
          id: 'ca.ID_IDENTIFICACION',
          label: 'Tipo Identificación',
          choices: [],
          operators: ['igual a']
        },
        {
          type: 'text',
          id: 'ca.ID_PERSONA',
          label: 'Identificación',
          operators: ['igual a']
        },        
        {
          type: "custom-component",
          component: 'el-date-picker',
          id: 'cc.FECHADIA',
          label: 'Fecha',
          operators: ['<', '<=', '>', '>=', '=']
        },
        {
          type: 'select',
          id: 'cc.ESTADO',
          label: 'Estado Actual',
          choices: [
            { label: 'Abierto', value: 'O' },
            { label: 'Cerrado', value: 'C' },
            { label: 'Anulado', value: 'N' }
          ],
          operators: ['igual a']
        }
      ],
      qlabels: {
        matchType: this.$i18n.t('qb.matchType'),
        matchTypes: [
          {
            id: 'all',
            label: this.$i18n.t('qb.matchTypeAll')
          },
          {
            id: 'any',
            label: this.$i18n.t('qb.matchTypeAny')
          }
        ],
        addRule: this.$i18n.t('qb.addRule'),
        removeRule: this.$i18n.t('qb.removeRule'),
        addGroup: this.$i18n.t('qb.addGroup'),
        removeGroup: this.$i18n.t('qb.removeGroup'),
        textInputPlaceholder: this.$i18n.t('qb.textInputPlaceholder')
      },
      qstyled: true      
    };
  },
  beforeMount() {
    this.listaTipoComprobante();
  },
  methods: {
    tableRowClassName({ row /*, rowIndex */ }) {
      if (row.ESTADO === "C") {
        return "success-row";
      } else if (row.ESTADO === "N") {
        return "warning-row";
      }
      return "";
    },
    handleSizeChange(val) {
      this.page_size = val;
      this.listaComprobante();
    },
    handleCurrentChange(val) {
      this.current_page = val;
      this.listaComprobante();
    },
    listaTipoComprobante() {
      obtenerTiposComprobante()
        .then((response) => {
          this.tipos_comprobante = response.data;
          this.tipos_comprobante.forEach(tc => {
            this.qrules[0].choices.push( { label: tc.DESCRIPCION, value: tc.ID });
          });
          this.listaTipoIdentificacion();
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los tipos de comprobantes: " + error,
            type: "error",
          });
        });
    },
    listaTipoIdentificacion() {
      obtenerTiposDocumento().then(response => {
        this.tipos_identificacion = response.data;
        this.tipos_identificacion.forEach(ti => {
          this.qrules[2].choices.push({ label: ti.DESCRIPCION_IDENTIFICACION, value: ti.ID_IDENTIFICACION });
        })
        this.listaComprobante();
      })
    },
    actualizar() {
      this.listaComprobante();
    },
    listaComprobante() {
      obtenerComprobantesPage(this.current_page, this.page_size, this.orderby, this.qbquery)
        .then((response) => {
          this.total = response.data.total;
          this.tableData = response.data.data;
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los comprobantes: " + error,
            type: "error",
          });
        });
    },
    tipo(id) {
      if (id === undefined || id === null) {
        return "SIN ID";
      } else {
        var _id = parseInt(id);
        var tipo = this.tipos_comprobante.find((o) => o.ID === _id);
        if (tipo) {
          return tipo.DESCRIPCION;
        } else {
          return "NO DEFINIDO";
        }
      }
    },
    estado(id) {
      if (id === undefined || id === null) {
        return ''
      } else {
        if (id === 'O') {
          return 'ABIERTO'
        } else if (id === 'C') {
          return 'CERRADO'
        } else if (id === 'N') {
          return 'ANULADO'
        }
      }
    },
    handleNew() {
      this.$router.push({ path: '/contabilidad/menu2proceso/menu2-1comprobante/menu2-1-2gestion/' });
    },
    handleView (row) {
      this.$router.push({ path: '/contabilidad/menu2proceso/menu2-1comprobante/menu2-1-2gestion/' + parseInt(row.TIPO_COMPROBANTE) + '/' + row.ID_COMPROBANTE });
    },
    handlePrint (row) {
      console.log('imprimir: ' + JSON.stringify(row))
    }    
  }
};
</script>
<style>
.el-table .warning-row {
  background: rgba(240, 114, 11, 0.644);
}

.el-table .success-row {
  background: #106af160;
}
</style>