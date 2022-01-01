import { login, logout, getInfo } from "@/api/login";
import { getToken, setToken, removeToken } from "@/utils/auth";
import cache from "@/plugins/cache"
import { ElMessage } from "element-plus";

const user = {
    state: {
        logined: false,
        token: getToken(),
        name: null,
    },

    mutations: {
        SET_LOGIN: (state, logined) => {
            state.logined = logined;
            cache.session.set("logined", logined)
        },
        SET_TOKEN: (state, token) => {
            state.token = token;
            cache.session.set("token", token)
        },
        SET_NAME: (state, name) => {
            state.name = name;
            cache.session.set("name", name)
        },
    },

    actions: {
        // 登录
        Login({ commit }, userInfo) {
            const username = userInfo.username.trim();
            const password = userInfo.password;
            const code = userInfo.code;
            const uuid = userInfo.uuid;
            return new Promise((resolve, reject) => {
                login(username, password, code, uuid)
                    .then((res) => {
                        setToken(res.token);
                        commit("SET_TOKEN", res.token);
                        commit("SET_LOGIN", true);
                        ElMessage({
                            message: "登录成功",
                            type: "success",
                        });
                        resolve();
                    })
                    .catch((error) => {
                        reject(error);
                    });
            });
        },

        // 获取用户信息
        GetInfo({ commit, state }) {
            return new Promise((resolve, reject) => {
                getInfo()
                    .then((res) => {
                        const user = res.user;
                        commit("SET_NAME", user.userName);
                        resolve(res);
                    })
                    .catch((error) => {
                        reject(error);
                    });
            });
        },

        // 退出系统
        LogOut({ commit, state }) {
            console.log("执行退出")
            return new Promise((resolve, reject) => {
                logout()
                    .then(() => {
                        cache.session.remove("token")
                        cache.session.remove("name")
                        commit("SET_TOKEN", null);
                        commit("SET_NAME", null);
                        commit("SET_LOGIN", false);
                        removeToken();
                        ElMessage({
                            message: "已登出",
                            type: "success",
                        });
                        resolve();
                    })
                    .catch((error) => {
                        reject(error);
                    });
            });
        },
    },
};

export default user;
