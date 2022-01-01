<template>
    <div>
        <el-row>
            <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                <div class="head-poster">
                    <div style="font-size: 30px; color: white">欢迎！{{ userName }}</div>
                    <div style="font-size: 26px; color: white">在Movielikes，探索你的电影喜好并为其评分。</div>
                </div>
            </el-col>
        </el-row>
        <el-row>
            <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                <div v-if="userLogined">
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
                            <movie movie-release-date="2021-12-30" movie-name="Forrest Gummp Forrest" movie-score="5.0">
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
                <div v-if="userLogined">
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
                <div v-if="userLogined">
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
        <el-footer>
            <div style="padding-bottom: 20px; text-align: center">
                <span v-if="!userLogined"
                    ><a href="javascript:void();" @click="login" style="color: powderblue">现在加入</a>
                    Movielikes,发现更多影视可能。</span
                >
                <span v-else>你好, {{ userName }}! 在Movielikes, 发现更多影视可能。</span>
                <br />
                <span>Copyright © 2021 Movielikes All Rights Reserved.</span>
            </div>
        </el-footer>
    </div>
</template>

<script setup>
import Movie from "@/components/Movie";
import { useStore } from "vuex";
import cache from "@/plugins/cache";

const store = useStore();
const userName = cache.session.get("name");
const userLogined = cache.session.get("logined") === 'true' || store.getters.logined === true;

</script>

<style scoped>
.head-poster {
    background-image: url("@/assets/images/1.jpg");
    height: 180px;
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

.movie-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
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


.el-footer {
    display: flex;
    flex-direction: column;
    height: 100px;
    color: white;
    background-color: #0d243f;
    justify-content: flex-end;
    align-items: center;
    margin-top: 50px;
}
</style>
