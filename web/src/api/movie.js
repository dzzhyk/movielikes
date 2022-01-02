import request from '@/utils/request'

// 获取电影列表
export function getMovieList(curr, size) {
    const data = {
        curr,
        size
    }
    return request({
        url: "/movie/list",
        method: "get",
        params: data
    });
}