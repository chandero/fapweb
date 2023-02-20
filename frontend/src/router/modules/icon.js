import Layout from '@/views/layout/Layout'

const iconRouter = {
        path: '/icon',
        component: Layout,
        hidden: true,
        children: [
          {
            path: 'index',
            component: () => import('@/views/svg-icons/index'),
            name: 'Icons',
            meta: { title: 'Icons', icon: 'icon', noCache: true }
          }
        ]
}

export default iconRouter