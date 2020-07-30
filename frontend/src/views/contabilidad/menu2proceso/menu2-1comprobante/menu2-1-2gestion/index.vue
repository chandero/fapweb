<template>
  <el-container>
    <el-header>
      <h1>Comprobante Contable No. {{ comprobante.ID_COMPROBANTE }}</h1>
    </el-header>
    <el-main>
      <comprobante :comprobante="comprobante" />
    </el-main>
  </el-container>
</template>
<script>
import ComprobanteComponent from "@/components/Comprobante";
import { obtenerComprobante, obtenerAuxiliar } from "@/api2/contabilidad";

export default {
  components: {
    comprobante: ComprobanteComponent,
  },
  data() {
    return {
      comprobante: {
        TIPO_COMPROBANTE: null,
        ID_COMPROBANTE: null,
      },
      auxiliar: [],
      tipos_comprobante: [],
    };
  },
  beforeMount() {
    this.buscarComprobante();
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
    buscarComprobante() {
      console.log("buscando comprobante");
      this.comprobante.TIPO_COMPROBANTE = parseInt(this.$route.params.tp, 10);
      this.comprobante.ID_COMPROBANTE = parseInt(this.$route.params.id, 10);
      if (
        this.comprobante.TIPO_COMPROBANTE &&
        this.comprobante.ID_COMPROBANTE
      ) {
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
      }
    },
  },
};
</script>
