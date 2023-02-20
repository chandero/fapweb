<template>
  <el-container>
    <el-main>
      <comprobante :tipo_comprobante="TIPO_COMPROBANTE" :id_comprobante="ID_COMPROBANTE" />
    </el-main>
  </el-container>
</template>
<script>
import ComprobanteComponent from "@/components/Comprobante";
import { obtenerTiposComprobante, obtenerComprobante, obtenerAuxiliar } from "@/api2/contabilidad";

export default {
  components: {
    comprobante: ComprobanteComponent,
  },
  data() {
    return {
      TIPO_COMPROBANTE: null,
      ID_COMPROBANTE: null,
    };
  },
  beforeMount() {
    this.TIPO_COMPROBANTE = parseInt(this.$route.params.tp, 10);
    this.ID_COMPROBANTE = parseInt(this.$route.params.id, 10);
  }
};
</script>
