import store from "@/store";

export function gTipoCartera(id) {
  return store.getters.tipo_cartera.find(item => item.TIPO_CARTERA_ID == id)
    .TIPO_CARTERA_DESCRIPCION;
}
