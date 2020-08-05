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
          <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
            <template v-if="comprobante.ESTADO === 'O'">
              <el-form-item label="FECHA">
                <el-date-picker
                  v-model="comprobante.FECHADIA"
                  type="date"
                  placeholder="Seleccione la Fecha del Comprobante"
                ></el-date-picker>
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item label="Fecha">
                <span>{{ comprobante.FECHADIA | moment('YYYY-MM-DD')}}</span>
              </el-form-item>
            </template>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
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
          <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
            <template>
              <el-form-item label="Número Comprobante">
                <span
                  style="font-size: 24px; font-weigth: bold;"
                >{{ comprobante.ID_COMPROBANTE | pad(6) }}</span>
              </el-form-item>
            </template>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
            <template>
              <el-form-item label="Estado Actual">
                <span style="font-size: 24px; font-weigth: bold;">{{ estado(comprobante.ESTADO) }}</span>
              </el-form-item>
            </template>
          </el-col>
        </el-row>
        <el-row :gutter="4" v-if="comprobante.ESTADO === 'O'">
          <el-col :span="24">
            <el-form :model="aux">
              <el-row :gutter="4">
                <el-col :xs="22" :sm="22" :md="6" :lg="6" :xl="6" >
                  <el-form-item label="CODIGO">
                    <el-input v-model="aux.CODIGO" @blur="validarCodigo"/>
                  </el-form-item>
                </el-col>
                <el-col :xs="2" :sm="2" :md="2" :lg="2" :xl="2">
                  <el-form-item label="_">
                    <el-popover placement="right" width="700" trigger="click" v-model="puc_visible">
                      <puctree @selected="colocarcodigo"/>
                      <el-button slot="reference" mini circle icon="el-icon-search"></el-button>
                    </el-popover>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16" >
                  <el-form-item label="CUENTA">
                    <el-input readonly v-model="aux.CUENTA" />
                  </el-form-item>
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
              show-summary
              max-height="600"
              stripe
              style="font-size: 14px; width: 100%;"
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
  </el-container>
</template>
<script>
import PucTree from '@/components/PucTree'

import {
  obtenerTiposComprobante,
  obtenerComprobante,
  obtenerAuxiliar,
  guardarComprobante,
} from "@/api2/contabilidad";

export default {
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
  components : {
    puctree: PucTree
  },
  data() {
    return {
      tipos_comprobante: [],
      comprobante: {},
      nuevo: true,
      auxiliar: [],
      filtro: null,
      aux: null,
      puc_visible: false,
    };
  },
  beforeMount() {
    this.comprobante = this.comprobanteVacio();
    this.aux = this.auxiliarVacio();
    this.listaTipoComprobante();
  },
  mount() {
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
          sums[index] =
            "$ " +
            this.$options.filters.currency_2(
              values.reduce((prev, curr) => {
                const value = Number(curr);
                if (!isNaN(value)) {
                  return prev + curr;
                } else {
                  return prev;
                }
              }, 0)
            );
        } else {
          sums[index] = "N/A";
        }
      });
      return sums;
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
        ID_AGENCIA: null,
        FECHA: null,
        CODIGO: null,
        DEBITO: null,
        CREDITO: null,
        ID_CUENTA: null,
        ID_COLOCACION: null,
        ID_IDENTIFICACION: null,
        ID_PERSONA: null,
        MONTO_RETENCION: null,
        TASA_RETENCION: null,
        ESTADOAUX: null,
        ID_CLASE_OPERACION: null,
        TIPO_COMPROBANTE: null,
        ID_COMPROBANTE: null,
        ID: null,
      };
    },
    auxiliarExtVacio() {
      return {
        DETALLE: null,
        CHEQUE: null,
        ID_AGENCIA: null,
        TIPO_COMPROBANTE: null,
        ID_COMPROBANTE: null,
        ID: null,
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
    guardar() {
      guardarComprobante(this.comprobante).then(() => {});
    },
    listaTipoComprobante() {
      obtenerTiposComprobante()
        .then((response) => {
          this.tipos_comprobante = response.data;
          this.buscarComprobante();
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los tipos de comprobantes: " + error,
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
    colocarcodigo(codigo) {
      this.puc_visible = false;
      this.aux.CODIGO = codigo;
      this.validarCodigo();
    },
    validarCodigo() {
      
    }
  },
};
</script>
<style lang="css" scoped>
.el-table .cell {
  line-height: 15px !important;
}
</style>