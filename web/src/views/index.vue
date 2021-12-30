<template>
    <el-container>
        <el-header>
            <div style="color: white; font-size: 24px; vertical-align: middle">Movielikes</div>
            <div>
                <el-button
                    v-if="userToken === undefined || userToken === ''"
                    @click="login"
                    style="float: right; margin-left: 20px"
                >
                    登录
                </el-button>
                <div v-else style="display: flex; flex-direction: row; align-items: center; gap: 10px">
                    <div style="color: white">当前在线: {{ userName }}</div>
                    <el-button @click="logout" style="float: right"> 登出 </el-button>
                </div>
            </div>
        </el-header>
        <el-main style="display: flex; flex-direction: column">
            <div>
                <div class=""></div>
            </div>
            <div v-if="userToken !== undefined && userToken !== ''">
                <div class="movie-list-title">私人推荐</div>
                <div class="movie-list-title-addon">每日10部新鲜电影推荐</div>
                <el-divider :always="true"></el-divider>
                <el-scrollbar>
                    <div class="movie-list">
                        <div v-for="count in 10" :key="count">
                            <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                {{ count }}
                            </movie>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

            <div>
                <div class="movie-list-title">历史热门</div>
                <div class="movie-list-title-addon">经典的、最好的</div>
                <el-divider :always="true"></el-divider>
                <el-scrollbar>
                    <div class="movie-list">
                        <div v-for="count in 10" :key="count">
                            <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                {{ count }}
                            </movie>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

            <div>
                <div class="movie-list-title">最新趋势</div>
                <div class="movie-list-title-addon">大家都在看</div>
                <el-divider :always="true"></el-divider>
                <el-scrollbar>
                    <div class="movie-list">
                        <div v-for="count in 10" :key="count">
                            <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                {{ count }}
                            </movie>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

            <div v-if="userToken !== undefined && userToken !== ''">
                <div class="movie-list-title">个人收藏</div>
                <div class="movie-list-title-addon">不忘旧时光</div>
                <el-divider :always="true"></el-divider>
                <el-scrollbar>
                    <div class="movie-list">
                        <div v-for="count in 10" :key="count">
                            <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                {{ count }}
                            </movie>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

            <div v-if="userToken !== undefined && userToken !== ''">
                <div class="movie-list-title">继续为电影评分</div>
                <div class="movie-list-title-addon">帮助Movielikes提升</div>
                <el-divider :always="true"></el-divider>
                <el-scrollbar>
                    <div class="movie-list">
                        <div v-for="count in 10" :key="count">
                            <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp" movie-score="5.0">
                                {{ count }}
                            </movie>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

        </el-main>
        <el-footer>
            <span v-if="userToken === undefined || userToken === ''" style="padding-bottom: 20px"
                ><a href="javascript:void();" @click="login" style="color: powderblue">立即加入</a> Movielikes ,
                发现更多影视可能。</span
            >
            <span v-else style="padding-bottom: 20px">你好, {{ userName }}! 在Movielikes, 发现更多影视可能。</span>
            <span style="padding-bottom: 20px">Copyright © 2021 Movielikes All Rights Reserved.</span>
        </el-footer>
    </el-container>
</template>

<script setup>
import Movie from "@/components/Movie";
import { useRouter } from "vue-router";

const store = useStore();
const router = useRouter();
const userToken = store.getters.token;
const userName = store.getters.name;

function login() {
    router.push("/login");
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
    padding-left: 250px;
    padding-right: 250px;
    display: flex;
    flex-direction: row;
    background-color: #0d243f;
    flex-wrap: nowrap;
    justify-content: space-between;
    align-items: center;
}

.el-main {
    padding-left: 250px;
    padding-right: 250px;
}

.el-footer {
    display: flex;
    flex-direction: column;
    margin-top: 40px;
    height: 100px;
    color: white;
    background-color: #0d243f;
    justify-content: flex-end;
    align-items: center;
}

.movie-list {
    display: flex;
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
