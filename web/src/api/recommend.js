import request from "@/utils/request";

// 历史热门
export function getMost() {
    return request({
        url: "/recommend/most",
        method: "get"
    });
}

// 最新热评
export function getRank() {
    return request({
        url: "/recommend/rank",
        method: "get"
    });
}

// 私人推荐
export function getUserRecommend() {
    return request({
        url: "/recommend/user",
        method: "get"
    });
}