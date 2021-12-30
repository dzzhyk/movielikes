import { createWebHistory, createRouter } from "vue-router";

// 公共路由
const constantRoutes = [
    {
        path: "/",
        component: () => import("@/views/index.vue"),
    },
    {
        path: "/index",
        component: () => import("@/views/index.vue"),
    },
    {
        path: "/login",
        component: () => import("@/views/login.vue"),
        hidden: true,
    },
    {
        path: "/register",
        component: () => import("@/views/register.vue"),
        hidden: true,
    },
    {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue"),
    },
    {
        path: "/:pathMatch(.*)*",
        component: () => import("@/views/error/404.vue"),
        hidden: true,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes: constantRoutes,
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition;
        } else {
            return { top: 0 };
        }
    },
});

export default router;
