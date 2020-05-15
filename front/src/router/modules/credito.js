import Layout from '@/views/layout/Layout'

const creditoRouter = {
  path: '/credito',
  component: Layout,
  redirect: '/credito/nuevo/solicitud',
  name: 'Credito',
  meta: {
    title: 'credito',
    icon: 'nested',
    roles: ['admin', 'credito', 'cartera']
  },
  children: [
    {
      path: 'solicitud',
      component: () => import('@/views/credito/nuevo/solicitud/index'), // Parent router-view
      name: 'Solicitud',
      meta: { title: 'Solicitud', roles: ['admin', 'credito'] },
      redirect: '/credito/nuevo/solicitud',
      children: [
        {
          path: 'solicitud',
          component: () => import('@/views/credito/nuevo/solicitud'),
          name: 'solicitud',
          redirect: '/credito/nuevo/solicitud',
          meta: { title: 'Solicitud', roles: ['admin', 'credito'] },
          children: [
            {
              path: 'nuevo',
              component: () => import('@/views/credito/nuevo/solicitud'),
              name: 'nuevo',
              meta: { title: 'nuevo', roles: ['admin', 'credito'] }
            }
          ]
        }
      ]
    },
    {
      path: 'menu2',
      name: 'Menu2',
      component: () => import('@/views/nested/menu2/index'),
      meta: { title: 'menu2', roles: ['admin'] }
    },
    {
      path: 'buenpago',
      name: 'BuenPago',
      component: () => import('@/views/credito/buenpago/index'),
      meta: { title: 'buenpago', roles: ['admin', 'cartera'] }
    }
  ]
}

export default creditoRouter
