import Layout from '@/views/layout/Layout'

const carteraRouter = {
  path: '/cartera',
  component: Layout,
  redirect: '/cartera/causacion/consulta',
  name: 'Cartera',
  meta: {
    title: 'Cartera',
    icon: 'nested',
    roles: ['admin', 'cartera', 'revisor']
  },
  children: [
    {
      path: 'causacion',
      component: () => import('@/views/cartera/causacion/consulta/index'), // Parent router-view
      name: 'Causacion',
      meta: { title: 'Causacion', roles: ['admin', 'cartera', 'revisor'] },
      redirect: '/cartera/causacion/consulta',
      children: [
        {
          path: 'causacion',
          component: () => import('@/views/cartera/causacion/consulta'),
          name: 'Causacion',
          redirect: '/cartera/causacion/consulta',
          meta: { title: 'Causacion', roles: ['admin', 'cartera', 'revisor'] },
          children: [
            {
              path: 'consulta',
              component: () => import('@/views/cartera/causacion/consulta'),
              name: 'consulta',
              meta: { title: 'consulta', roles: ['admin', 'cartera', 'revisor'] }
            }
          ]
        }
      ]
    },
    {
      path: 'controlcobro',
      component: () => import('@/views/cartera/controlcobro/index'), // Parent router-view
      name: 'ControlCobro',
      meta: { title: 'ControlCobro', roles: ['admin', 'cartera', 'revisor'] }
    }
  ]
}

export default carteraRouter
