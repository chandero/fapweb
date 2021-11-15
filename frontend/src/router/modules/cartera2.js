/** When your routing table is too long, you can split it into small modules**/

import Layout from '@/views/layout/Layout'

const cartera2Router = {
  path: '/menu_cartera',
  component: Layout,
  redirect: '/cartera2/menu1proceso/menu1-1liquidacionnormal',
  name: 'menu_cartera',
  meta: {
    title: 'menu_cartera',
    icon: 'el-icon-suitcase-1',
    roles: ['admin', 'cartera', 'revisor', 'gerencia']
  },
  children: [
    {
      path: 'menu1proceso',
      component: () => import('@/views/cartera2/menu1proceso/index'), // Parent router-view
      name: 'menu_cartera_menu1proceso',
      meta: { title: 'menu_cartera_menu1proceso', icon: 'el-icon-c-scale-to-original',  roles: ['admin', 'cartera', 'gerencia']},
      children: [
        {
          path: 'menu1-1liquidacionnormal',
          component: () => import('@/views/cartera2/menu1proceso/menu1-1liquidacionnormal'),
          name: 'menu_cartera_menu1proceso_menu1-1liquidacionnormal',
          meta: { title: 'menu_cartera_menu1proceso_menu1-1liquidacionnormal', icon: 'calcauto', roles: ['admin', 'cartera', 'gerencia'] }
        },
        {
          path: 'menu1-2liquidacionmanual',
          component: () => import('@/views/cartera2/menu1proceso/menu1-2liquidacionmanual'),
          name: 'menu_cartera_menu1proceso_menu1-2liquidacionmanual',
          meta: { title: 'menu_cartera_menu1proceso_menu1-2liquidacionmanual', icon: 'calcmanual', roles: ['admin', 'cartera', 'gerencia'] }
        }
      ]
    },
    {
      path: 'menu2gestion',
      component: () => import('@/views/cartera2/menu2gestion/index'), // Parent router-view
      name: 'menu_cartera_menu2gestion',
      meta: { title: 'menu_cartera_menu2gestion', icon: 'el-icon-sunrise-1',  roles: ['admin', 'cartera', 'gerencia'] },
      children: [
        {
          path: 'menu2-1controlcobro',
          component: () => import('@/views/cartera2/menu2gestion/menu2-1controlcobro'),
          name: 'menu_cartera_menu2gestion_menu2-1controlcobro',
          meta: { title: 'menu_cartera_menu2gestion_menu2-1controlcobro', icon:'el-icon-service', roles: ['admin', 'cartera', 'gerencia'] }
        },
        {
          path: 'menu2-2periodogracia',
          component: () => import('@/views/cartera2/menu2gestion/menu2-2periodogracia'),
          name: 'menu_cartera_menu2gestion_menu2-2periodogracia',
          meta: { title: 'menu_cartera_menu2gestion_menu2-2periodogracia', icon: 'el-icon-crop', roles: ['admin', 'gerencia'] }
        }
      ]
    },
    {
      path: 'menu3causacion',
      component: () => import('@/views/cartera2/menu3causacion/index'), // Parent router-view
      name: 'menu_cartera_menu3causacion',
      meta: { title: 'menu_cartera_menu3causacion', icon: 'table',  roles: ['admin', 'cartera', 'revisor', 'gerencia'] },
      children: [
        {
          path: 'menu3-1proceso',
          component: () => import('@/views/cartera2/menu3causacion/menu3-1proceso'),
          name: 'menu_cartera_menu3causacion_menu3-1proceso',
          meta: { title: 'menu_cartera_menu3causacion_menu3-1proceso', icon: 'process2', roles: ['admin', 'cartera', 'gerencia'] }
        },
        {
          path: 'menu3-2consulta',
          component: () => import('@/views/cartera2/menu3causacion/menu3-2consulta'),
          name: 'menu_cartera_menu3causacion_menu3-2consulta',
          meta: { title: 'menu_cartera_menu3causacion_menu3-2consulta', icon: 'eye-open', roles: ['admin', 'cartera', 'revisor', 'gerencia'] }
        }
      ]
    },
    {
      path: 'menu4informe',
      component: () => import('@/views/cartera2/menu4informe/index'), // Parent router-view
      name: 'menu_cartera_menu4informe',
      meta: { title: 'menu_cartera_menu4informe', icon: 'pdf',  roles: ['admin', 'cartera', 'gerencia'] },
      redirect: '/cartera2/menu4informe/menu4-1recaudoxmes',
      children: [
        {
          path: 'menu4-1recaudoxmes',
          component: () => import('@/views/cartera2/menu4informe/menu4-1recaudoxmes'),
          name: 'menu_cartera_menu4informe_menu4-1recaudoxmes',
          meta: { title: 'menu_cartera_menu4informe_menu4-1recaudoxmes', icon: 'coin2', roles: ['admin', 'cartera', 'gerencia'] }
        },
        {
          path: 'menu4-2recaudointerescausado',
          component: () => import('@/views/cartera2/menu4informe/menu4-2recaudointerescausado'),
          name: 'menu_cartera_menu4informe_menu4-2recaudointerescausado',
          meta: { title: 'menu_cartera_menu4informe_menu4-2recaudointerescausado', icon: 'coin5', roles: ['admin', 'cartera', 'gerencia'] }
        }
      ]
    },
    {
      path: 'menu5consulta',
      component: () => import('@/views/cartera2/menu5consulta/index'), // Parent router-view
      name: 'menu_cartera_menu5consulta',
      meta: { title: 'menu_cartera_menu5consulta', icon: 'eye-open', roles: ['admin', 'cartera', 'revisor', 'gerencia'] },
      children: [
        {
          path: 'menu5-1colocacion',
          component: () => import('@/views/cartera2/menu5consulta/menu5-1colocacion'),
          name: 'menu_cartera_menu5consulta_menu5-1colocacion',
          meta: { title: 'menu_cartera_menu5consulta_menu5-1colocacion', icon: 'banknote2', roles: ['admin', 'cartera', 'revisor', 'gerencia'] }
        }
      ]
    }    
  ]
}

export default cartera2Router
