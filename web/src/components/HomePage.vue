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
    <el-main style="margin-left: 100px;margin-right: 50px;">
      <el-row style="margin-left: 5px">
        <el-col :span="22">
      <el-input  placeholder="Search" />
        </el-col>
        <el-col :span="2" style="float: left">
      <el-button :icon="Search" circle></el-button>
        </el-col>
      </el-row>
      <div style="float: left">
      <div style="font-size: 30px">
         历史热门
      </div>
      <div v-for="count in 10" style="display: inline-block;margin-left: 10px;margin-top: 10px;float:left;" :key="count">
     <movie movie-category="电影分类：Comedy" movie-name="电影名称：GTA5" movie-score="电影评分：5.0">
       {{count}}
     </movie>
      </div>
      </div>

      <div style="float: left">
        <div style="font-size: 30px">
          最近热门
        </div>
        <div v-for="count in 10" style="display: inline-block;margin-left: 10px;margin-top: 10px;float:left;" :key="count">
          <movie movie-category="电影分类：Comedy" movie-name="电影名称：GTA5" movie-score="电影评分：5.0">
            {{count}}
          </movie>
        </div>
      </div>

      <div style="float: left">
        <div style="font-size: 30px">
          为你推荐
        </div>
        <div v-for="count in 10" style="display: inline-block;margin-left: 10px;margin-top: 10px;float:left;" :key="count">
          <movie movie-category="电影分类：Comedy" movie-name="电影名称：GTA5" movie-score="电影评分：5.0">
            {{count}}
          </movie>
        </div>
      </div>



    </el-main>
  </el-container>
</template>

<script>
import Movie from "@/components/Movie";
import {Search} from "@element-plus/icons-vue";
export default {
  name: "HomePage",
  components: {Movie},
  data() {
    return {
      Search
    }
  },
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
