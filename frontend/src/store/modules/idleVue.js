const idleVue = {
  state: {
    isIdle: false
  },

  mutations: {
    SET_ISIDLE: (state, isIdle) => {
      state.isIdle = isIdle
    }
  },

  actions: {
    setisIdle({ commit }, isIdle) {
      return new Promise(resolve => {
          commit('SET_ISIDLE', isIdle)
          resolve()
      })
    }
  }
}

export default idleVue