/** When your routing table is too long, you can split it into small modules**/

import Layout from '@/views/layout/Layout'

const cartera2Router = {
  path: '/cartera',
  component: Layout,
  redirect: '/cartera2/menu1proceso/menu1-1liquidacionnormal',
  name: 'Cartera',
  meta: {
    title: 'cartera',
    icon: 'nested',
    roles: ['admin', 'cartera', 'revisor', 'gerencia']
  },
  children: [
    {
      path: 'menu1proceso',
      component: () => import('@/views/cartera2/menu1proceso/index'), // Parent router-view
      name: 'Menu1proceso',
      meta: { title: 'menu1proceso', icon: 'nested',  roles: ['admin', 'cartera', 'gerencia']},
      redirect: '/cartera2/menu1proceso/menu1-1liquidacionnormal',
      children: [
        {
          path: 'menu1-1liquidacionnormal',
          component: () => import('@/views/cartera2/menu1proceso/menu1-1liquidacionnormal'),
          name: 'Menu1-1liquidacionnormal',
          meta: { title: 'menu1-1liquidacionnormal', roles: ['admin', 'cartera', 'gerencia'] }
        },
        {
          path: 'menu1-2liquidacionmanual',
          component: () => import('@/views/cartera2/menu1proceso/menu1-2liquidacionmanual'),
          name: 'Menu1-2liquidacionmanual',
          meta: { title: 'menu1-2liquidacionmanual', roles: ['admin', 'cartera', 'gerencia'] }
        }
      ]
    },
    {
      path: 'menu2gestion',
      component: () => import('@/views/cartera2/menu2gestion/index'), // Parent router-view
      name: 'Menu2gestion',
      meta: { title: 'menu2gestion', icon: 'nested',  roles: ['admin', 'cartera', 'gerencia'] },
      redirect: '/cartera2/menu2gestion/menu2-1controlcobro',
      children: [
        {
          path: 'menu2-1controlcobro',
          component: () => import('@/views/cartera2/menu2gestion/menu2-1controlcobro'),
          name: 'Menu2-1controlcobro',
          meta: { title: 'menu2-1controlcobro', roles: ['admin', 'cartera', 'gerencia'] }
        },
        {
          path: 'menu2-2periodogracia',
          component: () => import('@/views/cartera2/menu2gestion/menu2-2periodogracia'),
          name: 'Menu2-2periodogracia',
          meta: { title: 'menu2-2periodogracia', roles: ['admin', 'gerencia'] }
        }
      ]
    },
    {
      path: 'menu3causacion',
      component: () => import('@/views/cartera2/menu3causacion/index'), // Parent router-view
      name: 'Menu3causacion',
      meta: { title: 'menu3causacion', icon: 'nested',  roles: ['admin', 'cartera', 'gerencia'] },
      redirect: '/cartera2/menu3causacion/menu1-1liquidacionnormal',
      children: [
        {
          path: 'menu3-1proceso',
          component: () => import('@/views/cartera2/menu3causacion/menu3-1proceso'),
          name: 'Menu3-1proceso',
          meta: { title: 'menu3-1proceso' }
        },
        {
          path: 'menu3-2consulta',
          component: () => import('@/views/cartera2/menu3causacion/menu3-2consulta'),
          name: 'Menu3-2consulta',
          meta: { title: 'menu3-2consulta' }
        }
      ]
    },
    {
      path: 'menu4informe',
      component: () => import('@/views/cartera2/menu4informe/index'), // Parent router-view
      name: 'Menu4informe',
      meta: { title: 'menu4informe', icon: 'nested',  roles: ['admin', 'cartera', 'gerencia'] },
      redirect: '/cartera2/menu4informe/menu4-1recaudoxmes',
      children: [
        {
          path: 'menu4-1recaudoxmes',
          component: () => import('@/views/cartera2/menu4informe/menu4-1recaudoxmes'),
          name: 'Menu4-1recaudoxmes',
          meta: { title: 'menu4-1recaudoxmes' }
        },
        {
          path: 'menu4-2recaudointerescausado',
          component: () => import('@/views/cartera2/menu4informe/menu4-2recaudointerescausado'),
          name: 'Menu4-2recaudointerescausado',
          meta: { title: 'menu4-2recaudointerescausado' }
        }
      ]
    },
    {
      path: 'menu5consulta',
      component: () => import('@/views/cartera2/menu5consulta/index'), // Parent router-view
      name: 'Menu5consulta',
      meta: { title: 'menu5consulta', icon: 'nested', roles: ['admin', 'cartera', 'revisor', 'gerencia'] },
      redirect: '/cartera2/menu5consulta/menu5-1colocacion',
      children: [
        {
          path: 'menu5-1colocacion',
          component: () => import('@/views/cartera2/menu5consulta/menu5-1colocacion'),
          name: 'Menu5-1colocacion',
          meta: { title: 'menu5-1colocacion', roles: ['admin', 'cartera', 'revisor', 'gerencia'] }
        }
      ]
    }    
  ]
}

export default cartera2Router
