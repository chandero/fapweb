<template>
  <el-container v-loading.fullscreen.lock="fullscreenLoading">
    <el-header>
      <h2>Balance General</h2>
    </el-header>
    <el-main>
      <el-form>  
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
            <el-form-item label="Periodo">
              <el-date-picker v-model="fc" type="month" />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="Nivel">
              <el-select v-model="n">
                      <el-option
                        v-for="item in niveles"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                      </el-option>
              </el-select>
            </el-form-item>
          </el-col>          
        </el-row>  
        <el-row>
          <el-col>
            <el-button :disabled="validar()" type="primary" mini @click="obtener()">Consultar</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-main>
    <!-- Dialogo Datos Balance -->
    <el-dialog :visible.sync="showDatosBalanceDlg" title="Balance General" width="90%" height="550">
      <el-row>
        <el-col :span="24">
          <el-table
            :data="balanceData"
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
              label="Cuenta"
              width="280">
              <template slot-scope="scope">
                <span :title="scope.row.cuenta">{{ scope.row.cuenta | fm_truncate(52)}} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Débito Ant."
              width="150"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.debito_ant | currency_2 }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Crédito Ant."
              width="150"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.credito_ant | currency_2 }}</span>
              </template>
            </el-table-column>            
            <el-table-column
              label="Débito Mov"
              width="150"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.debito_mov | currency_2 }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Crédito Mov"
              width="150"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.credito_mov | currency_2 }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Débito Act"
              width="150"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.debito_act | currency_2 }} </span>
              </template>
            </el-table-column>
            <el-table-column
              label="Crédito Act"
              width="150"
              align="right">
              <template slot-scope="scope">
                <span>{{ scope.row.credito_act | currency_2 }} </span>
              </template>
            </el-table-column> 
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-dropdown>
            <el-button type="primary">
              Exportar<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item><span @click="exportarXls(1)">1 Columna</span></el-dropdown-item>
            <el-dropdown-item><span @click="exportarXls(4)">4 Columnas</span></el-dropdown-item>
            <el-dropdown-item><span @click="exportarXls(6)">6 Columnas</span></el-dropdown-item>
          </el-dropdown-menu>
          </el-dropdown>
        </el-col>
      </el-row>
    </el-dialog>
  </el-container>
</template>
<script>
import PucTree from "@/components/PucTree";
import { obtenerNombrePuc } from '@/api/puc'
import { consultar, aExcel } from '@/api/balance'

export default {
  components: {
    puctree: PucTree
  },  
  data () {
    return {
      niveles: [{
          value: 1,
          label: 'Nivel 1'
      }, {
          value: 2,
          label: 'Nivel 2'
      }, {
          value: 3,
          label: 'Nivel 3'
      }, {
          value: 4,
          label: 'Nivel 4'
      }, {
          value: 5,
          label: 'Nivel 5'
      }, {
          value: 6,
          label: 'Nivel 6'
      }, {
          value: 7,
          label: 'Nivel 7'
      }, {
          value: 8,
          label: 'Nivel 8'
      }
      ],
      balanceData: [],
      ci: '',
      cf: '',
      cin: null,
      cfn: null,
      fc: new Date(),
      n: 5,
      showDatosBalanceDlg: false,
      nombre: null,
      tipo_documento: null,
      puc_visible_ci: false,
      puc_visible_cf: false,
      fullscreenLoading: false
    }
  },
  methods: {
    validar() {
      if (this.ci && this.cf && this.cin && this.cfn && this.fc && this.n ) {
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
      consultar(this.ci, this.cf, this.fc.getTime(), this.n).then(response => {
        loading.close()
        this.balanceData = response.data;
        this.showDatosBalanceDlg = true;
      }).catch(() => {
        loading.close()
      })
    },
    exportarXls (cm) {
      const loading = this.$loading({
          lock: true,
          text: 'Exportando Datos, por favor espere...',
          background: 'rgba(255, 255, 255, 0.7)'
        });
      aExcel(this.ci, this.cf, this.fc.getTime(), this.n, cm).then(response => {
        loading.close()
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'fap102_balance_contable.xlsx');
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
    }
  }
}
</script>