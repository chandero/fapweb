<template>
  <el-container v-loading.fullscreen.lock="fullscreenLoading">
    <el-header>
      <h2>Consulta de Auxiliar</h2>
    </el-header>
    <el-main>
      <el-form>  
        <el-row>
          <el-col :xs="24" :sm="12" :md="3" :lg="3" :xl="3">
            <el-form-item label="Año a Consultar">
              <el-input type="number" v-model="anho" maxlength="4"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="6">
          <el-col :span="5">
            <el-form-item label="Codigo Inicial">
              <el-input v-model="ci" maxlength="18" @blur="buscarCi()"/>
            </el-form-item>
          </el-col>
          <el-col :span="1">
            <el-form-item>
              <el-popover placement="right" width="700" trigger="click" v-model="puc_visible_ci">
                <puctree @selected="colocarCodigoCi" />
                <el-button slot="reference" type="primary" mini circle icon="el-icon-search"></el-button>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="18">
            <el-form-item label="Cuenta">
              <el-input readonly v-model="cin" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="5">
            <el-form-item label="Codigo Final">
              <el-input v-model="cf" maxlength="18" @blur="buscarCf()"/>
            </el-form-item>
          </el-col>
          <el-col :span="1">
            <el-form-item>
              <el-popover placement="right" width="700" trigger="click" v-model="puc_visible_cf">
                <puctree @selected="colocarCodigoCf" />
                <el-button slot="reference" type="primary" mini circle icon="el-icon-search"></el-button>
              </el-popover>
            </el-form-item>
          </el-col>          
          <el-col :span="18">
            <el-form-item label="Cuenta">
              <el-input readonly v-model="cfn" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="Fecha Inicial">
              <el-date-picker v-model="fi" />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="Fecha Final">
              <el-date-picker v-model="ff" />
            </el-form-item>
          </el-col>
        </el-row>  
        <el-row :gutter="4">
          <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
            <el-form-item prop="id_identificacion" label="Tipo de Documento">
              <el-select v-model="id_identificacion" filterable>
                <el-option v-for="t in tipo_documento" :key="t.id_identificacion" :label="t.descripcion_identificacion" :value="t.id_identificacion" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
            <el-form-item prop="id_persona" label="Número de Documento">
              <el-input v-model="id_persona" @blur="buscarPersona"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <el-form-item label="Nombre">
              <el-input v-model="nombre" readonly />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
            <el-button type="primary" icon="el-icon-search" circle title="Buscar Por Apellidos y Nombre" @click="showBuscarPersonaDlg = true" />
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-button :disabled="validar()" type="primary" mini @click="obtener()">Consultar</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-main>
    <!-- Dialogo Buscar por Apellidos y Nombre -->
    <el-dialog :visible.sync="showBuscarPersonaDlg" title="Buscar Por Apellidos y Nombre" width="80%">
      <buscar-por-nombre @selectPersonEvent="setDatosPersonaDesdeElEvento" />
    </el-dialog>
    <!-- Dialogo Datos Auxiliar -->
    <el-dialog :visible.sync="showDatosAuxiliarDlg" title="Auxiliares" width="80%" height="550">
      <el-row>
        <el-col :span="24">
          <el-table
            :data="auxiliarData"
            style="width: 100%; font-size: 12px;"
            height="400"
          >
            <el-table-column
              label="Código"
              width="120">
              <template slot-scope="scope">
                <span>{{ scope.row.codigo | codigopuc }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Fecha"
              width="85">
              <template slot-scope="scope">
                <span>{{ scope.row.fecha | moment('YYYY-MM-DD') }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Tipo"
              width="50">
              <template slot-scope="scope">
                <span>{{ scope.row.abreviatura }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Número"
              width="70">
              <template slot-scope="scope">
                <span>{{ scope.row.id_comprobante }}</span>
              </template>
            </el-table-column>            
            <el-table-column
              label="Detalle"
              width="350">
              <template slot-scope="scope">
                <span :title="scope.row.detalle">{{ scope.row.detalle | fm_truncate(45)}} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Saldo Anterior"
              width="120"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.saldo_anterior | currency_2 }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Débito"
              width="120"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.debito | currency_2 }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Crédito"
              width="120"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.credito | currency_2 }} </span>
              </template>
            </el-table-column> 
            <el-table-column
              label="Nuevo Saldo"
              width="120"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.nuevo_saldo | currency_2 }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Documento"
              width="150"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.id_persona }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Persona"
              width="300"
              align="left">
              <template slot-scope="scope">
                <span>{{ scope.row.persona }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Cuenta"
              width="80"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.id_cuenta }} </span>
              </template>
            </el-table-column> 
            <el-table-column
              label="Colocación"
              width="100"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.id_colocacion }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Cheque"
              width="80"
              align="left">
              <template slot-scope="scope">
                <span>{{ scope.row.cheque }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Monto RF"
              width="100"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.monto_retencion | currency_2 }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="% RF"
              width="80"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.tasa_retencion | currency_2 }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-button type="primary" @click="exportarXlsx()">Exportar</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </el-container>
</template>
<script>
import BuscarPersonaComponent from '@/components/BuscarPersonaNombre'
import PucTree from "@/components/PucTree";
import { obtenerListaTipoIdentificacion } from '@/api/tipos'
import { obtenerNombrePuc } from '@/api/puc'
import { obtenerPersona } from '@/api/persona'
import { consultar, aExcel } from '@/api/auxiliar'

export default {
  components: {
    'buscar-por-nombre': BuscarPersonaComponent,
    puctree: PucTree
  },  
  data () {
    return {
      auxiliarData: [],
      ci: '',
      cf: '',
      cin: null,
      cfn: null,
      anho: new Date().getFullYear(),
      fi: null,
      ff: null,
      id_identificacion: 0,
      id_persona: '',
      showBuscarPersonaDlg: false,
      showDatosAuxiliarDlg: false,
      nombre: null,
      tipo_documento: null,
      puc_visible_ci: false,
      puc_visible_cf: false,
      fullscreenLoading: false
    }
  },
  beforeMount() {
    this.fi = new Date(this.anho, 0, 1)
    this.ff = new Date(this.anho, 11, 31)
    obtenerListaTipoIdentificacion().then(response => {
      this.tipo_documento = response.data
    })
  },
  methods: {
    validar() {
      if (this.ci && this.cf && this.cin && this.cfn && this.anho && this.fi && this.ff ) {
        return false
      } else {
        return true
      }
    },
    obtener () {
      const loading = this.$loading({
          lock: true,
          text: 'Cargando Datos, por favor espere...',
          background: 'rgba(255, 255, 255, 0.7)'
        });
      consultar(this.ci, this.cf, parseInt(this.anho), this.fi.getTime(), this.ff.getTime(), this.id_identificacion, this.id_persona).then(response => {
        loading.close()
        this.auxiliarData = response.data;
        this.showDatosAuxiliarDlg = true;
      }).catch(() => {
        loading.close()
      })
    },
    exportarXlsx () {
      const loading = this.$loading({
          lock: true,
          text: 'Exportando Datos, por favor espere...',
          background: 'rgba(255, 255, 255, 0.7)'
        });
      aExcel(this.ci, this.cf, parseInt(this.anho), this.fi.getTime(), this.ff.getTime(), this.id_identificacion, this.id_persona).then(response => {
        loading.close()
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'fap101_auxiliar_contable.xlsx');
        document.body.appendChild(link);
        link.click();
      }).catch((error) => { 
          console.log(error)
          loading.close()
      })
    },    
    buscarCi () {
      while (this.ci.length < 18) this.ci = this.ci + "0"
      obtenerNombrePuc(this.ci).then(response => {
        this.cin = response.data
      })
    },
    buscarCf () {
      console.log("codigo ant:" + this.cf)
      while (this.cf.length < 18) this.cf = this.cf + "0"
      obtenerNombrePuc(this.cf).then(response => {
        this.cfn = response.data
      })
    },
    colocarCodigoCi(codigo) {
      this.puc_visible_ci = false;
      this.ci = codigo;
      this.buscarCi();
    },
    colocarCodigoCf(codigo) {
      this.puc_visible_cf = false;
      this.cf = codigo;
      this.buscarCf();
    },    
    setDatosPersonaDesdeElEvento(data) {
      this.id_identificacion = data.id_identificacion
      this.id_persona = data.id_persona
      this.showBuscarPersonaDlg = false
      this.buscarPersona()
    },
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      obtenerPersona(this.id_identificacion, this.id_persona).then(response => {
        loading.close()
        if (response.status === 200) {
          this.nombre = response.data.a.nombre + ' ' + response.data.a.primer_apellido + ' ' + response.data.a.segundo_apellido
        }
      }).catch(() => {
        this.nombre = null
        loading.close()
        this.$alert('Persona No Existe', 'Buscando Persona')
      })
    }        
  },
  watch: {
    anho: function (val) {
      this.fi = new Date(val, 0, 1)
      this.ff = new Date(val, 11, 31)
    }
  }
}
</script>