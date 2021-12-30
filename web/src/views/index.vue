<template>
    <el-container>
        <el-header>
            <el-row>
                <el-col :span="6" style="text-align: center; line-height: 60px">
                    <el-button type="text" style="font-size: 20px">Movielikes</el-button>
                </el-col>
                <el-col :span="16" style="text-align: center; height: 60px; margin-top: 10px">
                    <el-button type="primary" @click="login" style="float: right; margin-left: 10px"> 登录 </el-button>
                    <el-button type="primary" @click="logout" style="float: right"> 登出 </el-button>
                </el-col>
            </el-row>
        </el-header>
        <el-main style="display: flex; flex-direction: column; gap: 30px">
            <div style="font-size: 30px">为你推荐</div>
            <div class="movie-list">
                <div
                    style="padding: 1px; border-radius: 12px; box-shadow: rgb(0 0 0 / 30%) 0px 4px 3px 0px"
                    v-for="count in 10"
                    :key="count"
                >
                    <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                        {{ count }}
                    </movie>
                </div>
            </div>

            <div style="font-size: 30px">历史热门</div>
            <div class="movie-list">
                <div
                    style="padding: 1px; border-radius: 12px; box-shadow: rgb(0 0 0 / 30%) 0px 4px 3px 0px"
                    v-for="count in 10"
                    :key="count"
                >
                    <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                        {{ count }}
                    </movie>
                </div>
            </div>

            <div style="font-size: 30px">最近热门</div>
            <div class="movie-list">
                <div
                    style="padding: 1px; border-radius: 12px; box-shadow: rgb(0 0 0 / 30%) 0px 4px 3px 0px"
                    v-for="count in 10"
                    :key="count"
                >
                    <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                        {{ count }}
                    </movie>
                </div>
            </div>
        </el-main>
        <el-footer>
            <div>movielikes.org</div>
        </el-footer>
    </el-container>
</template>

<script setup>
import Movie from "@/components/Movie";

const store = useStore();

function login() {
    this.$router.push("/login");
}

function logout() {
    if (confirm("确定退出吗?")) {
        store.dispatch("LogOut").then(() => {
            window.location.href = "/index";
        });
    }
}
</script>

<style lang="scss">
.el-header {
    padding-top: 10px;
    background-color: rgba(240, 245, 255, 0.5);
    vertical-align: center;
}
.el-footer {
    background-color: rgba(240, 245, 255, 0.5);
    vertical-align: center;
}

.movie-list {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 20px;
}
</style>
