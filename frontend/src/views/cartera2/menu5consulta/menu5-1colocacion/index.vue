<template>
  <el-container>
    <el-header>
      <el-row>
        <el-col :span="24">
          <h1>Consulta de Colocación</h1>
        </el-col>
      </el-row>
    </el-header>
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
          <el-table v-loading="loading" :data="dataColocacion" :row-class-name="tableRowClassName" highlight-current-row style="width: 100%; font-size: 12px;" max-height="350" >
            <el-table-column type="selection" width="55" />
            <el-table-column sortable label="Colocación" prop="id_colocacion" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.ID_COLOCACION }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nombre" prop="nombre" width="350">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.NOMBRE }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Saldo" prop="saldo" align="right" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">${{ (scope.row.VALOR_DESEMBOLSO - scope.row.ABONOS_CAPITAL) | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cuota" prop="cuota" align="right" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">${{ scope.row.VALOR_CUOTA | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Capital" prop="fecha_capital" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.FECHA_CAPITAL | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Interés" prop="fecha_interes" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.FECHA_INTERES | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="D.Mora" prop="dias_mora" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.DIAS_MORA }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Estado" prop="estado" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.ESTADO }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Id" prop="tipo_id" width="50">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.ID_IDENTIFICACION }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Identificación" prop="documento" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.ID_PERSONA }}</span>
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
                    <el-dropdown-item><div @click="buscarDireccion(scope.row)"><i class="el-icon-service" title="Dirección y Teléfono" />Dirección</div></el-dropdown-item>
                    <el-dropdown-item><div @click="buscarGarantia(scope.row.ID_COLOCACION)"><i class="el-icon-s-custom" title="Garantías" />Garantía</div></el-dropdown-item>
                    <el-dropdown-item><div @click="buscarPlanPago(scope.row.ID_COLOCACION)"><i class="el-icon-notebook-2" title="Plan de Pago" />Plan de Pago</div></el-dropdown-item>                    
                    <el-dropdown-item><div @click="buscarExtracto(scope.row.ID_COLOCACION)"><i class="el-icon-document" title="Extracto" />Extracto</div></el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.saldo !== 0"><div @click="buscarLiquidacion(scope.row)"><i class="el-icon-s-finance" title="Liquidación" />Liquidación</div></el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col />
      </el-row>
      <el-row>
        <el-col />
      </el-row>
    </el-main>
    <!-- Dialogo Garantias Colocacion -->
    <el-dialog :visible.sync="showGarantiaDlg" :title="'Garantías Colocación: ' + extracto_colocacion" :show-close="false" :destroy-on-close="true" width="90%">
      <garantias :colocacion="extracto_colocacion" :personal="personal" :real="real" :pignoracion="pignoracion" @cerrarGarantiaEvent="cerrarGarantiaEvento" />
    </el-dialog>
    <!-- Dialogo Extracto Colocacion -->
    <el-dialog :visible.sync="showExtractoDlg" :title="'Extracto de Colocación : ' + extracto_colocacion" :show-close="false" :destroy-on-close="true" width="90%">
      <extracto-colocacion :colocacion="extracto_colocacion" :datos="extracto_data" @cerrarExtractoEvent="cerrarExtractoEvento" />
    </el-dialog>
    <!-- Dialogo Buscar por Apellidos y Nombre -->
    <el-dialog :visible.sync="showBuscarPersonaDlg" title="Buscar Por Apellidos y Nombre" width="80%">
      <buscar-por-nombre @selectPersonEvent="setDatosPersonaDesdeElEvento" />
    </el-dialog>
    <!-- Dialogo de Direcciones-->
    <el-dialog :visible.sync="showDireccionDlg" :show-close="false" :destroy-on-close="true" :title="'Dirección y Teléfono de ' + nombrecompleto" width="90%" append-to-body>
      <direccion :direcciones="direcciones" @cerrarDireccionEvent="cerrarDireccionEvento"/>
    </el-dialog>
    <!-- Dialogo Liquidacion de Prueba Colocacion -->
    <el-dialog :visible.sync="showLiquidacionDePruebaDlg" :show-close="false" :destroy-on-close="true" title="Liquidación Proyectada" width="90%">
      <liquidacion-prueba :colocacion="colocacion" @cerrarLiquidacionDePruebaEvent="cerrarLiquidacionDePruebaEvento" />
    </el-dialog>
    <!-- Dialogo Plan Pago Colocacion -->
    <el-dialog :visible.sync="showPlanDlg" :title="'Plan de Pago Colocación : ' + plan_colocacion" :show-close="false" :destroy-on-close="true" width="90%">
      <planpago-colocacion :colocacion="plan_colocacion" :datos="plan_data" @cerrarPlanEvent="cerrarPlanEvento" />
    </el-dialog>    
  </el-container>

</template>
<script>
import BuscarPersonaComponent from '@/components/BuscarPersonaNombre'
import ExtractoColocacionComponent from '@/components/ExtractoColocacion'
import GarantiaCreditoComponent from '@/components/GarantiaCredito'
import DireccionPersonaComponent from '@/components/DireccionPersona'
import LiquidacionDePruebaComponent from '@/components/LiquidacionDePrueba'
import PlanPagoColocacionComponent from '@/components/PlandePago'
import { obtenerGarantiaPersonal, obtenerGarantiaReal } from '@/api/credito'
import { obtenerExtractoColocacion } from '@/api/extractocolocacion'
import { obtenerListaTipoIdentificacion, obtenerListaTipoDireccion } from '@/api/tipos'
import { buscarCreditoPorEstado, buscarDireccionPersona } from '@/api/controlcobro'
import { obtenerPersona } from '@/api/persona'
import { obtenerPlan, obtenerListaColocacion, obtenerListaPorDocumento } from '@/api2/colocacion'

export default {
  components: {
    'buscar-por-nombre': BuscarPersonaComponent,
    'extracto-colocacion': ExtractoColocacionComponent,
    'garantias': GarantiaCreditoComponent,
    'direccion': DireccionPersonaComponent,
    'liquidacion-prueba': LiquidacionDePruebaComponent,
    'planpago-colocacion': PlanPagoColocacionComponent
  },
  data() {
    return {
      id_estado_colocacion: -1,
      dias_ini: null,
      dias_fin: null,
      id_identificacion: null,
      id_persona: null,
      colocacion: {
        id_agencia: 1,
        id_colocacion: '00000000000'
      },
      id_colocacion_buscar: null,
      controlcobro: {
        fecha_observacion: new Date(),
        es_compromiso: false,
        fecha_compromiso: null,
        observacion: null,
        es_observacion: false
      },
      nombre: null,
      nombrecompleto: '',
      ases_id: -1,
      tipo_documento: null,
      tipos_estado_colocacion: null,
      dataColocacion: [],
      dataDireccion: [],
      dataControlCobro: [],
      loading: false,
      direcciones: null,
      tiposdireccion: [],
      municipios: [],
      asesores: [],
      extracto_colocacion: null,
      extracto_data: [],
      plan_colocacion: null,
      plan_data: [],
      personal: [],
      real: [],
      pignoracion: [],
      showDireccionDlg: false,
      showBuscarPersonaDlg: false,
      showControlCobroDlg: false,
      showExtractoDlg: false,
      showGarantiaDlg: false,
      showLiquidacionDePruebaDlg: false,
      showPlanDlg: false,
      loader: null
    }
  },
  beforeMount() {
    obtenerListaTipoIdentificacion().then(response => {
      this.tipo_documento = response.data
      obtenerListaTipoDireccion().then(response => {
        this.tiposdireccion = response.data
      }).catch(error => {
        console.log('Error Consultando Lista Tipo Direccion: ' + error)
      })
    }).catch(error => {
      console.log('Error Consultando Estados de Colocacion: ' + error)
    })
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
    buscarLiquidacion(colocacion) {
      this.extracto_colocacion = colocacion.ID_COLOCACION
      this.colocacion = colocacion
      this.showLiquidacionDePruebaDlg = true
    },
    cerrarExtractoEvento() {
      this.extracto_data = []
      this.extracto_colocacion = ''
      this.showExtractoDlg = false
    },
    cerrarPlanEvento() {
      this.plan_data = []
      this.plan_colocacion = ''
      this.showPlanDlg = false
    },    
    buscarGarantia(id_colocacion) {
      console.log('estoy en garantias')
      this.extracto_colocacion = id_colocacion
      obtenerGarantiaPersonal(id_colocacion).then(response => {
        this.personal = response.data
        obtenerGarantiaReal(id_colocacion).then(response => {
          this.real = response.data.filter(d => d.modelo_vehiculo === 0)
          this.pignoracion = response.data.filter(d => d.modelo_vehiculo !== 0)
          this.showGarantiaDlg = true
        }).catch((error) => {
          this.$message.error('Error obteniendo datos garantia real: ' + error)
        })
      }).catch((error) => {
        this.$message.error('Error obteniendo datos garantia personal: ' + error)
      })
    },
    cerrarGarantiaEvento() {
      this.extracto_colocacion = null
      this.showGarantiaDlg = false
    },
    buscarPorEstado() {
      this.loading = true
      this.limpiarTablas()
      buscarCreditoPorEstado(this.id_estado_colocacion, this.dias_ini, this.dias_fin, this.ases_id).then(response => {
        this.dataColocacion = response.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
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
    setDatosPersonaDesdeElEvento(data) {
      this.id_identificacion = data.id_identificacion
      this.id_persona = data.id_persona
      this.showBuscarPersonaDlg = false
      this.buscarPersona()
    },
    buscarPorDocumento() {
      this.loading = true
      this.limpiarTablas()
      obtenerListaPorDocumento(this.id_identificacion, this.id_persona).then(response => {
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
    buscarDireccion(persona) {
      this.nombrecompleto = persona.NOMBRE
      this.direcciones = []
      buscarDireccionPersona(persona.ID_IDENTIFICACION, persona.ID_PERSONA).then(response => {
        this.direcciones = response.data
        this.showDireccionDlg = true
      }).catch(() => {
        this.showDireccionDlg = false
      })
    },
    cerrarDireccionEvento() {
      this.direcciones = []
      this.showDireccionDlg = false
    },
    cerrarLiquidacionDePruebaEvento() {
      this.extracto_colocacion = null
      this.showLiquidacionDePruebaDlg = false
    },
    limpiarTablas() {
      this.dataColocacion = []
      this.dataControlCobro = []
      this.dataDireccion = []
      this.colocacion = {
        id_agencia: 1,
        id_colocacion: '00000000000'
      }
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
  }
}
</script>
<style>
  .el-table .warning-row {
    background: #E6A23C;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }

  .el-table .danger-row {
    background: #F56C6C;
  }
</style>
