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
            注册
          </van-button>
        </div>
        <div style="margin: 16px;">
          <van-button round block type="primary" @click="login">
            去登录
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: "Register",
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
    login() {
      this.$router.push("/login");
    }
  }
};
</script>

<style>

</style>
