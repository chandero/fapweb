import Vue from "vue";
import Vuex from "vuex";
import app from "./modules/app";
import errorLog from "./modules/errorLog";
import permission from "./modules/permission";
import tagsView from "./modules/tagsView";
import user from "./modules/user";
import idleVue from "./modules/idleVue";
import settings from "./modules/settings";
import parametro from "./modules/parametro";
import getters from "./getters";

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    app,
    parametro,
    errorLog,
    permission,
    tagsView,
    user,
    idleVue,
    settings
  },
  getters
});

export default store;
