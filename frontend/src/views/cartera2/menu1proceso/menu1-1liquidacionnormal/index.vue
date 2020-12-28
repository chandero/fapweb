<template >
  <el-container>
    <el-main>
      <el-row>
        <el-col>
          <el-tabs type="border-card">
            <el-tab-pane>
              <span slot="label"><i class="el-icon-user-solid" />Buscar Por Documento</span>
              <el-form label-position="left">
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
                  <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                    <el-form-item>
                      <el-button :disabled="id_identificacion == null || id_persona == null" type="success" icon="el-icon-search" title="Buscar Datos" @click="buscarPorDocumento">Buscar</el-button>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>
            <el-tab-pane>
              <span slot="label"><i class="el-icon-search" />Buscar por Colocación</span>
              <el-form label-position="left">
                <el-row>
                  <el-col :span="7">
                    <el-form-item label="Colocacion">
                      <el-input v-model="id_colocacion_buscar" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row>
                  <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                    <el-form-item>
                      <el-button :disabled="id_colocacion_buscar === null || id_colocacion_buscar === ''" type="success" icon="el-icon-search" title="Buscar Datos" @click="buscarPorColocacion">Buscar</el-button>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <div style="text-align: center;"><h3>Colocaciones</h3></div>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-table v-loading="loading" :data="dataColocacion.filter(o => o.saldo !== 0)" :row-class-name="tableRowClassName" highlight-current-row style="width: 100%; font-size: 12px;" max-height="350" >
            <el-table-column type="selection" width="55" />
            <el-table-column sortable label="Colocación" prop="id_colocacion" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_colocacion }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nombre" prop="nombre" width="350">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.nombre }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Saldo" prop="saldo" align="right" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ (scope.row.valor_desembolso - scope.row.abonos_capital) | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cuota" prop="cuota" align="right" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.valor_cuota | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Capital" prop="fecha_capital" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_capital | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Interés" prop="fecha_interes" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_interes | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="D.Mora" prop="dias_mora" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.dias_mora }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Estado" prop="estado" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.estado }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Id" prop="tipo_id" width="50">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_identificacion }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Identificación" prop="documento" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_persona }}</span>
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
                    <el-dropdown-item><div @click="buscarPlanPago(scope.row.id_colocacion)"><i class="el-icon-notebook-2" title="Plan de Pago" />Plan de Pago</div></el-dropdown-item>                    
                    <el-dropdown-item><div @click="buscarExtracto(scope.row.id_colocacion)"><i class="el-icon-document" title="Extracto" />Extracto</div></el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.saldo !== 0"><div @click="buscarLiquidacion(scope.row)"><i class="el-icon-s-finance" title="Liquidación" />Liquidar</div></el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-dialog :visible.sync="showLiquidacionDePruebaDlg" :show-close="false" :destroy-on-close="true" title="Liquidación A Pagar" width="90%">
        <liquidacion-prueba :colocacion="colocacion" @cerrarLiquidacionDePruebaEvent="cerrarLiquidacionDePruebaEvento" />
      </el-dialog>    
      <!-- Dialogo Extracto Colocacion -->
      <el-dialog :visible.sync="showExtractoDlg" :title="'Extracto de Colocación : ' + extracto_colocacion" :show-close="false" :destroy-on-close="true" width="90%">
        <extracto-colocacion :colocacion="extracto_colocacion" :datos="extracto_data" @cerrarExtractoEvent="cerrarExtractoEvento" />
      </el-dialog>
      <!-- Dialogo Plan Pago Colocacion -->
      <el-dialog :visible.sync="showPlanDlg" :title="'Plan de Pago Colocación : ' + plan_colocacion" :show-close="false" :destroy-on-close="true" width="90%">
        <planpago-colocacion :colocacion="plan_colocacion" :datos="plan_data" @cerrarPlanEvent="cerrarPlanEvento" />
      </el-dialog>              
      <!-- Dialogo Buscar por Apellidos y Nombre -->
      <el-dialog :visible.sync="showBuscarPersonaDlg" title="Buscar Por Apellidos y Nombre" width="80%">
        <buscar-por-nombre @selectPersonEvent="setDatosPersonaDesdeElEvento" />
      </el-dialog>    
    </el-main>
  </el-container>
</template>
<script>
import LiquidacionDePruebaComponent from '@/components/LiquidacionDePrueba'
import ExtractoColocacionComponent from '@/components/ExtractoColocacion'
import PlanPagoColocacionComponent from '@/components/PlandePago'

import BuscarPersonaComponent from '@/components/BuscarPersonaNombre'
import { obtenerExtractoColocacion } from '@/api/extractocolocacion'
import { obtenerListaTipoIdentificacion, obtenerListaTipoDireccion } from '@/api/tipos'
import { obtenerPersona } from '@/api/persona'
import { obtenerPlan, obtenerListaColocacion, obtenerListaPorDocumento } from '@/api2/colocacion'
import { buscarCreditoPorDocumento } from '@/api/controlcobro'

export default {
  components: {
    'buscar-por-nombre': BuscarPersonaComponent,    
    'liquidacion-prueba': LiquidacionDePruebaComponent,
    'planpago-colocacion': PlanPagoColocacionComponent,
    'extracto-colocacion': ExtractoColocacionComponent
  },
  data () {
    return {
      id_identificacion: null,
      id_persona: null,
      id_colocacion_buscar: null,
      colocacion: {
        id_agencia: 1,
        id_colocacion: '00000000000'
      },
      showBuscarPersonaDlg: false,
      showLiquidacionDePruebaDlg: false,
      tipo_documento: [],
      tipos_estado_colocacion: null,
      dataColocacion: [],
      loading: false,
      nombre: null,
      nombrecompleto: '',
      plan_colocacion: null,
      plan_data: [],
      extracto_colocacion: null,
      extracto_data: [],
      showExtractoDlg: false,
      showPlanDlg: false,
    }
  },
  methods: {
    tableRowClassName({ row, /* rowIndex */ }) {
      if (row.estado === 'JURIDICO') {
        return 'warning-row'
      } else if (row.estado === 'SALDADO') {
        return 'danger-row'
      }
      return ''
    },    
    limpiarTablas() {
      this.dataColocacion = []
      this.colocacion = {
        id_agencia: 1,
        id_colocacion: '00000000000'
      }
    },    
    buscarPorColocacion() {
      this.loading = true
      this.limpiarTablas()
      obtenerListaColocacion(this.id_colocacion_buscar).then(response => {
        this.dataColocacion = response.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    buscarLiquidacion(colocacion) {
      this.extracto_colocacion = colocacion.ID_COLOCACION
      this.colocacion = colocacion
      this.showLiquidacionDePruebaDlg = true
    },
    setDatosPersonaDesdeElEvento(data) {
      this.id_identificacion = data.id_identificacion
      this.id_persona = data.id_persona
      this.showBuscarPersonaDlg = false
      this.buscarPersona()
    },
    /* buscarPorDocumento() {
      this.loading = true
      this.limpiarTablas()
      obtenerListaPorDocumento(this.id_identificacion, this.id_persona).then(response => {
        this.dataColocacion = response.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }, */
    buscarPorDocumento() {
      this.loading = true
      this.limpiarTablas()
      buscarCreditoPorDocumento(this.id_identificacion, this.id_persona).then(response => {
        this.dataColocacion = response.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      this.limpiarTablas()
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
    },
    buscarExtracto(id_colocacion) {
      this.mostrarLoader()
      obtenerExtractoColocacion(id_colocacion).then(response => {
        this.extracto_colocacion = id_colocacion
        this.extracto_data = response.data
        this.showExtractoDlg = true
        this.ocultarLoader()
      }).catch(error => {
        this.ocultarLoader()
        this.$message.error('Error buscando extracto colocación: ' + error)
      })
    },
    buscarPlanPago(id_colocacion) {
      this.mostrarLoader()
      obtenerPlan(id_colocacion).then(response => {
        this.plan_colocacion = id_colocacion
        this.plan_data = response.data
        console.log("plan_data: " + this.plan_data)
        this.showPlanDlg = true
        this.ocultarLoader()
      }).catch(error => {
        this.ocultarLoader()
        this.$message.error('Error buscando plan de pago de colocación: ' + error)
      })
    },
    cerrarLiquidacionDePruebaEvento() {
      this.extracto_colocacion = null
      this.showLiquidacionDePruebaDlg = false
    },
    cerrarPlanEvento() {
      this.plan_data = []
      this.plan_colocacion = ''
      this.showPlanDlg = false
    },
    cerrarExtractoEvento() {
      this.extracto_data = []
      this.extracto_colocacion = ''
      this.showExtractoDlg = false
    },
    mostrarLoader() {
      this.loader = this.$loading({
        lock: true,
        text: 'Cargando',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    ocultarLoader() {
      if (this.loader) {
        this.loader.close()
      }
    }
  },
  beforeMount() {
    obtenerListaTipoIdentificacion().then(response => {
      this.tipo_documento = response.data
    }).catch(error => {
      console.log('Error Consultando Estados de Colocacion: ' + error)
    })
  },  
}
</script>