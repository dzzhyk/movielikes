<template>
  <el-container>
    <el-header style="padding-top: 10px;background-color: rgba(240,245,255,0.5)">
      <el-row gutter="20">
        <el-col :span="6" style="text-align: center;line-height: 36px">
          <el-button type="text" style="font-size: 20px">
          电影推荐网
          </el-button>
        </el-col>
        <el-col :span="18" >
          <el-button type="primary" @click="register" style="float:right;margin-left: 10px">
            注册
          </el-button>
          <el-button type="primary" @click="login" style="float:right">
            登录
          </el-button>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <div style="display: inline-block;margin-left: 10px;margin-top: 10px">
     <movie>

     </movie>
      </div>
      <div style="display: inline-block;margin-left: 10px;margin-top: 10px">
        <movie>

        </movie>
      </div>
      <div style="display: inline-block;margin-left: 10px;margin-top: 10px">
        <movie>

        </movie>
      </div>
      <div style="display: inline-block;margin-left: 10px;margin-top: 10px">
        <movie>

        </movie>
      </div>
      <div style="display: inline-block;margin-left: 10px;margin-top: 10px">
        <movie>

        </movie>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import Movie from "@/components/Movie";

export default {
  name: "HomePage",
  components: {Movie},
  methods:{
    login() {
      this.$router.push("/login");
    },
    register() {
      this.$router.push("/register");
    }
  }
}
</script>

<style lang="scss">
.el-row {
  margin-bottom: 20px;
&:last-child {
   margin-bottom: 0;
 }
}
.el-col {
  border-radius: 4px;
}
</style>
