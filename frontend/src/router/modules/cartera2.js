/** When your routing table is too long, you can split it into small modules**/

import Layout from "@/views/layout/Layout";

const cartera2Router = {
  path: "/menu_cartera",
  component: Layout,
  redirect: "/cartera2/menu1proceso/menu1-1liquidacionnormal",
  name: "menu_cartera",
  meta: {
    title: "menu_cartera",
    icon: "el-icon-suitcase-1",
    roles: ["admin", "cartera", "revisor", "gerencia", "pe"]
  },
  children: [
    {
      path: "menu1proceso",
      component: () => import("@/views/cartera2/menu1proceso/index"), // Parent router-view
      name: "menu_cartera_menu1proceso",
      meta: {
        title: "menu_cartera_menu1proceso",
        icon: "el-icon-c-scale-to-original",
        roles: ["admin", "cartera", "gerencia"]
      },
      children: [
        {
          path: "menu1-1liquidacionnormal",
          component: () =>
            import("@/views/cartera2/menu1proceso/menu1-1liquidacionnormal"),
          name: "menu_cartera_menu1proceso_menu1-1liquidacionnormal",
          meta: {
            title: "menu_cartera_menu1proceso_menu1-1liquidacionnormal",
            icon: "calcauto",
            roles: ["admin", "cartera", "gerencia"]
          }
        },
        {
          path: "menu1-2liquidacionmanual",
          component: () =>
            import("@/views/cartera2/menu1proceso/menu1-2liquidacionmanual"),
          name: "menu_cartera_menu1proceso_menu1-2liquidacionmanual",
          meta: {
            title: "menu_cartera_menu1proceso_menu1-2liquidacionmanual",
            icon: "calcmanual",
            roles: ["admin", "cartera", "gerencia"]
          }
        }
      ]
    },
    {
      path: "menu2gestion",
      component: () => import("@/views/cartera2/menu2gestion/index"), // Parent router-view
      name: "menu_cartera_menu2gestion",
      meta: {
        title: "menu_cartera_menu2gestion",
        icon: "el-icon-sunrise-1",
        roles: ["admin", "cartera", "gerencia"]
      },
      children: [
        {
          path: "menu2-1controlcobro",
          component: () =>
            import("@/views/cartera2/menu2gestion/menu2-1controlcobro"),
          name: "menu_cartera_menu2gestion_menu2-1controlcobro",
          meta: {
            title: "menu_cartera_menu2gestion_menu2-1controlcobro",
            icon: "el-icon-service",
            roles: ["admin", "cartera", "gerencia"]
          }
        },
        {
          path: "menu2-2periodogracia",
          component: () =>
            import("@/views/cartera2/menu2gestion/menu2-2periodogracia"),
          name: "menu_cartera_menu2gestion_menu2-2periodogracia",
          meta: {
            title: "menu_cartera_menu2gestion_menu2-2periodogracia",
            icon: "el-icon-crop",
            roles: ["admin", "gerencia"]
          }
        }
      ]
    },
    {
      path: "menu3causacion",
      component: () => import("@/views/cartera2/menu3causacion/index"), // Parent router-view
      name: "menu_cartera_menu3causacion",
      meta: {
        title: "menu_cartera_menu3causacion",
        icon: "table",
        roles: ["admin", "cartera", "revisor", "gerencia"]
      },
      children: [
        {
          path: "menu3-1proceso",
          component: () =>
            import("@/views/cartera2/menu3causacion/menu3-1proceso"),
          name: "menu_cartera_menu3causacion_menu3-1proceso",
          meta: {
            title: "menu_cartera_menu3causacion_menu3-1proceso",
            icon: "process2",
            roles: ["admin", "cartera", "gerencia"]
          }
        },
        {
          path: "menu3-2consulta",
          component: () =>
            import("@/views/cartera2/menu3causacion/menu3-2consulta"),
          name: "menu_cartera_menu3causacion_menu3-2consulta",
          meta: {
            title: "menu_cartera_menu3causacion_menu3-2consulta",
            icon: "eye-open",
            roles: ["admin", "cartera", "revisor", "gerencia"]
          }
        }
      ]
    },
    {
      path: "menu4informe",
      component: () => import("@/views/cartera2/menu4informe/index"), // Parent router-view
      name: "menu_cartera_menu4informe",
      meta: {
        title: "menu_cartera_menu4informe",
        icon: "pdf",
        roles: ["admin", "cartera", "gerencia"]
      },
      redirect: "/cartera2/menu4informe/menu4-1recaudoxmes",
      children: [
        {
          path: "menu4-1recaudoxmes",
          component: () =>
            import("@/views/cartera2/menu4informe/menu4-1recaudoxmes"),
          name: "menu_cartera_menu4informe_menu4-1recaudoxmes",
          meta: {
            title: "menu_cartera_menu4informe_menu4-1recaudoxmes",
            icon: "coin2",
            roles: ["admin", "cartera", "gerencia"]
          }
        },
        {
          path: "menu4-2recaudointerescausado",
          component: () =>
            import(
              "@/views/cartera2/menu4informe/menu4-2recaudointerescausado"
            ),
          name: "menu_cartera_menu4informe_menu4-2recaudointerescausado",
          meta: {
            title: "menu_cartera_menu4informe_menu4-2recaudointerescausado",
            icon: "coin5",
            roles: ["admin", "cartera", "gerencia"]
          }
        },
        {
          path: "menu4-3bancolombia",
          component: () =>
            import("@/views/cartera2/menu4informe/menu4-3bancolombia"),
          name: "menu_cartera_menu4informe_menu4-3bancolombia",
          meta: {
            title: "menu_cartera_menu4informe_menu4-3bancolombia",
            icon: "coin5",
            roles: ["admin", "cartera", "gerencia"]
          }
        }
      ]
    },
    {
      path: "menu5consulta",
      component: () => import("@/views/cartera2/menu5consulta/index"), // Parent router-view
      name: "menu_cartera_menu5consulta",
      meta: {
        title: "menu_cartera_menu5consulta",
        icon: "eye-open",
        roles: ["admin", "cartera", "revisor", "gerencia"]
      },
      children: [
        {
          path: "menu5-1colocacion",
          component: () =>
            import("@/views/cartera2/menu5consulta/menu5-1colocacion"),
          name: "menu_cartera_menu5consulta_menu5-1colocacion",
          meta: {
            title: "menu_cartera_menu5consulta_menu5-1colocacion",
            icon: "banknote2",
            roles: ["admin", "cartera", "revisor", "gerencia"]
          }
        }
      ]
    },
    {
      path: "menu6pe",
      component: () => import("@/views/cartera2/menu6pe/index"), // Parent router-view
      name: "menu_cartera_menu6pe",
      meta: {
        title: "menu_cartera_menu6pe",
        icon: "el-icon-data-board",
        roles: ["pe"]
      },
      children: [
        {
          path: "menu6-0parametro",
          component: () => import("@/views/cartera2/menu6pe/menu6-0parametro"),
          name: "menu_cartera_menu6pe_menu6-0parametro",
          meta: {
            title: "menu_cartera_menu6pe_menu6-0parametro",
            icon: "el-icon-setting",
            roles: ["pe"]
          },
          children: [
            {
              path: "menu6-0-1",
              component: () =>
                import("@/views/cartera2/menu6pe/menu6-0parametro/menu6-0-1"),
              name: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-1",
              meta: {
                title: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-1",
                icon: "el-icon-setting",
                roles: ["pe"]
              }
            },
            {
              path: "menu6-0-2",
              component: () =>
                import("@/views/cartera2/menu6pe/menu6-0parametro/menu6-0-2"),
              name: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-2",
              meta: {
                title: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-2",
                icon: "el-icon-setting",
                roles: ["pe"]
              }
            },
            {
              path: "menu6-0-3",
              component: () =>
                import("@/views/cartera2/menu6pe/menu6-0parametro/menu6-0-3"),
              name: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-3",
              meta: {
                title: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-3",
                icon: "el-icon-setting",
                roles: ["pe"]
              }
            },
            {
              path: "menu6-0-4",
              component: () =>
                import("@/views/cartera2/menu6pe/menu6-0parametro/menu6-0-4"),
              name: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-4",
              meta: {
                title: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-4",
                icon: "el-icon-setting",
                roles: ["pe"]
              }
            },
            {
              path: "menu6-0-5",
              component: () =>
                import("@/views/cartera2/menu6pe/menu6-0parametro/menu6-0-5"),
              name: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-5",
              meta: {
                title: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-5",
                icon: "el-icon-setting",
                roles: ["pe"]
              }
            },
            {
              path: "menu6-0-6",
              component: () =>
                import("@/views/cartera2/menu6pe/menu6-0parametro/menu6-0-6"),
              name: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-6",
              meta: {
                title: "menu_cartera_menu6pe_menu6-0parametro-menu6-0-6",
                icon: "el-icon-setting",
                roles: ["pe"]
              }
            }
          ]
        },
        {
          path: "menu6-1carga",
          component: () => import("@/views/cartera2/menu6pe/menu6-1carga"),
          name: "menu_cartera_menu6pe_menu6-1carga",
          meta: {
            title: "menu_cartera_menu6pe_menu6-1carga",
            icon: "el-icon-upload2",
            roles: ["pe"]
          }
        },
        {
          path: "menu6-2proceso",
          component: () => import("@/views/cartera2/menu6pe/menu6-2proceso"),
          name: "menu_cartera_menu6pe_menu6-2proceso",
          meta: {
            title: "menu_cartera_menu6pe_menu6-2proceso",
            icon: "el-icon-data-analysis",
            roles: ["pe"]
          }
        }
      ]
    }
  ]
};

export default cartera2Router;
