import { createWebHistory, createRouter } from "vue-router";
import Layout from "@/layout";

// 公共路由
const constantRoutes = [
    {
        path: "/redirect",
        component: Layout,
        hidden: true,
        children: [
            {
                path: "/redirect/:path(.*)",
                component: () => import("@/views/redirect/index.vue"),
            },
        ],
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
        path: "/:pathMatch(.*)*",
        component: () => import("@/views/error/404.vue"),
        hidden: true,
    },
    {
        path: "",
        component: Layout,
        redirect: "/index",
        children: [
            {
                path: "/index",
                component: () => import("@/views/index.vue"),
                name: "Index",
            },
            {
                path: "/profile",
                component: () => import("@/views/profile.vue"),
                name: "Profile",
            },
            {
                path: "/collection",
                component: () => import("@/views/collection.vue"),
                name: "Collection",
            },
            {
                path: "/detail/:movieId",
                component: () => import("@/views/detail.vue"),
                name: "Detail",
            },
        ],
    }
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
