<template>
    <el-container>
        <el-header>
            <el-row style="align-items: center">
                <el-col :xs="{ span: 14, offset: 0 }" :sm="{ span: 16, offset: 0 }" :md="{ span: 18, offset: 0 }">
                    <div style="color: white; font-size: 24px; padding-left: 12vw">Movielikes</div>
                </el-col>
                <el-col :xs="{ span: 10, offset: 0 }" :sm="{ span: 8, offset: 0 }" :md="{ span: 6, offset: 0 }">
                    <div style="padding-right: 10vw">
                        <el-button v-if="userLogined === 'false'" @click="login" style="float: right"> 登录 </el-button>
                        <div
                            v-if="userLogined === 'true'"
                            style="display: flex; flex-direction: row; align-items: center"
                        >
                            <div style="color: white; padding-right: 1vw">当前在线: {{ userName }}</div>
                            <el-button @click="logout" style="float: right"> 登出 </el-button>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </el-header>

        <el-main>
            <el-row>
                <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                    <div class="head-poster">
                        <div style="font-size: 36px; color: white">欢迎！{{ userName }}</div>
                        <div style="font-size: 32px; color: white">在Movielikes，探索你的电影喜好并为其评分。</div>
                        <div class="search-bar">
                            <el-autocomplete placeholder="搜索感兴趣的电影名称、imdb编号、tmdb编号" @select="" />
                        </div>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                    <div v-if="userLogined === 'true'">
                        <div class="movie-list-title">私人推荐</div>
                        <div class="movie-list-title-addon">每日新鲜电影推荐</div>
                        <el-divider :always="true"></el-divider>
                        <div class="movie-list">
                            <div v-for="count in 10" :key="count">
                                <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                    {{ count }}
                                </movie>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                    <div>
                        <div class="movie-list-title">历史热门</div>
                        <div class="movie-list-title-addon">经典的、最好的</div>
                        <el-divider :always="true"></el-divider>
                        <div class="movie-list">
                            <div v-for="count in 10" :key="count">
                                <movie
                                    movie-release-date="2021-12-30"
                                    movie-name="Forrest Gummp Forrest"
                                    movie-score="5.0"
                                >
                                    {{ count }}
                                </movie>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                    <div>
                        <div class="movie-list-title">最新热评</div>
                        <div class="movie-list-title-addon">大家都在看</div>
                        <el-divider :always="true"></el-divider>
                        <div class="movie-list">
                            <div v-for="count in 10" :key="count">
                                <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                    {{ count }}
                                </movie>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                    <div v-if="userLogined === 'true'">
                        <div class="movie-list-title">个人收藏</div>
                        <div class="movie-list-title-addon">不忘旧时光</div>
                        <el-divider :always="true"></el-divider>
                        <div class="movie-list">
                            <div v-for="count in 10" :key="count">
                                <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                    {{ count }}
                                </movie>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                    <div v-if="userLogined === 'true'">
                        <div class="movie-list-title">继续为电影评分</div>
                        <div class="movie-list-title-addon">帮助 Movielikes 不断改进</div>
                        <el-divider :always="true"></el-divider>
                        <div class="movie-list">
                            <div v-for="count in 10" :key="count">
                                <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                    {{ count }}
                                </movie>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <el-backtop />
        </el-main>
        <el-footer>
            <span v-if="userLogined === 'false'" style="padding-bottom: 20px"
                ><a href="javascript:void();" @click="login" style="color: powderblue">现在加入</a> Movielikes,
                发现更多影视可能。</span
            >
            <span v-else style="padding-bottom: 20px">你好, {{ userName }}! 在Movielikes, 发现更多影视可能。</span>
            <span style="padding-bottom: 20px">Copyright © 2021 Movielikes All Rights Reserved.</span>
        </el-footer>
    </el-container>
</template>

<script setup>
import Movie from "@/components/Movie";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import cache from "@/plugins/cache";
import { ElMessageBox } from "element-plus";

const store = useStore();
const router = useRouter();
const userName = cache.session.get("name");
const userLogined = cache.session.get("logined") || store.getters.logined;

function login() {
    router.push("/login");
}

function logout() {
    ElMessageBox.confirm("确定退出吗?", "Warning", {
        confirmButtonText: "OK",
        cancelButtonText: "Cancel",
        type: "warning",
    }).then(() => {
        store.dispatch("LogOut").then(() => {
            window.location.href = "/index";
        });
    });
}
</script>

<style lang="scss">
.el-header {
    background-color: #0d243f;
    height: 100%;
    padding: 10px;
    text-align: left;
    align-items: center;
}

.el-main {
    background-image: url("@/assets/images/bg.png");
    background-size: cover;
    background-repeat: no-repeat;
    background-attachment: fixed;
    background-position: left center;
    padding-top: 2px;
    padding-bottom: 40px;
}

.el-footer {
    display: flex;
    flex-direction: column;
    height: 100px;
    color: white;
    background-color: #0d243f;
    justify-content: flex-end;
    align-items: center;
}

.head-poster {
    background-image: url("@/assets/images/1.jpg");
    height: 240px;
    text-align: left;
    background-size: cover;
    background-repeat: no-repeat;
    display: flex;
    flex-direction: column;
    user-select: none;
    justify-content: space-evenly;
    align-items: flex-start;
    padding: 40px;
}

.search-bar {
    width: -webkit-fill-available;
    width: -moz-available;
}

.search-bar .el-autocomplete {
    width: inherit;
}

.movie-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
    align-items: center;
    gap: 15px;
}

.movie-list-title {
    padding-top: 30px;
    text-align: left;
    font-size: 24px;
    font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

.movie-list-title-addon {
    text-align: left;
    font-size: 16px;
    color: gray;
    font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
</style>
