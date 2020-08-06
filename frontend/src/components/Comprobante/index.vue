<template>
  <el-container>
    <el-main>
      <el-row :gutter="4">
        <el-col :span="24">
          <h4>CONTABILIZACION</h4>
        </el-col>
      </el-row>
      <el-form :model="comprobante" label-position="top">
        <el-row :gutter="4">
          <el-col :xs="12" :sm="12" :md="12" :lg="6" :xl="6">
            <template v-if="comprobante.ESTADO === 'O'">
              <el-form-item label="FECHA">
                <el-date-picker
                  v-model="comprobante.FECHADIA"
                  type="date"
                  placeholder="Seleccione la Fecha del Comprobante"
                  style="width: 100%;"
                ></el-date-picker>
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item label="Fecha">
                <span>{{ comprobante.FECHADIA | moment('YYYY-MM-DD')}}</span>
              </el-form-item>
            </template>
          </el-col>
          <el-col :xs="24" :sm="12" :md="12" :lg="6" :xl="6">
            <template v-if="comprobante.ESTADO === 'O' && nuevo === true">
              <el-form-item label="Tipo de Comprobante">
                <el-select
                  v-model="comprobante.TIPO_COMPROBANTE"
                  filterable
                  placeholder="Seleccione el Tipo de Comprobante"
                  @change="showId(comprobante.TIPO_COMPROBANTE)"
                  style="width: 90%;"
                >
                  <el-option
                    v-for="tipo in tipos_comprobante"
                    :key="tipo.ID"
                    :label="tipo.DESCRIPCION"
                    :value="tipo.ID"
                  ></el-option>
                </el-select>
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item label="Tipo de Comprobante">
                <span>{{ tipo(comprobante.TIPO_COMPROBANTE) }}</span>
              </el-form-item>
            </template>
          </el-col>
          <el-col :xs="24" :sm="12" :md="12" :lg="6" :xl="6">
            <template>
              <el-form-item label="Número Comprobante">
                <span
                  style="font-size: 24px; font-weigth: bold;"
                >{{ comprobante.ID_COMPROBANTE | pad(6) }}</span>
              </el-form-item>
            </template>
          </el-col>
          <el-col :xs="24" :sm="12" :md="12" :lg="6" :xl="6">
            <template>
              <el-form-item label="Estado Actual">
                <span style="font-size: 24px; font-weigth: bold;">{{ estado(comprobante.ESTADO) }}</span>
              </el-form-item>
            </template>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :span="24">
            <template v-if="comprobante.ESTADO === 'O'">
              <el-form-item label="DESCRIPCION">
                <el-input type="textarea" autosize v-model="comprobante.DESCRIPCION" />
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item label="DESCRIPCION">
                <span>{{ comprobante.DESCRIPCION }}</span>
              </el-form-item>
            </template>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <template v-if="comprobante.ESTADO === 'O' && anular">
              <el-form-item label="ANULADO POR">
                <el-input type="textarea" autosize v-model="comprobante.ANULACION" />
              </el-form-item>
            </template>
            <template v-else-if="comprobante.ESTADO === 'N'">
              <el-form-item label="ANULADO POR">
                <span>{{ comprobante.DESCRIPCION }}</span>
              </el-form-item>
            </template>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-alert
              v-if="comprobante.DEBITO === comprobante.CREDITO"
              title="Comprobante Cuadrado"
              type="success"
              description="el comprobante presenta sumas iguales DEBITO CREDITO"
              show-icon
            ></el-alert>
            <el-alert
              v-else
              title="Comprobante Descuadrado"
              type="warning"
              description="el comprobante presenta sumas desiguales DEBITO CREDITO"
              show-icon
            ></el-alert>
          </el-col>
        </el-row>
        <el-row :gutter="4" v-if="comprobante.ESTADO === 'O'">
          <el-col :span="24">
            <el-form :model="aux">
              <el-row :gutter="4">
                <el-col :xs="22" :sm="22" :md="7" :lg="6" :xl="6">
                  <el-form-item label="CODIGO">
                    <el-input
                      v-if="comprobante.ESTADO === 'O'"
                      ref="codigo"
                      v-model="aux.CODIGO"
                      @blur="validarCodigo"
                    />
                    <span v-else>{{ aux.CODIGO }}</span>
                  </el-form-item>
                </el-col>
                <el-col :xs="2" :sm="2" :md="2" :lg="2" :xl="2">
                  <el-form-item label="_">
                    <el-popover placement="right" width="700" trigger="click" v-model="puc_visible">
                      <puctree @selected="colocarcodigo" />
                      <el-button slot="reference" mini circle icon="el-icon-search"></el-button>
                    </el-popover>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="15" :lg="16" :xl="16">
                  <el-form-item label="CUENTA">
                    <el-input readonly v-model="aux.CUENTA" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :guter="4">
                <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                  <el-form-item label="CENTRO DE COSTO">
                    <el-select
                      v-if="comprobante.ESTADO === 'O'"
                      v-model="aux.ID_AGENCIA"
                      filterable
                    >
                      <el-option
                        v-for="c in centros"
                        :key="c.ID_AGENCIA"
                        :label="c.DESCRIPCION_AGENCIA"
                        :value="c.ID_AGENCIA"
                      />
                    </el-select>
                    <span v-else>{{ centro(aux.ID_AGENCIA) }}</span>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16">
                  <el-form-item label="TIPO DE OPERACION">
                    <el-select
                      v-if="comprobante.ESTADO === 'O'"
                      v-model="aux.ID_CLASE_OPERACION"
                      clearable
                      filterable
                      style="width: 100%;"
                    >
                      <el-option
                        v-for="t in tipos_operacion"
                        :key="t.ID_CLASE_OPERACION"
                        :label="t.DESCRIPCION_CLASE_OPERACION"
                        :value="t.ID_CLASE_OPERACION"
                      />
                    </el-select>
                    <span v-else>{{ operacion(aux.ID_CLASE_OPERACION) }}</span>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="4">
                <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                  <el-form-item label="Detalle del Movimiento">
                    <el-input
                      v-if="comprobante.ESTADO === 'O'"
                      v-model="aux.DETALLE"
                      type="textarea"
                      maxlength="200"
                      rows="4"
                      show-word-limit
                    />
                    <span v-else>{{ aux.DETALLE }}</span>
                  </el-form-item>
                </el-col>
                <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                  <el-row :gutter="4">
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item prop="id_identificacion" label="Tipo de Documento">
                        <el-select v-model="aux.ID_IDENTIFICACION" filterable>
                          <el-option
                            v-for="t in tipos_documento"
                            :key="t.id_identificacion"
                            :label="t.descripcion_identificacion"
                            :value="t.id_identificacion"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item prop="id_persona" label="Número de Documento">
                        <el-input v-model="aux.ID_PERSONA" @blur="buscarPersona" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :xs="24" :sm="24" :md="22" :lg="22" :xl="22">
                      <el-form-item label="Nombre">
                        <el-input v-model="aux.PERSONA" readonly />
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
                </el-col>
              </el-row>
              <el-row :gutter="4">
                <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                  <el-form-item label="Valor Débito">
                    <el-input
                      v-if="comprobante.ESTADO === 'O'"
                      ref="debito"
                      v-model="aux.DEBITO"
                      v-currency="{locale: 'en', currency: 'USD', valueAsInteger: false, distractionFree: true, precision: 2, autoDecimalMode: false,  valueRange: { min: 0 }, allowNegative: false}"
                      @blur="aux.DEBITO > 0 ? aux.CREDITO = 0: aux.DEBITO = 0"
                      style="align: right;"
                    />
                    <span v-else>{{ aux.DEBITO | currency_2 }}</span>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                  <el-row :gutter="4">
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item label="Monto">
                        <el-input
                          v-if="comprobante.ESTADO ==='O'"
                          v-model="aux.MONTO_RETENCION"
                          v-currency="{locale: 'en', currency: 'USD', valueAsInteger: false, distractionFree: true, precision: 2, autoDecimalMode: false,  valueRange: { min: 0 }, allowNegative: false}"
                        />
                        <span v-else>{{ aux.MONTO_RETENCION }}</span>
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item label="Tasa (%)">
                        <el-input v-if="comprobante.ESTADO ==='O'" v-model="aux.TASA_RETENCION" />
                        <span v-else>{{ tasaValue }}</span>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
              <el-row :gutter="4">
                <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                  <el-form-item label="Valor Crébito">
                    <el-input
                      v-if="comprobante.ESTADO === 'O'"
                      ref="credito"
                      v-model="aux.CREDITO"
                      v-currency="{locale: 'en', currency: 'USD', valueAsInteger: false, distractionFree: true, precision: 2, autoDecimalMode: false,  valueRange: { min: 0 }, allowNegative: false}"
                      @blur="aux.CREDITO > 0 ? aux.DEBITO = 0: aux.CREDITO = 0"
                    />
                    <span v-else>{{ aux.CREDITO | currency_2 }}</span>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                  <el-row>
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item v-if="cuenta.ESBANCO > 0" label="Cheque">
                        <el-input v-if="comprobante.ESTADO === 'O'" />
                        <span v-else>{{ aux.CHEQUE }}</span>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
            </el-form>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :span="24">
            <el-input v-model="filtro" size="mini" placeholder="Digite para Buscar" />
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-table
              :data="auxiliar.filter(a => !filtro || (a.CODIGO.includes(filtro.toUpperCase()) || a.CUENTA.includes(filtro.toUpperCase()) || a.DEBITO.toString().includes(filtro.toUpperCase()) || a.CREDITO.toString().includes(filtro.toUpperCase()) || a.ID_PERSONA.includes(filtro.toUpperCase()) || a.PERSONA.includes(filtro.toUpperCase())))"
              :summary-method="getSummaries"
              highlight-current-row
              @current-change="handleCurrentChange"
              show-summary
              max-height="600"
              stripe
              style="font-size: 14px; width: 100%; cursor: pointer;"
            >
              <el-table-column prop="CODIGO" label="Código" width="180">
                <template slot-scope="scope">
                  <span>{{ scope.row.CODIGO }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="CUENTA" label="Cuenta" width="400">
                <template slot-scope="scope">
                  <span>{{ scope.row.CUENTA | fm_truncate(50)}}</span>
                </template>
              </el-table-column>
              <el-table-column prop="DEBITO" label="V.Débito" width="120" align="right">
                <template slot-scope="scope">
                  <span>{{ scope.row.DEBITO | currency_2 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="CREDITO" label="V.Crédito" width="120" align="right">
                <template slot-scope="scope">
                  <span>{{ scope.row.CREDITO | currency_2 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="ID_PERSONA" label="Documento" width="100">
                <template slot-scope="scope">
                  <span>{{ scope.row.ID_PERSONA }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="PERSONA" label="Persona" width="220">
                <template slot-scope="scope">
                  <span :title="scope.row.PERSONA">{{ scope.row.PERSONA | fm_truncate(20) }}</span>
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="Ver" width="140">
                <template slot-scope="scope">
                  <el-dropdown :split-button="true" size="mini" type="primary" trigger="click">
                    Tareas
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item>
                        <div @click="handleDelete(scope.row)">
                          <i class="el-icon-view" title="Quitar Item" />Quitar
                        </div>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-form>
    </el-main>
    <!-- Dialogo Buscar por Apellidos y Nombre -->
    <el-dialog
      :visible.sync="showBuscarPersonaDlg"
      :destroy-on-close="true"
      append-to-body
      title="Buscar Por Apellidos y Nombre"
      width="80%"
    >
      <buscar-por-nombre @selectPersonEvent="setDatosPersonaDesdeElEvento" />
    </el-dialog>
  </el-container>
</template>
<script>
import BuscarPersonaComponent from "@/components/BuscarPersonaNombre";
import PucTree from "@/components/PucTree";
import { CurrencyDirective } from "vue-currency-input";
import { PercentDirective } from "vue-percent-input";

import { esDeMovimiento } from "@/api2/puc";

import {
  obtenerTiposComprobante,
  obtenerCentro,
  obtenerTipoOperacion,
  obtenerComprobante,
  obtenerAuxiliar,
  guardarComprobante,
} from "@/api2/contabilidad";

import { obtenerListaTipoIdentificacion } from "@/api/tipos";
import { obtenerPersona } from "@/api/persona";

export default {
  directives: {
    currency: CurrencyDirective,
    percent: PercentDirective,
  },
  props: {
    id_comprobante: {
      type: Number,
      required: true,
    },
    tipo_comprobante: {
      type: Number,
      required: true,
    },
  },
  components: {
    puctree: PucTree,
    "buscar-por-nombre": BuscarPersonaComponent,
  },
  computed: {},
  data() {
    return {
      showBuscarPersonaDlg: false,
      tipos_comprobante: [],
      tipos_operacion: [],
      tipos_documento: [],
      centros: [],
      comprobante: {},
      aux: null,
      ext: null,
      nuevo: true,
      auxiliar: [],
      filtro: null,
      puc_visible: false,
      cuenta: {
        CODIGO: null,
        ID_AGENCIA: null,
        CLAVE: null,
        NOMBRE: null,
        TIPO: null,
        CODIGO_MAYOR: null,
        MOVIMIENTO: null,
        ESBANCO: null,
        INFORME: null,
        NIVEL: null,
        NATURALEZA: null,
        CENTROCOSTO: null,
        SALDOINICIAL: null,
        ESCAPTACION: null,
        ESCOLOCACION: null
      },
      anular: false
    };
  },
  beforeMount() {
    this.comprobante = this.comprobanteVacio();
    this.aux = this.auxiliarVacio();
    this.listaTipoComprobante();
  },
  methods: {
    getSummaries(param) {
      const { columns, data } = param;
      const sums = [];
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = "Totales";
          return;
        }
        if (column.property !== "DEBITO" && column.property !== "CREDITO") {
          return;
        }
        const values = data.map((item) => Number(item[column.property]));
        if (!values.every((value) => isNaN(value))) {
          var valor = this.$options.filters.currency_2(
            values.reduce((prev, curr) => {
              const value = Number(curr);
              if (!isNaN(value)) {
                return prev + curr;
              } else {
                return prev;
              }
            }, 0)
          );
          if (column.property === "DEBITO") {
            this.comprobante.TOTAL_DEBITO = valor;
          } else {
            this.comprobante.TOTAL_CREDITO = valor;
          }
          sums[index] = "$ " + valor;
        } else {
          sums[index] = "N/A";
        }
      });
      return sums;
    },
    handleCurrentChange(val) {
      this.aux = val;
      this.$refs["debito"].blur();
      this.$refs["credito"].blur();
    },
    showId(id) {
      console.log("id: '" + id + "'");
    },
    handleDelete(aux) {
      this.comprobante.AUXS.splice(aux, 1);
    },
    comprobanteVacio() {
      return {
        ID_COMPROBANTE: null,
        ID_AGENCIA: null,
        TIPO_COMPROBANTE: null,
        FECHADIA: new Date(),
        DESCRIPCION: null,
        TOTAL_DEBITO: null,
        TOTAL_CREDITO: null,
        ESTADO: "O",
        IMPRESO: false,
        ANULACION: null,
        ID_EMPLEADO: null,
      };
    },
    auxiliarVacio() {
      return {
        ID_AGENCIA: 1,
        FECHA: new Date(),
        CODIGO: null,
        CUENTA: null,
        DEBITO: null,
        CREDITO: null,
        ID_CUENTA: null,
        ID_COLOCACION: null,
        ID_IDENTIFICACION: null,
        ID_PERSONA: null,
        PERSONA: null,
        MONTO_RETENCION: null,
        TASA_RETENCION: "",
        ESTADOAUX: null,
        ID_CLASE_OPERACION: null,
        TIPO_COMPROBANTE: null,
        ID_COMPROBANTE: null,
        ID: null,
        DETALLE: null,
        CHEQUE: null,
      };
    },
    tipo(id) {
      if (id === undefined || id === null) {
        return "SIN ID";
      } else {
        var _id = id;
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
        return "";
      } else {
        if (id === "O") {
          return "ABIERTO";
        } else if (id === "C") {
          return "CERRADO";
        } else if (id === "N") {
          return "ANULADO";
        }
      }
    },
    centro(id) {
      if (id === undefined || id === null) {
        return "SIN ID";
      } else {
        var _id = id;
        var centro = this.centros.find((o) => o.ID_AGENCIA === _id);
        if (centro) {
          return centro.DESCRIPCION_AGENCIA;
        } else {
          return "NO DEFINIDO";
        }
      }
    },
    operacion(id) {
      if (id === undefined || id === null) {
        return "SIN ID";
      } else {
        var _id = id;
        var op = this.centros.find((o) => o.ID_CLASE_OPERACION === _id);
        if (op) {
          return op.DESCRIPCION_CLASE_OPERACION;
        } else {
          return "NO DEFINIDO";
        }
      }
    },
    guardar() {
      guardarComprobante(this.comprobante).then(() => {});
    },
    listaTipoComprobante() {
      obtenerTiposComprobante()
        .then((response) => {
          this.tipos_comprobante = response.data;
          this.listaTipoOperacion();
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los tipos de comprobantes: " + error,
            type: "error",
          });
        });
    },
    listaTipoOperacion() {
      obtenerTipoOperacion()
        .then((response) => {
          this.tipos_operacion = response.data;
          this.listaCentro();
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los tipos de operación: " + error,
            type: "error",
          });
        });
    },
    listaCentro() {
      obtenerCentro()
        .then((response) => {
          this.centros = response.data;
          this.listaTipoDocumento();
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los centros de costo: " + error,
            type: "error",
          });
        });
    },
    listaTipoDocumento() {
      obtenerListaTipoIdentificacion()
        .then((response) => {
          this.tipos_documento = response.data;
          this.buscarComprobante();
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los tipos de documento: " + error,
            type: "error",
          });
        });
    },
    buscarComprobante() {
      this.comprobante.TIPO_COMPROBANTE = parseInt(this.$route.params.tp, 10);
      this.comprobante.ID_COMPROBANTE = parseInt(this.$route.params.id, 10);
      if (
        this.comprobante.TIPO_COMPROBANTE &&
        this.comprobante.ID_COMPROBANTE
      ) {
        this.nuevo = false;
        obtenerComprobante(this.$route.params.tp, this.$route.params.id)
          .then((response) => {
            this.comprobante = response.data[0];
            obtenerAuxiliar(this.$route.params.tp, this.$route.params.id)
              .then((response) => {
                this.auxiliar = response.data;
                this.comprobante.AUXS = this.auxiliar;
              })
              .catch((error) => {
                this.$message({
                  message:
                    "Error al buscar los auxiliares del comprobante: " + error,
                  type: "error",
                });
              });
          })
          .catch((error) => {
            this.$message({
              message: "Error al buscar el comprobante: " + error,
              type: "error",
            });
          });
      } else {
        this.nuevo = true;
        this.compro;
      }
    },
    setDatosPersonaDesdeElEvento(data) {
      this.aux.ID_IDENTIFICACION = data.id_identificacion;
      this.aux.ID_PERSONA = data.id_persona;
      this.showBuscarPersonaDlg = false;
      this.buscarPersona();
    },
    limpiarTablas() {
      this.aux.PERSONA = null;
    },
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: "Cargando...",
        background: "rgba(0, 0, 0, 0.7)",
      });
      this.limpiarTablas();
      obtenerPersona(this.aux.ID_IDENTIFICACION, this.aux.ID_PERSONA)
        .then((response) => {
          loading.close();
          if (response.status === 200) {
            this.aux.PERSONA =
              response.data.a.nombre +
              " " +
              response.data.a.primer_apellido +
              " " +
              response.data.a.segundo_apellido;
          }
        })
        .catch(() => {
          this.aux.PERSONA = null;
          loading.close();
          this.$alert("Persona No Existe", "Buscando Persona");
        });
    },
    colocarcodigo(codigo) {
      this.puc_visible = false;
      this.aux.CODIGO = codigo;
      this.validarCodigo();
    },
    validarCodigo() {
      if (this.aux.CODIGO.length < 18) {
        this.aux.CODIGO =
          this.aux.CODIGO +
          "000000000000000000".substring(0, 18 - this.aux.CODIGO.length);
      }
      esDeMovimiento(this.aux.CODIGO)
        .then((response) => {
          if (response.data.movimiento) {
            this.cuenta = response.data.cuenta;
            this.aux.CUENTA = response.data.cuenta.NOMBRE;
          } else {
            this.$refs["codigo"].focus();
            this.$message({
              message:
                "Cuenta PUC " +
                response.data.cuenta.NOMBRE +
                " con Código: " +
                response.data.cuenta.CODIGO +
                ", no es de movimiento",
              type: "error",
            });
          }
        })
        .catch(() => {
          this.$message({
            message:
              "Cuenta PUC con Código: " + this.aux.CODIGO + ", no existe",
            type: "error",
          });
        });
    },
  },
};
</script>
<style lang="css" scoped>
.el-table .cell {
  line-height: 15px !important;
}
</style>