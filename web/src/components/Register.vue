<template>
    <div style="background-color: azure; height: 100%; margin: 0; padding: 0">
        <div class="login-box">
            <div class="login-box-title">
                <div
                    style="
                        user-select: none;
                        -webkit-user-drag: none;
                        padding-left: 30px;
                        display: inline-block;
                        font-size: 14px;
                        vertical-align: center;
                    "
                >
                    <a href="javascript:void(0);" @click="toLogin">
                        <div style="display: inline-block; vertical-align: middle">
                            <svg
                                class="icon"
                                width="16"
                                height="16"
                                viewBox="0 0 1024 1024"
                                xmlns="http://www.w3.org/2000/svg"
                                data-v-365b8594=""
                            >
                                <path fill="currentColor" d="M224 480h640a32 32 0 110 64H224a32 32 0 010-64z"></path>
                                <path
                                    fill="currentColor"
                                    d="M237.248 512l265.408 265.344a32 32 0 01-45.312 45.312l-288-288a32 32 0 010-45.312l288-288a32 32 0 1145.312 45.312L237.248 512z"
                                ></path>
                            </svg>
                        </div>
                        <div style="display: inline-block">返回登录</div>
                    </a>
                </div>
                <div
                    style="display: inline-block; width: 100%; text-align: center; margin-top: 10px; user-select: none"
                >
                    创建 Movielikes 账号
                </div>
            </div>
            <van-form @submit="onSubmit">
                <van-cell-group inset>
                    <van-field
                        v-model="username"
                        name="username"
                        label="用户名"
                        placeholder="用户名"
                        :rules="[{ required: true, message: '请填写用户名' }]"
                    />
                    <van-field
                        v-model="password"
                        type="password"
                        name="password"
                        label="密码"
                        placeholder="密码"
                        :rules="[{ required: true, message: '请填写密码' }]"
                    />
                    <van-field
                        v-model="repassword"
                        type="re-password"
                        name="re-password"
                        label="确认密码"
                        placeholder="确认密码"
                        :rules="[{ required: true, message: '请填写密码' }]"
                    />
                    <van-field
                        v-model="captcha"
                        type="text"
                        name="captcha"
                        label="验证码"
                        placeholder="请输入右侧验证码"
                        :rules="[{ required: true, message: '请正确填写验证码' }]"
                    />
                </van-cell-group>

            </van-form>
            <div style="text-align: center; margin-top: 20px; margin-bottom: 10px">
                <div style="width: 100px; display: inline-block">
                    <van-button round block type="success" @click="register"> 立即注册 </van-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from "vue";

export default {
    name: "Login",
    setup() {
        const username = ref("");
        const password = ref("");
        const repassword = ref("");
        const captcha = ref("");
        const onSubmit = (values) => {
            console.log("submit", values);
        };

        return {
            username,
            password,
            repassword,
            captcha,
            onSubmit,
        };
    },
    methods: {
        toLogin() {
            this.$router.push("/login");
        },
    },
};
</script>

<style scoped>
.login-box {
    padding: 10px;
    border-radius: 50px;
    box-shadow: rgba(0, 0, 0, 0.274) 0px 0px 10px 4px;
    position: absolute;
    background: #ffffff;
    left: 50%;
    top: 50%;
    width: 400px;
    transform: translate(-50%, -50%);
}
.login-box-title {
    font-size: 20px;
    font-family: Arial, "Helvetica Neue", Helvetica, sans-serif;
    text-align: left;
    margin-top: 20px;
    margin-bottom: 20px;
}

.van-field {
    user-select: none;
}
</style>
