import { login, logout, getInfo } from "@/api/login";
import { getToken, setToken, removeToken } from "@/utils/auth";

const user = {
    state: {
        token: getToken(),
        name: "",
    },

    mutations: {
        SET_TOKEN: (state, token) => {
            state.token = token;
        },
        SET_NAME: (state, name) => {
            state.name = name;
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
                        commit("SET_TOKEN", "");
                        removeToken();
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
