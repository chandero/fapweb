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
      path: 'menu2proceso',
      component: () => import('@/views/contabilidad/menu2proceso/index'), // Parent router-view
      name: 'menu_contabilidad_menu2proceso',
      meta: { title: 'menu_contabilidad_menu2proceso', icon: 'process1', roles: ['admin', 'contabilidad', 'revisor'] },
      redirect: '/contabilidad/menu2proceso/menu2-1list',
      children: [
        {
          path: 'menu2-1comprobante',
          component: () => import('@/views/contabilidad/menu2proceso/menu2-1comprobante/index'),
          name: 'menu_contabilidad_menu2proceso_menu2-1comprobante',
          redirect: '/contabilidad/menu2proceso/menu2-1comprobante/menu2-1-1list',
          meta: { title: 'menu_contabilidad_menu2proceso_menu2-1comprobante', icon: 'el-icon-notebook-2', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu2-1-1list',
              component: () => import('@/views/contabilidad/menu2proceso/menu2-1comprobante/menu2-1-1list/index'),
              name: 'menu_contabilidad_menu2proceso_menu2-1comprobante_menu2-1-1list',
              hidden: true,
              meta: { title: 'menu_contabilidad_menu2proceso_menu2-1comprobante_menu2-1-1list', icon: 'note-19', roles: ['admin', 'contabilidad', 'revisor'] }
            },
            {
              path: 'menu2-1-2gestion/:tp?/:id?',
              component: () => import('@/views/contabilidad/menu2proceso/menu2-1comprobante/menu2-1-2gestion/index'),
              name: 'menu_contabilidad_menu2proceso_menu2-1comprobante_menu2-1-2gestion',
              hidden: true,
              meta: { title: 'menu_contabilidad_menu2proceso_menu2-1comprobante_menu2-1-2gestion', roles: ['admin', 'contabilidad'] }
            }
          ]
        }
      ]
    },
    {
      path: 'menu3libros',
      component: () => import('@/views/contabilidad/menu3libros/index'), // Parent router-view
      name: 'menu_contabilidad_menu3libros',
      meta: { title: 'menu_contabilidad_menu3libros', icon: 'el-icon-notebook-1', roles: ['admin', 'contabilidad', 'revisor'] },
      redirect: '/contabilidad/menu3libros/menu3-1mayorbalance',
      children: [
        {
          path: 'menu3-1mayorbalance',
          component: () => import('@/views/contabilidad/menu3libros/menu3-1mayorbalance/index'),
          name: 'menu_contabilidad_menu3libros_menu3-1mayorbalance',
          redirect: '/contabilidad/menu3libros/menu3-1mayorbalance/menu3-1-1consultar',
          meta: { title: 'menu_contabilidad_menu3libros_menu3-1mayorbalance', icon: 'el-icon-notebook-2', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu3-1-1consultar',
              component: () => import('@/views/contabilidad/menu3libros/menu3-1mayorbalance/menu3-1-1consultar'),
              name: 'menu_contabilidad_menu3libros_menu3-1mayorbalance_menu3-1-1consultar',
              meta: { title: 'menu_contabilidad_menu3libros_menu3-1mayorbalance_menu3-1-1consultar', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
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
              meta: { title: 'menu_contabilidad_menu3libros_menu3-2cajadiario_menu3-2-1consultar', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
            },
            {
              path: 'menu3-2-2generar',
              component: () => import('@/views/contabilidad/menu3libros/menu3-2cajadiario/menu3-2-2generar'),
              name: 'menu_contabilidad_menu3libros_menu3-2cajadiario_menu3-2-2generar',
              meta: { title: 'menu_contabilidad_menu3libros_menu3-2cajadiario_menu3-2-2generar', icon: 'el-icon-edit-outline', roles: ['admin', 'contabilidad'] }
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
      redirect: '/contabilidad/menu4facturaelectronica/menu4-1factura',
      children: [
        {
          path: 'menu4-1factura',
          component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-1factura'),
          name: 'menu_contabilidad_menu4facturaelectronica_menu4-1factura',
          meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-1factura', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu4-1-1list',
              component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-1factura/menu4-1-1list'),
              name: 'menu_contabilidad_menu4facturaelectronica_menu4-1factura_menu4-1-1list',
              meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-1factura_menu4-1-1list', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
            },
            {
              path: 'menu4-1-4reprocesar',
              component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-1factura/menu4-1-4reprocesar'),
              name: 'menu_contabilidad_menu4facturaelectronica_menu4-1factura_menu4-1-4reprocesar',
              hidden: true,
              meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-1factura_menu4-1-4reprocesar', icon: 'process1', roles: ['admin', 'contabilidad'] }
            }
          ]
        },
        {
          path: 'menu4-5dsa',
          component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-5dsa'),
          name: 'menu_contabilidad_menu4facturaelectronica_menu4-5dsa',
          meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-5dsa', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu4-5-1list',
              component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-5dsa/menu4-5-1list'),
              name: 'menu_contabilidad_menu4facturaelectronica_menu4-5dsa_menu4-5-1list',
              meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-5dsa_menu4-5-1list', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
            },
            {
              path: 'menu4-5-4reprocesar',
              component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-5dsa/menu4-5-4reprocesar'),
              name: 'menu_contabilidad_menu4facturaelectronica_menu4-5dsa_menu4-5-4reprocesar',
              hidden: true,
              meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-5dsa_menu4-5-4reprocesar', icon: 'process1', roles: ['admin', 'contabilidad'] }
            }
          ]
        },
        {
          path: 'menu4-2nd',
          component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-2nd'),
          name: 'menu_contabilidad_menu4facturaelectronica_menu4-2nd',
          meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-2nd', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu4-2-1list',
              component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-2nd/menu4-2-1list'),
              name: 'menu_contabilidad_menu4facturaelectronica_menu4-2nd_menu4-2-1list',
              meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-2nd_menu4-2-1list', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
            },
          ]
        },
        {
          path: 'menu4-3nc',
          component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-3nc'),
          name: 'menu_contabilidad_menu4facturaelectronica_menu4-3nc',
          meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-3nc', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu4-3-1list',
              component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-3nc/menu4-3-1list'),
              name: 'menu_contabilidad_menu4facturaelectronica_menu4-3nc_menu4-3-1list',
              meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-3nc_menu4-3-1list', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
            },
          ]
        },
        {
          path: 'menu4-4ncdsa',
          component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-4ncdsa'),
          name: 'menu_contabilidad_menu4facturaelectronica_menu4-ncdsa',
          meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-4ncdsa', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'menu4-4-1list',
              component: () => import('@/views/contabilidad/menu4facturaelectronica/menu4-4ncdsa/menu4-4-1list'),
              name: 'menu_contabilidad_menu4facturaelectronica_menu4-4ncdsa_menu4-4-1list',
              meta: { title: 'menu_contabilidad_menu4facturaelectronica_menu4-4ncdsa_menu4-4-1list', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] }
            },
          ]
        }
      ]
    },
    {
      path: 'menu5informe',
      component: () => import('@/views/contabilidad/menu5informe/index'), // Parent router-view
      name: 'menu_contabilidad_menu5informe',
      meta: { title: 'menu_contabilidad_menu5informe', icon: 'el-icon-notebook-2', roles: ['admin', 'contabilidad', 'revisor'] },
      redirect: '/contabilidad/menu5informe/menu5-1auxiliar',
      children: [
        {
          path: 'menu5-1auxiliar',
          component: () => import('@/views/contabilidad/menu5informe/menu5-1auxiliar'),
          name: 'menu_contabilidad_menu5informe_menu5-1auxiliar',
          meta: { title: 'menu_contabilidad_menu5informe_menu5-1auxiliar', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] },
        },
        {
          path: 'menu5-2balance',
          component: () => import('@/views/contabilidad/menu5informe/menu5-2balance'),
          name: 'menu_contabilidad_menu5informe_menu5-2balance',
          meta: { title: 'menu_contabilidad_menu5informe_menu5-2balance', icon: 'el-icon-view', roles: ['admin', 'contabilidad', 'revisor'] },
        }
      ]
    }
  ]
}

export default contabilidadRouter
