<template>
    <div style="background-color: azure; height: 100%; margin: 0; padding: 0">
        <div class="login-box">
            <div class="login-box-title">登录到 Movielikes</div>
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
                        v-model="captcha"
                        type="text"
                        name="captcha"
                        label="验证码"
                        placeholder="请输入右侧验证码"
                        :rules="[{ required: true, message: '请正确填写验证码' }]"
                    />
                </van-cell-group>
            </van-form>
            <div style="text-align: center; margin-top: 20px; margin-bottom: 20px">
                <div style="width: 100px; display: inline-block">
                    <van-button round block type="primary" @click="register"> 立即注册 </van-button>
                </div>
                <div style="margin-left: 20px; width: 100px; display: inline-block">
                    <van-button round block type="success" native-type="submit"> 登录 </van-button>
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
        const captcha = ref("");
        const onSubmit = (values) => {
            console.log("submit", values);
        };

        return {
            username,
            password,
            captcha,
            onSubmit,
        };
    },
    methods: {
        register() {
            this.$router.push("/register");
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
    text-align: center;
    margin-top: 20px;
    margin-bottom: 20px;
    user-select: none;
}

.van-field{
  user-select: none;
}
</style>
