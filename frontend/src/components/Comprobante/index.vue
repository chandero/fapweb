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
                <span>{{ comprobante.FECHADIA | moment("YYYY-MM-DD") }}</span>
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
                <span style="font-size: 24px; font-weigth: bold;">
                  {{
                  comprobante.ID_COMPROBANTE | pad(6)
                  }}
                </span>
              </el-form-item>
            </template>
          </el-col>
          <el-col :xs="24" :sm="12" :md="12" :lg="6" :xl="6">
            <template>
              <el-form-item label="Estado Actual">
                <span style="font-size: 24px; font-weigth: bold;">
                  {{
                  estado(comprobante.ESTADO)
                  }}
                </span>
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
            <template v-if="comprobante.ESTADO === 'N'">
              <el-form-item label="ANULADO POR">
                <span>{{ comprobante.DESCRIPCION }}</span>
              </el-form-item>
            </template>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
            <span style="font-size: 20px;">Total Débito:${{ totaldebito | currency_2 }}</span>
          </el-col>
          <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
            <span style="font-size: 20px;">Total Crébito:${{ totalcredito | currency_2 }}</span>
          </el-col>
          <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
            <span style="font-size: 20px;">Diferencia:${{ totaldebito - totalcredito | currency_2 }}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-alert
              v-if="totaldebito === totalcredito"
              title="Comprobante Cuadrado"
              type="success"
              description="el comprobante presenta sumas iguales DEBITO CREDITO"
              show-icon
              :closable="false"
            ></el-alert>
            <el-alert
              v-else
              title="Comprobante Descuadrado"
              type="warning"
              description="el comprobante presenta sumas desiguales DEBITO CREDITO"
              show-icon
              :closable="false"
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
                    <span>{{ aux.CUENTA }}</span>
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
                      @blur="!aux.DETALLE || aux.DETALLE.length === 0? aux.DETALLE = comprobante.DESCRIPCION: aux.DETALLE"
                    />
                    <span v-else>{{ aux.DETALLE }}</span>
                  </el-form-item>
                </el-col>
                <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                  <el-row :gutter="4">
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item
                        v-if="informe.PIDOTIPOID === 1"
                        prop="id_identificacion"
                        label="Tipo de Documento"
                      >
                        <el-select
                          v-if="comprobante.ESTADO === 'O'"
                          v-model="aux.ID_IDENTIFICACION"
                          filterable
                        >
                          <el-option
                            v-for="t in tipos_documento"
                            :key="t.id_identificacion"
                            :label="t.descripcion_identificacion"
                            :value="t.id_identificacion"
                          />
                        </el-select>
                        <span v-else>
                          {{
                          tipo_documento(aux.ID_IDENTIFICACION)
                          }}
                        </span>
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item
                        v-if="informe.PIDOID === 1"
                        prop="id_persona"
                        label="Número de Documento"
                      >
                        <el-input
                          v-if="comprobante.ESTADO === 'O'"
                          v-model="aux.ID_PERSONA"
                          @blur="buscarPersona"
                        />
                        <span v-else>{{ aux.ID_PERSONA }}</span>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :xs="24" :sm="24" :md="22" :lg="22" :xl="22">
                      <el-form-item
                        v-if="
                          comprobante.ESTADO === 'O' && informe.PIDOID === 1
                        "
                        label="Nombre"
                      >
                        <span>{{ aux.PERSONA }}</span>
                        <el-button
                          type="primary"
                          icon="el-icon-search"
                          circle
                          title="Buscar Por Apellidos y Nombre"
                          @click="showBuscarPersonaDlg = true"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
              <el-row :gutter="4">
                <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                  <el-form-item label="Valor Débito" style="text-align:right;">
                    <currency-input
                      v-if="comprobante.ESTADO === 'O'"
                      ref="debito"
                      v-model="aux.DEBITO"
                      locale="en"
                      @blur="
                        aux.DEBITO > 0 ? (aux.CREDITO = 0) : (aux.DEBITO = 0)
                      "
                      style="text-align:right;"
                    />
                    <span v-else class="toright">{{ aux.DEBITO | currency_2 }}</span>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                  <el-row :gutter="4">
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item v-if="informe.PIDOMONTO === 1" label="Monto">
                        <currency-input
                          v-if="
                            comprobante.ESTADO === 'O' &&
                              informe.PIDOMONTO === 1
                          "
                          v-model="aux.MONTO_RETENCION"
                          locale="en"
                          style="text-align:right;"
                        />
                        <span v-else>{{ aux.MONTO_RETENCION }}</span>
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item v-if="informe.PIDOTASA === 1" label="Tasa (%)">
                        <percent-input
                          v-if="comprobante.ESTADO === 'O'"
                          v-model="aux.TASA_RETENCION"
                          style="text-align:right;"
                        />
                        <span v-else>{{ tasaValue }}</span>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
              <el-row :gutter="4">
                <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                  <el-form-item label="Valor Crédito" style="text-align:right;">
                    <currency-input
                      v-if="comprobante.ESTADO === 'O'"
                      ref="credito"
                      v-model="aux.CREDITO"
                      locale="en"
                      @blur="
                        aux.CREDITO > 0 ? (aux.DEBITO = 0) : (aux.CREDITO = 0)
                      "
                      style="text-align:right;"
                    />
                    <span v-else style="text-align:right;">{{ aux.CREDITO | currency_2 }}</span>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                  <el-row>
                    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                      <el-form-item v-if="cuenta.ESBANCO > 0" label="Cheque">
                        <el-input v-if="comprobante.ESTADO === 'O'" v-model="aux.CHEQUE" />
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
          <el-col :xs="24" :sm="7" :md="5" :lg="3" :xl="3">
            <el-button
              :disabled="!aux.CODIGO || (aux.DEBITO == 0 && aux.CREDITO == 0)"
              type="primary"
              mini
              @click="handleAdd"
            >
              <i>
                <svg-icon icon-class="item-plus" />
              </i> Agregar
            </el-button>
          </el-col>
          <el-col :xs="24" :sm="7" :md="5" :lg="3" :xl="3">
            <el-button
              :disabled="!aux.CODIGO || (aux.DEBITO == 0 && aux.CREDITO == 0)"
              type="info"
              mini
              @click="handleUpdate"
            >
              <i>
                <svg-icon icon-class="item-update" />
              </i> Modificar
            </el-button>
          </el-col>
          <el-col :xs="24" :sm="7" :md="5" :lg="3" :xl="3">
            <el-button
              :disabled="!aux.CODIGO || (aux.DEBITO == 0 && aux.CREDITO == 0)"
              type="warning"
              mini
              @click="handleClear"
            >
              <i>
                <svg-icon icon-class="item-clear" />
              </i> Limpiar
            </el-button>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :span="24">
            <el-input ref="buscar" v-model="filtro" size="mini" placeholder="Digite para Buscar" />
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-table
              ref="tabla"
              :data="
                auxiliares.filter(
                  a =>
                    !filtro ||
                    a.CODIGO.includes(filtro.toUpperCase()) ||
                      a.CUENTA.includes(filtro.toUpperCase()) ||
                      a.DEBITO.toString().includes(filtro.toUpperCase()) ||
                      a.CREDITO.toString().includes(filtro.toUpperCase()) ||
                      a.ID_PERSONA.includes(filtro.toUpperCase()) ||
                      a.PERSONA.includes(filtro.toUpperCase())
                )
              "
              highlight-current-row
              show-summary
              :summary-method="getSummaries"
              @current-change="handleCurrentChange"
              max-height="600"
              stripe
              style="font-size: 14px; width: 100%; cursor: pointer;"
            >
              <el-table-column type="expand">
                <template slot-scope="props">
                  <p>Detalle: {{ props.row.DETALLE }}</p>
                  <p>Documento: {{ props.row.ID_PERSONA }}</p>
                  <p>Persona: {{ props.row.PERSONA }}</p>
                  <p>Monto: {{ props.row.address }}</p>
                  <p>Tasa: {{ props.row.zip }}</p>
                  <p>Colocacion: {{ props.row.ID_COLOCACION}}</p>
                  <p>Cuenta: {{ props.row.ID_CUENTA }}</p>
                  <p>Operacion: {{ operacion(props.row.ID_CLASE_OPERACION) }}</p>
                  <p>Cheque: {{ props.row.CHEQUE }}</p>
                </template>
              </el-table-column>
              <el-table-column prop="CODIGO" label="Código" width="180">
                <template slot-scope="scope">
                  <span>{{ scope.row.CODIGO }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="CUENTA" label="Cuenta" width="400">
                <template slot-scope="scope">
                  <span>{{ scope.row.CUENTA | fm_truncate(50) }}</span>
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
                  <span :title="scope.row.PERSONA">
                    {{
                    scope.row.PERSONA | fm_truncate(20)
                    }}
                  </span>
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
        <el-row :gutter="4">
          <el-col :xs="12" :sm="12" :md="4" :lg="4" :xl="4">
            <el-button
              :disabled="totaldebito !== totalcredito"
              type="primary"
              mini
              @click="handleSave"
            >
              <i>
                <svg-icon icon-class="item-save" />
              </i> Guardar
            </el-button>
          </el-col>
          <el-col :xs="12" :sm="12" :md="4" :lg="4" :xl="4">
            <el-button
              :disabled="totaldebito !== totalcredito"
              type="warning"
              mini
              @click="handleNullify"
            >
              <i>
                <svg-icon icon-class="item-remove" />
              </i> Anular
            </el-button>
          </el-col>
          <el-col :xs="12" :sm="12" :md="4" :lg="4" :xl="4">
            <el-button :disabled="totaldebito !== totalcredito" type="info" mini @click="handlePdf">
              <i>
                <svg-icon icon-class="pdf" />
              </i> Imprimir
            </el-button>
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
    <el-dialog title="Confirmación" :visible.sync="showGuardarDlg" width="50%">
      <span>Seguro de guardar los cambios?</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showGuardarDlg = false">No</el-button>
        <el-button type="primary" @click="guardar">Sí</el-button>
      </span>
    </el-dialog>
    <el-dialog title="Anular Comprobante" :visible.sync="showAnularDlg">
      <el-form>
        <el-form-item label="Mótivo Anulación">
          <el-input type="textarea" v-model="comprobante.ANULACION"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="comprobante.ANULACION = null; showAnularDlg = false">Cancelar</el-button>
        <el-button type="primary" @click="anular">Anular</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>
<script>
import SvgIcon from "@/components/SvgIcon";
import BuscarPersonaComponent from "@/components/BuscarPersonaNombre";
import PucTree from "@/components/PucTree";
import { CurrencyDirective } from "vue-currency-input";
import { PercentDirective } from "vue-percent-input";

import { esDeMovimiento } from "@/api2/puc";

import {
  obtenerTiposComprobante,
  obtenerCentro,
  obtenerTipoOperacion,
  obtenerInforme,
  obtenerCodigo,
  obtenerComprobante,
  obtenerAuxiliar,
  guardarComprobante,
  anularComprobante,
  obtenerNotaPdf,
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
    "svg-icon": SvgIcon,
    puctree: PucTree,
    "buscar-por-nombre": BuscarPersonaComponent,
  },
  computed: {},
  data() {
    return {
      showBuscarPersonaDlg: false,
      showGuardarDlg: false,
      showAnularDlg: false,
      tipos_comprobante: [],
      tipos_operacion: [],
      tipos_documento: [],
      centros: [],
      informes: [],
      comprobante: {
        ID_COMPROBANTE: 0,
      },
      csc: 0,
      aux: null,
      lastaux: null,
      nuevo: true,
      auxiliares: [],
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
        ESCOLOCACION: null,
      },
      informe: null,
      totaldebito: 0,
      totalcredito: 0,
    };
  },
  watch: {
    auxiliares: {
      deep: true,
      handler() {
        this.totaldebito = 0;
        this.totalcredito = 0;
        this.auxiliares.forEach((a) => {
          this.totaldebito += a.DEBITO;
          this.totalcredito += a.CREDITO;
        });
      },
    },
  },
  beforeMount() {
    this.comprobante = this.comprobanteVacio();
    this.aux = this.auxiliarVacio();
    this.informe = this.informeVacio();
    this.listaTipoComprobante();
  },
  methods: {
    getSummaries(param) {
      const { columns, data } = param;
      const sums = [];
      columns.forEach((column, index) => {
        if (index === 1) {
          sums[index] = "Totales";
          return;
        }
        if (column.property !== "DEBITO" && column.property !== "CREDITO") {
          return;
        }
        const values = data.map((item) => Number(item[column.property]));
        if (!values.every((value) => isNaN(value))) {
          var valor = values.reduce((prev, curr) => {
            const value = Number(curr);
            if (!isNaN(value)) {
              return prev + curr;
            } else {
              return prev;
            }
          }, 0);
          if (column.property === "DEBITO") {
            this.comprobante.TOTAL_DEBITO = valor;
          } else {
            this.comprobante.TOTAL_CREDITO = valor;
          }
          const moneda = this.$options.filters.currency_2(valor);
          sums[index] = "$ " + moneda;
        } else {
          sums[index] = "N/A";
        }
      });
      return sums;
    },
    handleCurrentChange(val) {
      if (val) {
        this.setAuxiliar(val);
        obtenerCodigo(this.aux.CODIGO)
          .then((response) => {
            this.cuenta = response.data[0];
            this.informe = this.tipo_informe(this.cuenta.INFORME);
          })
          .catch((error) => {
            this.$message({
              message: "Error al consultar código contable: " + error,
              type: "error",
            });
          });
      }
    },
    handleAdd() {
      this.csc += 1;
      this.aux.CSC = this.csc;
      var lastaux = {
        CSC: this.aux.CSC,
        ID_AGENCIA: this.aux.ID_AGENCIA,
        FECHA: this.aux.FECHA,
        CODIGO: this.aux.CODIGO,
        CUENTA: this.aux.CUENTA,
        DEBITO: this.aux.DEBITO,
        CREDITO: this.aux.CREDITO,
        ID_CUENTA: this.aux.ID_CUENTA,
        ID_COLOCACION: this.aux.ID_COLOCACION,
        ID_IDENTIFICACION: this.aux.ID_IDENTIFICACION,
        ID_PERSONA: this.aux.ID_PERSONA,
        PERSONA: this.aux.PERSONA,
        MONTO_RETENCION: this.aux.MONTO_RETENCION,
        TASA_RETENCION: this.aux.TASA_RETENCION,
        ESTADOAUX: this.aux.ESTADOAUX,
        ID_CLASE_OPERACION: this.aux.ID_CLASE_OPERACION,
        TIPO_COMPROBANTE: this.aux.TIPO_COMPROBANTE,
        ID_COMPROBANTE: this.aux.ID_COMPROBANTE,
        ID: this.aux.ID,
        DETALLE: this.aux.DETALLE,
        CHEQUE: this.aux.CHEQUE,
      };
      this.auxiliares.push(lastaux);
      this.aux = this.auxiliarVacio();
      this.$refs["codigo"].focus();
    },
    handleUpdate() {
      this.lastaux.CSC = this.aux.CSC;
      this.lastaux.ID_AGENCIA = this.aux.ID_AGENCIA;
      this.lastaux.FECHA = this.aux.FECHA;
      this.lastaux.CODIGO = this.aux.CODIGO;
      this.lastaux.CUENTA = this.aux.CUENTA;
      this.lastaux.DEBITO = this.aux.DEBITO;
      this.lastaux.CREDITO = this.aux.CREDITO;
      this.lastaux.ID_CUENTA = this.aux.ID_CUENTA;
      this.lastaux.ID_COLOCACION = this.aux.ID_COLOCACION;
      this.lastaux.ID_IDENTIFICACION = this.aux.ID_IDENTIFICACION;
      this.lastaux.ID_PERSONA = this.aux.ID_PERSONA;
      this.lastaux.PERSONA = this.aux.PERSONA;
      this.lastaux.MONTO_RETENCION = this.aux.MONTO_RETENCION;
      this.lastaux.TASA_RETENCION = this.aux.TASA_RETENCION;
      this.lastaux.ESTADOAUX = this.aux.ESTADOAUX;
      this.lastaux.ID_CLASE_OPERACION = this.aux.ID_CLASE_OPERACION;
      this.lastaux.TIPO_COMPROBANTE = this.aux.TIPO_COMPROBANTE;
      this.lastaux.ID_COMPROBANTE = this.aux.ID_COMPROBANTE;
      this.lastaux.ID = this.aux.ID;
      this.lastaux.DETALLE = this.aux.DETALLE;
      this.lastaux.CHEQUE = this.aux.CHEQUE;
      var index = this.auxiliares.indexOf(this.aux);
      if (~index) {
        this.auxiliares.splice(index, 1, this.aux);
      }
      /*
      this.auxiliares.forEach((a) => {
        if (a.CSC === this.aux.CSC) {
          a.ID_AGENCIA = this.aux.ID_AGENCIA;
          a.FECHA = this.aux.FECHA;
          a.CODIGO = this.aux.CODIGO;
          a.CUENTA = this.aux.CUENTA;
          a.DEBITO = this.aux.DEBITO;
          a.CREDITO = this.aux.CREDITO;
          a.ID_CUENTA = this.aux.ID_CUENTA;
          a.ID_COLOCACION = this.aux.ID_COLOCACION;
          a.ID_IDENTIFICACION = this.aux.ID_IDENTIFICACION;
          a.ID_PERSONA = this.aux.ID_PERSONA;
          a.PERSONA = this.aux.PERSONA;
          a.MONTO_RETENCION = this.aux.MONTO_RETENCION;
          a.TASA_RETENCION = this.aux.TASA_RETENCION;
          a.ESTADOAUX = this.aux.ESTADOAUX;
          a.ID_CLASE_OPERACION = this.aux.ID_CLASE_OPERACION;
          a.TIPO_COMPROBANTE = this.aux.TIPO_COMPROBANTE;
          a.ID_COMPROBANTE = this.aux.ID_COMPROBANTE;
          a.ID = this.aux.ID;
          a.DETALLE = this.aux.DETALLE;
          a.CHEQUE = this.aux.CHEQUE;
        }
        
      }); */
    },
    handleDelete(aux) {
      var _idx = this.auxiliares.indexOf(aux);
      if (_idx > -1) {
        this.auxiliares.splice(_idx, 1);
        this.aux = this.auxiliarVacio();
      }
      this.$refs["tabla"].doLayout();
    },
    handleClear() {
      this.aux = this.auxiliarVacio();
    },
    handleSave() {
      this.showGuardarDlg = true;
    },
    handleNullify() {
      this.showAnularDlg = true;
    },
    handlePdf() {
      obtenerNotaPdf(
        this.comprobante.TIPO_COMPROBANTE,
        this.comprobante.ID_COMPROBANTE
      ).then((response) => {
        let blob = new Blob([response.data], { type: "application/pdf" });
        let link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download =
          "nota_" +
          this.tipo(this.comprobante.TIPO_COMPROBANTE).replace(/\s/g, "_") +
          "_" +
          this.comprobante.ID_COMPROBANTE +
          ".pdf";
        link.click();
      });
    },
    setAuxiliar(val) {
      this.aux = {
        CSC: val.CSC,
        ID_AGENCIA: val.ID_AGENCIA,
        FECHA: val.FECHA,
        CODIGO: val.CODIGO,
        CUENTA: val.CUENTA,
        DEBITO: val.DEBITO,
        CREDITO: val.CREDITO,
        ID_CUENTA: val.ID_CUENTA,
        ID_COLOCACION: val.ID_COLOCACION,
        ID_IDENTIFICACION: val.ID_IDENTIFICACION,
        ID_PERSONA: val.ID_PERSONA,
        PERSONA: val.PERSONA,
        MONTO_RETENCION: val.MONTO_RETENCION,
        TASA_RETENCION: val.TASA_RETENCION,
        ESTADOAUX: val.ESTADOAUX,
        ID_CLASE_OPERACION: val.ID_CLASE_OPERACION,
        TIPO_COMPROBANTE: val.TIPO_COMPROBANTE,
        ID_COMPROBANTE: val.ID_COMPROBANTE,
        ID: val.ID,
        DETALLE: val.DETALLE,
        CHEQUE: val.CHEQUE,
      };
      this.lastaux = val;
    },
    showId(id) {
      console.log("id: '" + id + "'");
    },
    comprobanteVacio() {
      return {
        ID_COMPROBANTE: 0,
        ID_AGENCIA: 1,
        TIPO_COMPROBANTE: 1,
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
        CSC: null,
        ID_AGENCIA: 1,
        FECHA: new Date(),
        CODIGO: null,
        CUENTA: null,
        DEBITO: 0,
        CREDITO: 0,
        ID_CUENTA: null,
        ID_COLOCACION: null,
        ID_IDENTIFICACION: 0,
        ID_PERSONA: null,
        PERSONA: null,
        MONTO_RETENCION: 0,
        TASA_RETENCION: null,
        ESTADOAUX: null,
        ID_CLASE_OPERACION: null,
        TIPO_COMPROBANTE: null,
        ID_COMPROBANTE: null,
        ID: null,
        DETALLE: null,
        CHEQUE: null,
      };
    },
    informeVacio() {
      return {
        ID: null,
        DESCRIPCION: null,
        PIDOID: false,
        PIDOMONTO: false,
        PIDOTASA: false,
        PIDOTIPOID: false,
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
    tipo_documento(id) {
      if (id === undefined || id === null) {
        return "SIN ID";
      } else {
        var _id = id;
        var op = this.tipos_documento.find((o) => o.ID_IDENTIFICACION === _id);
        if (op) {
          return op.DESCRIPCION_IDENTIFICACION;
        } else {
          return "NO DEFINIDO";
        }
      }
    },
    tipo_informe(id) {
      if (id === undefined || id === null) {
        return this.informeVacio();
      } else {
        var _id = id;
        var op = this.informes.find((o) => o.ID.trim() === _id);
        if (op) {
          return op;
        } else {
          return this.informeVacio();
        }
      }
    },
    guardar() {
      this.showGuardarDlg = false;
      this.comprobante.AUXS = this.auxiliares;
      guardarComprobante(this.comprobante).then((response) => {
        this.comprobante.ID_COMPROBANTE = response.data;
        this.$message({
          message: "Comprobante Guardado: " + response.data,
          type: "success",
        });
      });
    },
    anular() {
      this.showAnularDlg = false;
      anularComprobante(
        this.comprobante.TIPO_COMPROBANTE,
        this.comprobante.ID_COMPROBANTE,
        this.comprobante.ANULACION
      )
        .then(() => {
          this.$message({
            message: "Comprobante Anulado",
            type: "success",
          });
        })
        .catch((err) => {
          this.$message({
            message: "Error al anular el comprobante: " + err,
            type: "error",
          });
        });
    },
    listaTipoComprobante() {
      obtenerTiposComprobante()
        .then((response) => {
          this.tipos_comprobante = response.data;
          this.listaInformes();
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los tipos de comprobantes: " + error,
            type: "error",
          });
        });
    },
    listaInformes() {
      obtenerInforme()
        .then((response) => {
          this.informes = response.data;
          this.listaTipoOperacion();
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los informes: " + error,
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
                this.auxiliares = response.data;
                this.auxiliares.forEach((a) => {
                  this.csc += 1;
                  this.$set(a, "CSC", this.csc);
                });
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
        this.comprobante.ID_COMPROBANTE = 0;
        this.comprobante.TIPO_COMPROBANTE = 1;
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
      if (
        this.aux !== null &&
        this.aux.CODIGO !== null &&
        this.aux.CODIGO.length < 18
      ) {
        this.aux.CODIGO =
          this.aux.CODIGO +
          "000000000000000000".substring(0, 18 - this.aux.CODIGO.length);
      }
      if (this.aux !== null && this.aux.CODIGO !== null) {
        esDeMovimiento(this.aux.CODIGO)
          .then((response) => {
            if (response.data.movimiento) {
              this.cuenta = response.data.cuenta;
              this.aux.CUENTA = response.data.cuenta.NOMBRE;
              this.informe = this.tipo_informe(this.cuenta.INFORME);
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
      }
    },
  },
};
</script>
<style lang="css" scoped>
.el-table .cell {
  line-height: 15px !important;
}
div.input.el-input__inner {
  text-align: right;
}
</style>
