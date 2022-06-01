const parametro = {
  state: {
    tipo_cartera: []
  },
  mutations: {
    SET_TIPO_CARTERA: (state, data) => {
      state.tipo_cartera = data;
    }
  },
  actions: {
    SetTipoCartera({ commit }, data) {
      return new Promise(resolve => {
        commit("SET_TIPO_CARTERA", data);
        resolve();
      });
    }
  }
};

export default parametro;
