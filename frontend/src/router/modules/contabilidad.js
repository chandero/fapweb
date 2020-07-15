import Layout from '@/views/layout/Layout'

const contabilidadRouter = {
  path: '/contabilidad',
  component: Layout,
  name: 'menu_contabilidad',
  meta: {
    title: 'menu_contabilidad',
    icon: 'checkout',
    roles: ['admin', 'contabilidad', 'revisor']
  },
  children: [
    {
      path: 'menu3libros',
      component: () => import('@/views/contabilidad/menu3libros/index'), // Parent router-view
      name: 'menu_contabilidad_menu3libros',
      meta: { title: 'menu_contabilidad_menu3libros', icon: 'el-icon-notebook-1', roles: ['admin', 'contabilidad', 'revisor']  },
      redirect: '/contabilidad/menu3libros/menu3-1mayorbalance',
      children: [
        {
          path: 'menu3-1mayorbalance',
          component: () => import('@/views/contabilidad/menu3libros/menu3-1mayorbalance/index'),
          name: 'menu_contabilidad_menu3libros_menu3-1mayorbalance',
          redirect: '/contabilidad/menu3libros/menu3-1mayorbalance/menu3-1-1consultar',
          meta: { title: 'menu_contabilidad_menu3libros_menu3-1mayorbalance', icon: 'el-icon-notebook-2',  roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu3-1-1consultar',
              component: () => import('@/views/contabilidad/menu3libros/menu3-1mayorbalance/menu3-1-1consultar'),
              name: 'menu_contabilidad_menu3libros_menu3-1mayorbalance_menu3-1-1consultar',
              meta: { title: 'menu_contabilidad_menu3libros_menu3-1mayorbalance_menu3-1-1consultar', icon:'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
            },
            {
              path: 'menu3-1-2generar',
              component: () => import('@/views/contabilidad/menu3libros/menu3-1mayorbalance/menu3-1-2generar'),
              name: 'menu_contabilidad_menu3libros_menu3-1mayorbalance_menu3-1-2generar',
              meta: { title: 'menu_contabilidad_menu3libros_menu3-1mayorbalance_menu3-1-2generar', icon: 'el-icon-edit-outline', roles: ['admin', 'contabilidad'] }
            }
          ]
        },
        {
          path: 'menu3-2cajadiario',
          component: () => import('@/views/contabilidad/menu3libros/menu3-2cajadiario'),
          // component: Layout,
          name: 'menu_contabilidad_menu3libros_menu3-2cajadiario',
          redirect: '/contabilidad/menu3libros/menu3-2cajadiario/menu3-2-1consultar',
          meta: { title: 'menu_contabilidad_menu3libros_menu3-2cajadiario', icon: 'el-icon-coin', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu3-2-1consultar',
              component: () => import('@/views/contabilidad/menu3libros/menu3-2cajadiario/menu3-2-1consultar'),
              name: 'menu_contabilidad_menu3libros_menu3-2cajadiario_menu3-2-1consultar',
              meta: { title: 'menu_contabilidad_menu3libros_menu3-2cajadiario_menu3-2-1consultar', icon:'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
            },
            {
              path: 'menu3-2-2generar',
              component: () => import('@/views/contabilidad/menu3libros/menu3-2cajadiario/menu3-2-2generar'),
              name: 'menu_contabilidad_menu3libros_menu3-2cajadiario_menu3-2-2generar',
              meta: { title: 'menu_contabilidad_menu3libros_menu3-2cajadiario_menu3-2-2generar', icon:'el-icon-edit-outline', roles: ['admin', 'contabilidad'] }
            }
          ]
        }
      ]
    },
    {
      path: 'menu4facturaelectronica',
      component: () => import('@/views/contabilidad/menu4facturaelectronica/index'), // Parent router-view
      name: 'menu_contabilidad_menu4facturaelectronica',
      meta: { title: 'menu_contabilidad_menu4facturaelectronica', icon: 'qrcode4', roles: ['admin', 'contabilidad', 'revisor'] },
      redirect: '/contabilidad/menu4facturaelectronica/menu4-1list',
      children: [
        {
          path: 'menu4-1list',
          component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-1list'),
          name: 'menu_contabilidad_menu4facturaelectronica_menu4-1list',
          meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-1list', icon:'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
        },
        {
          path: 'menu4-4reprocesar',
          component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-4reprocesar'),
          name: 'menu_contabilidad_menu4facturaelectronica_menu4-4reprocesar',
          meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-4reprocesar', icon: 'process1', roles: ['admin', 'contabilidad'] }
        }
      ]
    }
  ]
}

export default contabilidadRouter
