import Layout from '@/views/layout/Layout'

const creditoRouter = {
  path: '/menu_credito',
  component: Layout,
  redirect: '/menu_credito/menu1solicitud',
  name: 'menu_credito',
  meta: {
    title: 'menu_credito',
    icon: 'el-icon-bank-card',
    roles: ['admin', 'credito']
  },
  children: [
    {
      path: 'menu1solicitud',
      component: () => import('@/views/credito/menu1solicitud/index'), // Parent router-view
      name: 'menu_credito_menu1solicitud',
      meta: { title: 'menu_credito_menu1solicitud', icon: 'form', roles: ['admin', 'credito'] },
      redirect: '/credito/menu1solicitud/menu1-2create',
      children: [
        {
          path: 'menu1-1list',
          component: () => import('@/views/credito/menu1solicitud/menu1-1list'),
          name: 'menu_solicitud_menu1solicitud_menu1-1list',
          hidden: true,          
          meta: { title: 'menu_credito_menu1solicitud_menu1-1list', roles: ['admin', 'credito'] }
        },
        {
          path: 'menu1-2create',
          component: () => import('@/views/credito/menu1solicitud/menu1-2create'),
          name: 'menu_credito_menu1solicitud_menu1-2create',
          hidden: true,
          meta: { title: 'menu_credito_menu1solicitud_menu1-2create', roles: ['admin', 'credito'] }
        },
        {
          path: 'menu1-3edit',
          component: () => import('@/views/credito/menu1solicitud/menu1-3edit'),
          name: 'menu_solicitud_menu1solicitud_menu1-3edit',
          hidden: true,
          meta: { title: 'menu_credito_menu1solicitud_menu1-3edit', roles: ['admin', 'credito'] }
        }
      ]
    },
    {
      path: 'menu3informe',
      name: 'menu3informe',
      component: () => import('@/views/credito/menu3informe/index'),
      meta: { title: 'menu_credito_menu3informe', icon:'pdf', roles: ['admin', 'credito'] },
      redirect: '/menu3informe/menu3-5buenpago',
      children: [
        {
          path: 'menu3-5buenpago',
          component: () => import('@/views/credito/menu3informe/menu3-5buenpago'),
          name: 'menu_credito_menu3informe_menu3-5buenpago',
          meta: { title: 'menu_credito_menu3informe_menu3-5buenpago', icon: 'money', roles: ['admin', 'credito'] }
        },
        {
          path: 'menu3-6saldadaasesor',
          component: () => import('@/views/credito/menu3informe/menu3-6saldadaasesor'),
          name: 'menu_credito_menu3informe_menu3-6saldadaasesor',
          meta: { title: 'menu_credito_menu3informe_menu3-6saldadaasesor', icon: 'money', roles: ['admin', 'credito'] }
        }        
      ]      
    }
  ]
}

export default creditoRouter
