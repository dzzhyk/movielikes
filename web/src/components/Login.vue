<template>
  <div class="box1">
    <div class="box2">
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
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        登录
      </van-button>
    </div>
  </van-form>
      <div style="margin: 16px;">
      <van-button round block type="primary" @click="register">
        去注册
      </van-button>
      </div>
  </div>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: "Login",
  setup() {
    const username = ref('');
    const password = ref('');
    const onSubmit = (values) => {
      console.log('submit', values);
    };

    return {
      username,
      password,
      onSubmit,
    };
  },
  methods:{
    register() {
      this.$router.push("/register");
    }
  }
};
</script>

<style>

.box1{
  width: auto;
  height: auto;
  background-color: rgba(242, 248, 242, 0.5);
  margin:auto;
  line-height: 500px;
  text-align: center;
  position: absolute;
  font-size: 0;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}
.box2{
  background: #ffffff;
  display: inline-block;
  width: 400px;
}
</style>
