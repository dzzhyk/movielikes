import request from '@/utils/request'

// 查询用户个人信息
export function getUserProfile() {
    return request({
        url: "/user/profile",
        method: "get",
    });
}

// 更新用户个人信息
export function updateUserProfile(data) {
    return request({
        url: "/user/profile",
        method: "put",
        data: data,
    });
}

// 用户密码重置
export function updateUserPwd(newPassword) {
    const data = {
        newPassword,
    };
    return request({
        url: "/user/pwd",
        method: "put",
        params: data,
    });
}

// 获取用户收藏
export function getUserCollection() {
    return request({
        url: "/user/collection",
        method: "get"
    });
}