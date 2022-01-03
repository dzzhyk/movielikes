import router from "@/router";
import "nprogress/nprogress.css";
import {getToken} from "@/utils/auth";

const whiteList = ["/login", "/index", "/register", "/"];

// 路由守卫
router.beforeEach((to, from, next) => {
    if (getToken()) {
        if (to.path === "/login") {
            next({path: "/index"});
        } else {
            next();
        }
    } else {
        // 没有token
        if (whiteList.indexOf(to.path) !== -1) {
            // 在免登录白名单，直接进入
            next();
        } else {
            next(`/login?target=${to.fullPath}`); // 否则全部重定向到登录页
        }
    }
});

router.afterEach(() => {

});
