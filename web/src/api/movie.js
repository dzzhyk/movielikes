import request from "@/utils/request";

// 获取电影详情
export function getMovieDetail(movieId) {
    return request({
        url: "/movie/detail/" + movieId,
        method: "get",
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

// 获取当前用户对该电影的评分
export function checkUserRating(movieId) {
    return request({
        url: "/movie/rating/" + movieId,
        method: "get",
    });
}

// 更新当前用户对该电影的评分
export function updateUserRating(movieId, rating) {
    const data = {
        rating,
    };
    return request({
        url: "/movie/rating/" + movieId,
        method: "put",
        params: data,
    });
}
