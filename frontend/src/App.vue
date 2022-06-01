<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { getTipoCartera } from "./api/pe";

export default {
  name: "App",
  mounted() {
    getTipoCartera().then((response) => {
      this.$store.dispatch("SetTipoCartera", response.data);
    });
    this.$store.dispatch("setBaseUrl", {
      url:
        window.location.protocol +
        "//" +
        window.location.host.split("/")[0] +
        "/api",
    });
  },
};
</script>
