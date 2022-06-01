import Vue from "vue";

import Cookies from "js-cookie";

import "normalize.css/normalize.css"; // A modern alternative to CSS resets

import Element from "element-ui";
import "element-ui/lib/theme-chalk/index.css";

import "@/styles/index.scss"; // global css

import VueMoment from "vue-moment";
import VueCurrencyFilter from "vue-currency-filter";
import VueCurrencyInput from "vue-currency-input";
import VuePercentInput from "vue-percent-input";
import VueMask from "v-mask";
import VueSSE from "vue-sse";

import App from "./App";
import store from "./store";
import router from "./router";

import i18n from "./lang"; // Internationalization
import "./icons"; // icon
import "./errorLog"; // error log
import "./permission"; // permission control
// import './mock' // simulation data

import * as filters from "./filters"; // global filters
// Session Idle
import IdleVue from "idle-vue";

import { v4 as uuidv4 } from "uuid";

Vue.use(VueMoment);
Vue.use(VuePercentInput);
Vue.use(VueMask);
Vue.use(VueSSE);
Vue.use(VueCurrencyFilter, [
  {
    // default name 'currency'
    symbol: "$",
    thousandsSeparator: ",",
    fractionCount: 2,
    fractionSeparator: ".",
    symbolPosition: "front",
    symbolSpacing: true
  },
  {
    // default name 'currency_2'
    name: "currency_2",
    symbol: "",
    thousandsSeparator: ",",
    fractionCount: 2,
    fractionSeparator: ".",
    symbolPosition: "front",
    symbolSpacing: false
  }
]);

const vueCurrencyInputPluginOptions = {
  /* see config reference */
  globalOptions: { currency: "COP", locale: "es" }
};

Vue.use(VueCurrencyInput, vueCurrencyInputPluginOptions);
Vue.use(Element, {
  size: Cookies.get("size") || "medium", // set element-ui default size
  i18n: (key, value) => i18n.t(key, value)
});

// register global utility filters.
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key]);
});

Vue.config.productionTip = false;

const eventsHub = new Vue();

Vue.use(IdleVue, {
  eventEmmiter: eventsHub,
  store,
  idleTime: 60000, // 60 segundos
  startAtIdle: false
});

const uuid = localStorage.getItem("SessionUUID");
console.log("Uuid almacenado: " + uuid);
if (uuid !== undefined && uuid !== null && uuid !== "") {
  console.log("Usando uuid almacenado: " + uuid);
  store.dispatch("SetUUID", uuid);
} else {
  const nuuid = uuidv4();
  console.log("Usando uuid NUEVO: " + nuuid);
  store.dispatch("SetUUID", nuuid);
}

new Vue({
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount("#app");
