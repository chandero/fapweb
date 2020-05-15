/** Rutas Cartera */

import Layout from '@/views/layout/Layout'

const carteraRouter = {
  path: '/cartera',
  component: Layout,
  redirect: 'noRedirect',
  name: 'Cartera',
  meta: {
    title: 'cartera',
    icon: 'nested',
    roles: ['admin', 'cartera', 'revisor']
  },
  children: [
    {
      path: 'informe',
      component: () => import('@/views/cartera/informe/index'), // Parent router-view
      name: 'Informe',
      meta: { title: 'informe', roles: ['admin', 'cartera', 'revisor'] },
      redirect: '/cartera/informe/recaudocajames',
      children: [
        {
          path: 'recaudocajames',
          component: () => import('@/views/cartera/informe/recaudocajames'),
          name: 'RecaudoCajaMes',
          meta: { title: 'recaudocajames', roles: ['admin', 'cartera', 'revisor'] }
        }
      ]
    },
    {
      path: 'controlcobro',
      name: 'ControlCobro',
      component: () => import('@/views/cartera/controlcobro'),
      meta: { title: 'controlcobro', roles: ['admin', 'cartera', 'revisor'] }
    },
    {
      path: 'causacion',
      component: () => import('@/views/cartera/causacion'), // Parent router-view
      name: 'Informe',
      meta: { title: 'informe', roles: ['admin', 'cartera', 'revisor'] },
      redirect: 'noRedirect',
      children: [
        {
          path: 'consulta',
          component: () => import('@/views/cartera/causacion/consulta'),
          name: 'CausacionConsulta',
          meta: { title: 'consulta', roles: ['admin', 'cartera', 'revisor'] }
        }
      ]
    }
  ]
}

export default carteraRouter
