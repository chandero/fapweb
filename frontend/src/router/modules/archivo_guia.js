/** When your routing table is too long, you can split it into small modules**/

import Layout from '@/views/layout/Layout'

const fappRouter = {
  path: '/contabilidad',
  component: Layout,
  redirect: '/contabilidad/libros/mayor',
  name: 'contabilidad',
  meta: {
    title: 'contabilidad',
    icon: 'contabilidad'
  },
  children: [
    {
      path: 'libros',
      component: () => import('@/views/contabilidad/libros/index'), // Parent router-view
      name: 'Libros',
      meta: { title: 'libros' },
      redirect: '/contabilidad/libros/mayor',
      children: [
        {
          path: 'mayor',
          component: () => import('@/views/contabilidad/libros/mayor'),
          name: 'Menu1-1',
          meta: { title: 'mayor' }
        },
        {
          path: 'menu1-2',
          component: () => import('@/views/contabilidad/libros/menu1-2'),
          name: 'Menu1-2',
          redirect: '/contabilidad/libros/menu1-2/menu1-2-1',
          meta: { title: 'menu1-2' },
          children: [
            {
              path: 'menu1-2-1',
              component: () => import('@/views/contabilidad/libros/menu1-2/menu1-2-1'),
              name: 'Menu1-2-1',
              meta: { title: 'menu1-2-1' }
            },
            {
              path: 'menu1-2-2',
              component: () => import('@/views/contabilidad/libros/menu1-2/menu1-2-2'),
              name: 'Menu1-2-2',
              meta: { title: 'menu1-2-2' }
            }
          ]
        },
        {
          path: 'menu1-3',
          component: () => import('@/views/contabilidad/libros/menu1-3'),
          name: 'Menu1-3',
          meta: { title: 'menu1-3' }
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
