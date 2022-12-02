/** When your routing table is too long, you can split it into small modules**/

import Layout from '@/views/layout/Layout'

const fappRouter = {
  path: '/contabilidad',
  component: Layout,
  redirect: '/contabilidad/libros/cajadiario',
  name: 'contabilidad',
  meta: {
    title: 'contabilidad',
    icon: 'component'
  },
  children: [
    {
      path: 'libros',
      component: () => import('@/views/contabilidad/libros/index'), // Parent router-view
      name: 'libros',
      meta: { title: 'libros' },
      redirect: '/contabilidad/libros/cajadiario',
      children: [
        {
          path: 'mayorbalance',
          component: () => import('@/views/contabilidad/libros/mayorbalance'),
          name: 'MayorBalance',
          meta: { title: 'mayorbalance' },
          redirect: '/contabilidad/libros/mayorbalance',
          children: [
            {
              path: 'generar',
              component: () => import('@/views/contabilidad/libros/mayorbalance/generar'),
              name: 'generar',
              meta: { title: 'generar' }
            }
          ]
        },
        {
          path: 'cajadiario',
          component: () => import('@/views/contabilidad/libros/cajadiario'),
          name: 'CajaDiario',
          meta: { title: 'cajadiario' }
        }
      ]
    },
    {
      path: 'menu2',
      name: 'Menu2',
      component: () => import('@/views/contabilidad/menu2/index'),
      meta: { title: 'menu2' }
    }
  ]
}

export default fappRouter
