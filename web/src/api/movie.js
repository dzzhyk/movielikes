import request from "@/utils/request";

// 获取电影详情
export function getMovieDetail(movieId) {
    return request({
        url: "/movie/detail/" + movieId,
        method: "get"
    });
}

// 获取电影列表
export function getMovieList(curr, size) {
    const data = {
        curr,
        size,
    };
    return request({
        url: "/movie/list",
        method: "get",
        params: data,
    });
}
