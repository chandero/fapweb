<template>
  <el-container>
    <div class="app-wrapper" :class="{ hideSidebar: !sidebar.opened }">
      <sidebar class="sidebar-container"></sidebar>
      <div class="main-container">
        <offline @detected-condition="handleConnectivityChange">
          <!-- Only renders when the device is online -->
          <div slot="online">
            <el-alert
              title="En Línea"
              type="success"
              center
              show-icon
            ></el-alert>
          </div>
          <!-- Only renders when the device is offline -->
          <div slot="offline">
            <el-alert
              title="Fuera de Línea"
              type="warning"
              center
              show-icon
            ></el-alert>
          </div>
        </offline>
        <span>Is Idle - {{ isIdle }}</span>
        <navbar></navbar>
        <tags-view></tags-view>
        <app-main></app-main>
      </div>
    </div>
    <ModalIdle v-if="isIdle" />
    <div class="statusBox">
      <span>Estado Actual: {{ status }}</span>
    </div>
  </el-container>
</template>
<script>
import offline from "v-offline";
import { mapGetters } from "vuex";
import EventBus from "@/utils/eventBus";
import { Navbar, Sidebar, AppMain, TagsView } from "./components";
import ModalIdle from "@/components/ModalIdle";

export default {
  name: "layout",
  components: {
    offline,
    ModalIdle,
    Navbar,
    Sidebar,
    AppMain,
    TagsView,
  },
  data() {
    return {
      loading: false,
      inProgress: false,
      porcentaje: 0,
      status: "",
    };
  },
  computed: {
    ...mapGetters(["baseurl", "empresa", "sessionUUID", "months"]),
    sidebar() {
      return this.$store.state.app.sidebar;
    },
    isIdle() {
      return this.$store.state.idleVue.isIdle;
    },
  },
  mounted() {
    this.handleSse();
  },
  methods: {
    handleConnectivityChange(status) {
      console.log(status);
    },
    handleSse() {
      this.$sse
        .create("/api/progressStatuses/" + this.sessionUUID, {
          format: "json",
          withCredentials: true,
        })
        .on("message", (msg) => {
          var message = JSON.parse(msg);
          console.log("Mensaje Recibido: ", message);
          const event = message.name;
          console.log("Evento:", event);
          switch (event) {
            case "carga1PreparingEvent":
              this.inProgress = true;
              this.porcentaje = 0;
              this.status = "Iniciando Carga de Datos de Usuarios";
              break;
            case "carga1ParsingEvent":
              this.inProgress = true;
              this.porcentaje =
                (parseInt(message.data) / parseInt(message.size)) * 100;
              this.status = "Usuario Cargando " + this.porcentaje + "%";
              break;
            case "carga1DoneEvent":
              this.inProgress = false;
              this.porcentaje = 0;
              this.status = "Carga de Datos de Usuarios Finalizada";
              break;

            case "carga2PreparingEvent":
              this.inProgress = true;
              this.porcentaje = 0;
              this.status = "Iniciando Carga de Datos de Cartera";
              break;
            case "carga2ParsingEvent":
              this.inProgress = true;
              this.porcentaje =
                (parseInt(message.data) / parseInt(message.size)) * 100;
              this.status = "Cartera Cargando " + this.porcentaje + "%";
              break;
            case "carga2DoneEvent":
              this.inProgress = false;
              this.porcentaje = 0;
              this.status = "Carga de Datos de Cartera Finalizada";
              break;

            case "carga3PreparingEvent":
              this.inProgress = true;
              this.porcentaje = 0;
              this.status = "Iniciando Carga de Datos de Depositos";
              break;
            case "carga3ParsingEvent":
              this.inProgress = true;
              this.porcentaje =
                (parseInt(message.data) / parseInt(message.size)) * 100;
              this.status = "Depositos Cargando " + this.porcentaje + "%";
              break;
            case "carga3DoneEvent":
              this.inProgress = false;
              this.porcentaje = 0;
              this.status = "Carga de Datos de Depositos Finalizada";
              break;

            case "carga4PreparingEvent":
              this.inProgress = true;
              this.porcentaje = 0;
              this.status = "Iniciando Carga de Datos de Aportes";
              break;
            case "carga4ParsingEvent":
              this.inProgress = true;
              this.porcentaje =
                (parseInt(message.data) / parseInt(message.size)) * 100;
              this.status = "Aportes Cargando " + this.porcentaje + "%";
              break;
            case "carga4DoneEvent":
              this.inProgress = false;
              this.porcentaje = 0;
              this.status = "Carga de Datos de Aportes Finalizada";
              EventBus.$emit("loadEnd");
              break;
          }
        })
        .on("error", (err) => {
          console.error("Failed to connect to server", err);
        })
        .connect()
        .catch((err) => {
          console.error("Failed to connect to server", err);
        });
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "src/styles/mixin.scss";
.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;
}
.app_main {
  max-height: 800px;
}
.sidebar-container {
  max-height: 100vh;
  overflow-y: auto;
}

.statusBox {
  width: 400px;
  height: 30px;
  background-color: #cc0;
  border: 1px solid #ccc;
  border-radius: 10px;
  z-index: 10;
  padding-top: 5px;
  position: absolute;
  right: 10px;
  top: 120px;
}
</style>
