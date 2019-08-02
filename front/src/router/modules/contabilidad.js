import Layout from '@/views/layout/Layout'

const contabilidadRouter = {
  path: '/contabilidad',
  component: Layout,
  redirect: '/contabilidad/libros/mayorbalance',
  name: 'Contabilidad',
  meta: {
    title: 'Contabilidad',
    icon: 'nested',
    roles: ['admin', 'contabilidad', 'revisor']
  },
  children: [
    {
      path: 'libros',
      component: () => import('@/views/contabilidad/libros/index'), // Parent router-view
      name: 'Libros',
      meta: { title: 'libros'/*, roles: ['admin', 'contabilidad', 'revisor'] */ },
      redirect: '/contabilidad/libros/mayorbalance',
      children: [
        {
          path: 'mayorbalance',
          component: () => import('@/views/contabilidad/libros/mayorbalance/index'),
          name: 'MayorBalance',
          redirect: '/contabilidad/libros/mayorbalance/consultar',
          meta: { title: 'mayorbalance'/*,  roles: ['admin', 'contabilidad', 'revisor'] */ },
          children: [
            {
              path: 'consultar',
              component: () => import('@/views/contabilidad/libros/mayorbalance/consultar'),
              name: 'Consultar',
              meta: { title: 'consultar'/*, roles: ['admin', 'contabilidad', 'revisor'] */ }
            },
            {
              path: 'generar',
              component: () => import('@/views/contabilidad/libros/mayorbalance/generar'),
              name: 'Generar',
              meta: { title: 'Generar', roles: ['admin', 'contabilidad'] }
            }
          ]
        },
        {
          path: 'cajadiario',
          component: () => import('@/views/contabilidad/libros/cajadiario'),
          // component: Layout,
          name: 'CajaDiario',
          redirect: '/contabilidad/libros/cajadiario/consultar',
          meta: { title: 'CajaDiario', roles: ['admin', 'contabilidad', 'revisor'] },
          children: [
            {
              path: 'consultar',
              component: () => import('@/views/contabilidad/libros/cajadiario/consultar'),
              name: 'Consultar',
              meta: { title: 'Consultar', roles: ['admin', 'contabilidad', 'revisor'] }
            },
            {
              path: 'generar',
              component: () => import('@/views/contabilidad/libros/cajadiario/generar'),
              name: 'Generar',
              meta: { title: 'Generar', roles: ['admin', 'contabilidad'] }
            }
          ]
        }
      ]
    },
    {
      path: 'menu2',
      name: 'Menu2',
      component: () => import('@/views/nested/menu2/index'),
      meta: { title: 'menu2', roles: ['admin', 'contabilidad'] }
    }
  ]
}

export default contabilidadRouter
