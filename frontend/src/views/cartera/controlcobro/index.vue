<template>
  <el-container>
    <el-header>
      <el-row>
        <el-col :span="24">
          <h1>CONTROL DE COBRO</h1>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-row>
        <el-col>
          <el-tabs type="border-card">
            <el-tab-pane>
              <span slot="label"
                ><i class="el-icon-search" />Busqueda General</span
              >
              <el-form label-position="left">
                <el-row>
                  <el-col :span="7">
                    <el-form-item label="Estado">
                      <el-select v-model="id_estado_colocacion" filterable>
                        <el-option
                          v-for="t in tipos_estado_colocacion"
                          :key="t.id"
                          :label="t.descripcion"
                          :value="t.id"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="7">
                    <el-form-item label="Días desde">
                      <el-input v-model="dias_ini" style="width: 50%" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="7">
                    <el-form-item label="Días hasta">
                      <el-input v-model="dias_fin" style="width: 50%" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                    <el-form-item prop="ases_id" label="Asesor">
                      <el-select v-model="ases_id" filterable>
                        <el-option
                          v-for="a in asesores"
                          :key="a.id"
                          :label="a.descripcion"
                          :value="a.id"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row>
                  <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                    <el-form-item>
                      <el-button
                        :disabled="
                          dias_ini == null ||
                          dias_fin == null ||
                          id_estado_colocacion == null
                        "
                        type="success"
                        icon="el-icon-search"
                        title="Buscar Datos"
                        @click="buscarPorEstado"
                        >Buscar</el-button
                      >
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>
            <el-tab-pane>
              <span slot="label"
                ><i class="el-icon-user-solid" />Buscar Por Documento</span
              >
              <el-form label-position="left">
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                    <el-form-item
                      prop="id_identificacion"
                      label="Tipo de Documento"
                    >
                      <el-select v-model="id_identificacion" filterable>
                        <el-option
                          v-for="t in tipo_documento"
                          :key="t.id_identificacion"
                          :label="t.descripcion_identificacion"
                          :value="t.id_identificacion"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                    <el-form-item prop="id_persona" label="Número de Documento">
                      <el-input v-model="id_persona" @blur="buscarPersona" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                    <el-form-item label="Nombre">
                      <el-input v-model="nombre" readonly />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                    <el-button
                      type="primary"
                      icon="el-icon-search"
                      circle
                      title="Buscar Por Apellidos y Nombre"
                      @click="showBuscarPersonaDlg = true"
                    />
                  </el-col>
                </el-row>
                <el-row>
                  <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                    <el-form-item>
                      <el-button
                        :disabled="
                          id_identificacion == null || id_persona == null
                        "
                        type="success"
                        icon="el-icon-search"
                        title="Buscar Datos"
                        @click="buscarPorDocumento"
                        >Buscar</el-button
                      >
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
          <div style="text-align: center"><h3>Colocaciones</h3></div>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-table
            v-loading="loading"
            :data="dataColocacion"
            :row-class-name="tableRowClassName"
            highlight-current-row
            style="width: 100%; font-size: 12px"
            max-height="350"
            @current-change="handleCurrentChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column
              sortable
              label="Colocación"
              prop="id_colocacion"
              width="110"
            >
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.id_colocacion
                }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nombre" prop="nombre" width="350">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.nombre }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Saldo"
              prop="saldo"
              align="right"
              width="110"
            >
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.saldo | currency
                }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Cuota"
              prop="cuota"
              align="right"
              width="110"
            >
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.cuota | currency
                }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Capital" prop="fecha_capital" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.fecha_capital | moment("YYYY-MM-DD")
                }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Interés" prop="fecha_interes" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.fecha_interes | moment("YYYY-MM-DD")
                }}</span>
              </template>
            </el-table-column>
            <el-table-column
              sortable
              label="D.Mora"
              prop="dias_mora"
              width="80"
            >
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
                <span style="margin-left: 10px">{{
                  scope.row.id_identificacion
                }}</span>
              </template>
            </el-table-column>
            <el-table-column
              sortable
              label="Identificación"
              prop="documento"
              width="110"
            >
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{
                  scope.row.id_persona
                }}</span>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="Ver" width="140">
              <template slot-scope="scope">
                <el-dropdown
                  :split-button="true"
                  size="mini"
                  type="primary"
                  trigger="click"
                >
                  Info
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      ><div @click="buscarDireccion(scope.row)">
                        <i
                          class="el-icon-service"
                          title="Dirección y Teléfono"
                        />Dirección
                      </div></el-dropdown-item
                    >
                    <el-dropdown-item
                      ><div @click="buscarGarantia(scope.row.id_colocacion)">
                        <i class="el-icon-s-custom" title="Garantías" />Garantía
                      </div></el-dropdown-item
                    >
                    <el-dropdown-item
                      ><div @click="buscarExtracto(scope.row.id_colocacion)">
                        <i class="el-icon-document" title="Extracto" />Extracto
                      </div></el-dropdown-item
                    >
                    <el-dropdown-item v-if="scope.row.saldo !== 0"
                      ><div @click="buscarLiquidacion(scope.row)">
                        <i
                          class="el-icon-s-finance"
                          title="Liquidación"
                        />Liquidación
                      </div></el-dropdown-item
                    >
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-tabs type="border-card">
            <el-tab-pane label="Compromiso">
              <el-row>
                <el-col :span="24">
                  <div style="text-align: center">
                    <h3>Observaciones y Compromisos</h3>
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col>
                  <el-table
                    v-loading="loading"
                    :data="dataControlCobro"
                    stripe
                    style="width: 100%; font-size: 12px"
                    max-height="350"
                  >
                    <el-table-column
                      sortable
                      label="Fecha"
                      prop="fecha_observacion"
                      width="110"
                    >
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{
                          scope.row.fecha_observacion | moment("YYYY-MM-DD")
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      sortable
                      label="Observación"
                      prop="observacion"
                      width="450"
                    >
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{
                          scope.row.observacion
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      label="Compromiso"
                      prop="fecha_compromiso"
                      align="right"
                      width="110"
                    >
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{
                          scope.row.fecha_compromiso | moment("YYYY-MM-DD")
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      label="Reportó"
                      prop="empleado"
                      align="right"
                      width="250"
                    >
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{
                          scope.row.empleado
                        }}</span>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-button type="primary" @click="showControlCobroDlg = true"
                    >Agregar</el-button
                  >
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane label="Carta Primer Cobro">
              <el-row>
                <el-col><span>Cartas</span></el-col>
              </el-row>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </el-main>
    <!-- Dialogo Garantias Colocacion -->
    <el-dialog
      :visible.sync="showGarantiaDlg"
      :title="'Garantías Colocación: ' + extracto_colocacion"
      :show-close="false"
      :destroy-on-close="true"
      width="90%"
    >
      <garantias
        :colocacion="extracto_colocacion"
        :personal="personal"
        :real="real"
        :pignoracion="pignoracion"
        @cerrarGarantiaEvent="cerrarGarantiaEvento"
      />
    </el-dialog>
    <!-- Dialogo Extracto Colocacion -->
    <el-dialog
      :visible.sync="showExtractoDlg"
      :title="'Extracto de Colocación : ' + extracto_colocacion"
      :show-close="false"
      :destroy-on-close="true"
      width="90%"
    >
      <extracto-colocacion
        :colocacion="extracto_colocacion"
        :datos="extracto_data"
        @cerrarExtractoEvent="cerrarExtractoEvento"
      />
    </el-dialog>
    <!-- Dialogo Buscar por Apellidos y Nombre -->
    <el-dialog
      :visible.sync="showBuscarPersonaDlg"
      title="Buscar Por Apellidos y Nombre"
      width="80%"
    >
      <buscar-por-nombre @selectPersonEvent="setDatosPersonaDesdeElEvento" />
    </el-dialog>
    <!-- Dialogo Control Cobro -->
    <el-dialog
      :visible.sync="showControlCobroDlg"
      title="Agregar Observación"
      width="80%"
    >
      <control-cobro
        :agencia="colocacion.id_agencia"
        :colocacion="colocacion.id_colocacion"
        :controlcobro="controlcobro"
        @savedEvent="validarAgregarControlCobro"
      />
    </el-dialog>
    <!-- Dialogo de Direcciones-->
    <el-dialog
      :visible.sync="showDireccionDlg"
      :show-close="false"
      :destroy-on-close="true"
      :title="'Dirección y Teléfono de ' + nombrecompleto"
      width="90%"
      append-to-body
    >
      <direccion
        :direcciones="direcciones"
        @cerrarDireccionEvent="cerrarDireccionEvento"
      />
    </el-dialog>
    <!-- Dialogo Liquidacion de Prueba Colocacion -->
    <el-dialog
      :visible.sync="showLiquidacionDePruebaDlg"
      :show-close="false"
      :destroy-on-close="true"
      title="Liquidación Proyectada"
      width="90%"
    >
      <liquidacion-prueba
        :colocacion="colocacion"
        @cerrarLiquidacionDePruebaEvent="cerrarLiquidacionDePruebaEvento"
      />
    </el-dialog>
  </el-container>
</template>
<script>
import BuscarPersonaComponent from "@/components/BuscarPersonaNombre";
import ControlCobroComponent from "@/components/ControlCobro";
import ExtractoColocacionComponent from "@/components/ExtractoColocacion";
import GarantiaCreditoComponent from "@/components/GarantiaCredito";
import DireccionPersonaComponent from "@/components/DireccionPersona";
import LiquidacionDePruebaComponent from "@/components/LiquidacionDePrueba";
import { obtenerGarantiaPersonal, obtenerGarantiaReal } from "@/api/credito";
import { obtenerExtractoColocacion } from "@/api/extractocolocacion";
import {
  obtenerListaTipoIdentificacion,
  obtenerListaTipoEstadoColocacion,
  obtenerListaTipoDireccion,
  obtenerListaMunicipios,
  obtenerListaAsesores,
} from "@/api/tipos";
import {
  buscarCreditoPorEstado,
  buscarCreditoPorDocumento,
  buscarDireccionPersona,
  buscarControlCobro,
} from "@/api/controlcobro";
import { obtenerPersona } from "@/api/persona";

export default {
  components: {
    "buscar-por-nombre": BuscarPersonaComponent,
    "control-cobro": ControlCobroComponent,
    "extracto-colocacion": ExtractoColocacionComponent,
    garantias: GarantiaCreditoComponent,
    direccion: DireccionPersonaComponent,
    "liquidacion-prueba": LiquidacionDePruebaComponent,
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
        id_colocacion: "00000000000",
      },
      controlcobro: {
        fecha_observacion: new Date(),
        es_compromiso: false,
        fecha_compromiso: null,
        observacion: null,
        es_observacion: false,
      },
      nombre: null,
      nombrecompleto: "",
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
      personal: [],
      real: [],
      pignoracion: [],
      showDireccionDlg: false,
      showBuscarPersonaDlg: false,
      showControlCobroDlg: false,
      showExtractoDlg: false,
      showGarantiaDlg: false,
      showLiquidacionDePruebaDlg: false,
      loader: null,
    };
  },
  beforeMount() {
    obtenerListaTipoIdentificacion()
      .then((response) => {
        this.tipo_documento = response.data;
        obtenerListaTipoEstadoColocacion()
          .then((response) => {
            this.tipos_estado_colocacion = response.data;
            this.tipos_estado_colocacion.unshift({
              id: -1,
              descripcion: "TODOS CON SALDO",
            });
            obtenerListaTipoDireccion()
              .then((response) => {
                this.tiposdireccion = response.data;
                obtenerListaMunicipios()
                  .then((response) => {
                    this.municipios = response.data;
                    obtenerListaAsesores()
                      .then((response) => {
                        this.asesores = response.data;
                        this.asesores.unshift({ id: -1, descripcion: "TODOS" });
                      })
                      .catch((error) => {
                        console.log(
                          "Error Consultando Lista Asesores: " + error
                        );
                      });
                  })
                  .catch((error) => {
                    console.log("Error Consultando Lista Municipios: " + error);
                  });
              })
              .catch((error) => {
                console.log("Error Consultando Lista Tipo Direccion: " + error);
              });
          })
          .catch((error) => {
            console.log("Error Consultando Estados de Colocacion: " + error);
          });
      })
      .catch((error) => {
        console.log("Error Consultando Tipos de Identificacion: " + error);
      });
  },
  methods: {
    handleCurrentChange(colocacion) {
      this.colocacion = colocacion;
      buscarControlCobro(this.colocacion.id_colocacion).then((response) => {
        this.dataControlCobro = response.data;
      });
    },
    tableRowClassName({ row, rowIndex }) {
      if (row.estado === "JURIDICO") {
        return "warning-row";
      } else if (row.estado === "SALDADO") {
        return "danger-row";
      }
      return "";
    },
    buscarExtracto(id_colocacion) {
      this.mostrarLoader();
      obtenerExtractoColocacion(id_colocacion)
        .then((response) => {
          this.extracto_colocacion = id_colocacion;
          this.extracto_data = response.data;
          this.showExtractoDlg = true;
          this.ocultarLoader();
        })
        .catch((error) => {
          this.ocultarLoader();
          this.$message.error("Error buscando extracto colocación: " + error);
        });
    },
    buscarLiquidacion(colocacion) {
      this.extracto_colocacion = colocacion.id_colocacion;
      this.colocacion = colocacion;
      this.showLiquidacionDePruebaDlg = true;
    },
    cerrarExtractoEvento() {
      this.extracto_data = [];
      this.extracto_colocacion = null;
      this.showExtractoDlg = false;
    },
    buscarGarantia(id_colocacion) {
      console.log("estoy en garantias");
      this.extracto_colocacion = id_colocacion;
      obtenerGarantiaPersonal(id_colocacion)
        .then((response) => {
          this.personal = response.data;
          obtenerGarantiaReal(id_colocacion)
            .then((response) => {
              this.real = response.data.filter((d) => d.modelo_vehiculo === 0);
              this.pignoracion = response.data.filter(
                (d) => d.modelo_vehiculo !== 0
              );
              this.showGarantiaDlg = true;
            })
            .catch((error) => {
              this.$message.error(
                "Error obteniendo datos garantia real: " + error
              );
            });
        })
        .catch((error) => {
          this.$message.error(
            "Error obteniendo datos garantia personal: " + error
          );
        });
    },
    cerrarGarantiaEvento() {
      this.extracto_colocacion = null;
      this.showGarantiaDlg = false;
    },
    buscarPorEstado() {
      this.loading = true;
      this.limpiarTablas();
      buscarCreditoPorEstado(
        this.id_estado_colocacion,
        this.dias_ini,
        this.dias_fin,
        this.ases_id
      )
        .then((response) => {
          this.dataColocacion = response.data;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    setDatosPersonaDesdeElEvento(data) {
      this.id_identificacion = data.id_identificacion;
      this.id_persona = data.id_persona;
      this.showBuscarPersonaDlg = false;
      this.buscarPersona();
    },
    validarAgregarControlCobro(data) {
      this.showControlCobroDlg = false;
      this.controlcobro = {
        fecha_observacion: new Date(),
        es_compromiso: false,
        fecha_compromiso: null,
        observacion: null,
        es_observacion: false,
      };
      if (data === true) {
        buscarControlCobro(this.colocacion.id_colocacion).then((response) => {
          this.dataControlCobro = response.data;
        });
        this.$notify({
          title: "Exito!",
          message: "Observación Ha Sido Guardada",
          type: "success",
        });
      }
    },
    buscarPorDocumento() {
      this.loading = true;
      this.limpiarTablas();
      buscarCreditoPorDocumento(this.id_identificacion, this.id_persona)
        .then((response) => {
          this.dataColocacion = response.data;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: "Cargando...",
        background: "rgba(0, 0, 0, 0.7)",
      });
      this.limpiarTablas();
      obtenerPersona(this.id_identificacion, this.id_persona)
        .then((response) => {
          loading.close();
          if (response.status === 200) {
            this.nombre =
              response.data.a.nombre +
              " " +
              response.data.a.primer_apellido +
              " " +
              response.data.a.segundo_apellido;
          }
        })
        .catch(() => {
          this.nombre = null;
          loading.close();
          this.$alert("Persona No Existe", "Buscando Persona");
        });
    },
    buscarDireccion(persona) {
      this.nombrecompleto = persona.nombre;
      this.direcciones = [];
      buscarDireccionPersona(persona.id_identificacion, persona.id_persona)
        .then((response) => {
          this.direcciones = response.data;
          this.showDireccionDlg = true;
        })
        .catch(() => {
          this.showDireccionDlg = false;
        });
    },
    cerrarDireccionEvento() {
      this.direcciones = [];
      this.showDireccionDlg = false;
    },
    cerrarLiquidacionDePruebaEvento() {
      this.extracto_colocacion = null;
      this.showLiquidacionDePruebaDlg = false;
    },
    limpiarTablas() {
      this.dataColocacion = [];
      this.dataControlCobro = [];
      this.dataDireccion = [];
      this.colocacion = {
        id_agencia: 1,
        id_colocacion: "00000000000",
      };
    },
    mostrarLoader() {
      this.loader = this.$loading({
        lock: true,
        text: "Cargando",
        background: "rgba(0, 0, 0, 0.7)",
      });
    },
    ocultarLoader() {
      if (this.loader) {
        this.loader.close();
      }
    },
  },
};
</script>
<style>
.el-table .warning-row {
  background: #e6a23c;
}

.el-table .success-row {
  background: #f0f9eb;
}

.el-table .danger-row {
  background: #f56c6c;
}
</style>
