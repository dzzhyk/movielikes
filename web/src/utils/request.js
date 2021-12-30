import axios from "axios";
import { ElNotification, ElMessageBox, ElMessage, ElLoading } from "element-plus";
import store from "@/store";
import { getToken } from "@/utils/auth";
import errorCode from "@/utils/errorCode";
import { saveAs } from "file-saver";

let downloadLoadingInstance;

axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8";

// 创建axios实例
const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: import.meta.env.VITE_APP_BASE_API,
    // 超时
    timeout: 10000,
});

// request拦截器
service.interceptors.request.use(
    (config) => {
        config.headers["Movielikes-Token"] = "Movielikes " + getToken(); // 让每个请求携带自定义token
        // get请求映射params参数
        if (config.method === "get" && config.params) {
            let url = config.url + "?" + tansParams(config.params);
            url = url.slice(0, -1);
            config.params = {};
            config.url = url;
        }
        return config;
    },
    (error) => {
        console.log(error);
        Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (res) => {
        // 未设置状态码则默认成功状态
        const code = res.data.code || 200;
        // 获取错误信息
        const msg = errorCode[code] || res.data.msg || errorCode["default"];
        // 二进制数据则直接返回
        if (res.request.responseType === "blob" || res.request.responseType === "arraybuffer") {
            return res.data;
        }
        if (code === 401) {
            ElMessageBox.confirm("登录状态已过期，您可以继续留在该页面，或者重新登录", "系统提示", {
                confirmButtonText: "重新登录",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    store.dispatch("LogOut").then(() => {
                        window.location.href = "/index";
                    });
                })
                .catch(() => {});
            return Promise.reject("无效的会话，或者会话已过期，请重新登录。");
        } else if (code === 500) {
            ElMessage({
                message: msg,
                type: "error",
            });
            return Promise.reject(new Error(msg));
        } else if (code !== 200) {
            ElNotification.error({
                title: msg,
            });
            return Promise.reject("error");
        } else {
            return Promise.resolve(res.data);
        }
    },
    (error) => {
        console.log("err" + error);
        let { message } = error;
        if (message == "Network Error") {
            message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
            message = "系统接口请求超时";
        } else if (message.includes("Request failed with status code")) {
            message = "系统接口" + message.substr(message.length - 3) + "异常";
        }
        ElMessage({
            message: message,
            type: "error",
            duration: 5 * 1000,
        });
        return Promise.reject(error);
    }
);

// 通用下载方法
export function download(url, params, filename) {
    downloadLoadingInstance = ElLoading.service({ text: "正在下载数据，请稍候", background: "rgba(0, 0, 0, 0.7)" });
    return service
        .post(url, params, {
            transformRequest: [
                (params) => {
                    return tansParams(params);
                },
            ],
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            responseType: "blob",
        })
        .then(async (data) => {
            const isLogin = await blobValidate(data);
            if (isLogin) {
                const blob = new Blob([data]);
                saveAs(blob, filename);
            } else {
                ELMessage.error("无效的会话，或者会话已过期，请重新登录。");
            }
            downloadLoadingInstance.close();
        })
        .catch((r) => {
            console.error(r);
            ELMessage.error("下载文件出现错误，请联系管理员！");
            downloadLoadingInstance.close();
        });
}

/**
 * 参数处理
 * @param {*} params  参数
 */
export function tansParams(params) {
    let result = "";
    for (const propName of Object.keys(params)) {
        const value = params[propName];
        var part = encodeURIComponent(propName) + "=";
        if (value !== null && typeof value !== "undefined") {
            if (typeof value === "object") {
                for (const key of Object.keys(value)) {
                    if (value[key] !== null && typeof value[key] !== "undefined") {
                        let params = propName + "[" + key + "]";
                        var subPart = encodeURIComponent(params) + "=";
                        result += subPart + encodeURIComponent(value[key]) + "&";
                    }
                }
            } else {
                result += part + encodeURIComponent(value) + "&";
            }
        }
    }
    return result;
}

// 返回项目路径
export function getNormalPath(p) {
    if (p.length === 0 || !p || p == "undefined") {
        return p;
    }
    let res = p.replace("//", "/");
    if (res[res.length - 1] === "/") {
        return res.slice(0, res.length - 1);
    }
    return res;
}

// 验证是否为blob格式
export async function blobValidate(data) {
    try {
        const text = await data.text();
        JSON.parse(text);
        return false;
    } catch (error) {
        return true;
    }
}

export default service;
