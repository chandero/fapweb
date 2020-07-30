<template>
  <el-container>
    <el-header>
      <span>COMPROBANTE CONTABLE</span>
    </el-header>
    <el-main>
      <el-form :model="comprobante">
        <el-row :gutter="4">
          <el-col :xs="4" :sm="4" :md="4" :lg="4" :xl="4">
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
          <el-col :xs="4" :sm="4" :md="4" :lg="4" :xl="4">
            <template v-if="comprobante.ESTADO === 'O'">
              <el-form-item label="Tipo de Comprobante">
                <el-select
                  v-model="comprobante.TIPO_COMPROBANTE"
                  filterable
                  placeholder="Seleccione el Tipo de Comprobante"
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
          <el-col :xs="4" :sm="4" :md="4" :lg="4" :xl="4">
            <template>
              <el-form-item label="Número Comprobante">
                <span>{{ comprobante.ID_COMPROBANTE | pad(6) }}</span>
              </el-form-item>
            </template>
          </el-col>
        </el-row>
        <el-col :xs="4" :sm="4" :md="4" :lg="4" :xl="4">
          <template>
            <el-form-item label="Estado Actual">
              <span>{{ estado(comprobante.ESTADO) }}</span>
            </el-form-item>
          </template>
        </el-col>
      </el-form>
    </el-main>
  </el-container>
</template>
<script>
import { obtenerTiposComprobante } from "@/api2/contabilidad";

export default {
  props: {
    comprobante: {
      type: Object,
      required: true,
      default: function () {
        return {
          ID_COMPROBANTE: null,
          ID_AGENCIA: null,
          TIPO_COMPROBANTE: null,
          FECHADIA: null,
          DESCRIPCION: null,
          TOTAL_DEBITO: null,
          TOTAL_CREDITO: null,
          ESTADO: null,
          IMPRESO: null,
          ANULACION: null,
          ID_EMPLEADO: null,
          AUXS: [],
        };
      },
    },
  },
  data() {
    return {
      tipos_comprobante: [],
    };
  },
  beforeMount() {
    this.listaTipoComprobante();
  },
  methods: {
    comprobanteVacio() {
      return {
          ID_COMPROBANTE: null,
          ID_AGENCIA: null,
          TIPO_COMPROBANTE: null,
          FECHADIA: null,
          DESCRIPCION: null,
          TOTAL_DEBITO: null,
          TOTAL_CREDITO: null,
          ESTADO: null,
          IMPRESO: null,
          ANULACION: null,
          ID_EMPLEADO: null,
          AUXS: [],
      }
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
        ID: null
      }
    },
    auxiliarExtVacio() {
      return {
        DETALLE: null, 
        CHEQUE: null, 
        ID_AGENCIA: null, 
        TIPO_COMPROBANTE: null, 
        ID_COMPROBANTE: null, 
        ID: null
      }
    },    
    listaTipoComprobante() {
      obtenerTiposComprobante()
        .then((response) => {
          this.tipos_comprobante = response.data;
        })
        .catch((error) => {
          this.$message({
            message: "Error al consultar los tipos de comprobantes: " + error,
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
    }    
  }
};
</script>