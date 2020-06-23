import Cookies from 'js-cookie'

const app = {
  state: {
    sidebar: {
      opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true,
      withoutAnimation: false
    },
    device: 'desktop',
    language: Cookies.get('language') || 'es',
    size: Cookies.get('size') || 'medium',
    periodos: [
      {
        id: 1,
        descripcion: 'enero'
      },
      {
        id: 2,
        descripcion: 'febrero'
      },
      {
        id: 3,
        descripcion: 'marzo'
      },
      {
        id: 4,
        descripcion: 'abril'
      },
      {
        id: 5,
        descripcion: 'mayo'
      },
      {
        id: 6,
        descripcion: 'junio'
      },
      {
        id: 7,
        descripcion: 'julio'
      },
      {
        id: 8,
        descripcion: 'agosto'
      },
      {
        id: 9,
        descripcion: 'septiembre'
      },
      {
        id: 10,
        descripcion: 'octubre'
      },
      {
        id: 11,
        descripcion: 'noviembre'
      },
      {
        id: 12,
        descripcion: 'diciembre'
      }
    ]
  },
  mutations: {
    TOGGLE_SIDEBAR: state => {
      state.sidebar.opened = !state.sidebar.opened
      state.sidebar.withoutAnimation = false
      if (state.sidebar.opened) {
        Cookies.set('sidebarStatus', 1)
      } else {
        Cookies.set('sidebarStatus', 0)
      }
    },
    CLOSE_SIDEBAR: (state, withoutAnimation) => {
      Cookies.set('sidebarStatus', 0)
      state.sidebar.opened = false
      state.sidebar.withoutAnimation = withoutAnimation
    },
    TOGGLE_DEVICE: (state, device) => {
      state.device = device
    },
    SET_LANGUAGE: (state, language) => {
      state.language = language
      Cookies.set('language', language)
    },
    SET_SIZE: (state, size) => {
      state.size = size
      Cookies.set('size', size)
    }
  },
  actions: {
    toggleSideBar({ commit }) {
      commit('TOGGLE_SIDEBAR')
    },
    closeSideBar({ commit }, { withoutAnimation }) {
      commit('CLOSE_SIDEBAR', withoutAnimation)
    },
    toggleDevice({ commit }, device) {
      commit('TOGGLE_DEVICE', device)
    },
    setLanguage({ commit }, language) {
      commit('SET_LANGUAGE', language)
    },
    setSize({ commit }, size) {
      commit('SET_SIZE', size)
    }
  }
}

export default app
