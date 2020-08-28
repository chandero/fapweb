<template>
  <el-container>
    <div class="app-wrapper" :class="{hideSidebar:!sidebar.opened}">
      <sidebar class="sidebar-container"></sidebar>
      <div class="main-container">
        <offline @detected-condition="handleConnectivityChange">
          <!-- Only renders when the device is online -->
          <div slot="online">
            <el-alert title="En Línea" type="success" center show-icon></el-alert>
          </div>
          <!-- Only renders when the device is offline -->
          <div slot="offline">
            <el-alert title="Fuera de Línea" type="warning" center show-icon></el-alert>
          </div>
        </offline>
        <span>Is Idle - {{ isIdle }}</span>
        <navbar></navbar>
        <tags-view></tags-view>
        <app-main></app-main>
      </div>
    </div>
    <ModalIdle v-if="isIdle" />
  </el-container>
</template>
<script>
import offline from "v-offline";
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
    };
  },
  computed: {
    sidebar() {
      return this.$store.state.app.sidebar;
    },
    isIdle() {
      return this.$store.state.idleVue.isIdle;
    },
  },
  methods: {
    handleConnectivityChange(status) {
      console.log(status);
    }    
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
  max-height: 800px;
}
</style>
