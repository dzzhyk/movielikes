<template>
    <div>
        <el-row>
            <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
                <div class="head-poster">
                    <div v-if="!userLogined" style="font-size: 32px; color: white">
                        欢迎!
                        <a class="welcome-login-link" href="javascript:void(0);" @click="this.$router.push('/login')"
                            >点击登录</a
                        >
                    </div>
                    <div v-if="userLogined" style="font-size: 32px; color: white">欢迎! {{ userName }}</div>
                    <div style="font-size: 26px; color: white">在Movielikes, 探索你的电影喜好并为其评分。</div>
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
                        <div v-for="m in data_user">
                            <movie
                                :movieId="m.movieId"
                                :title="m.title"
                                :release="m.release"
                                :posterPath="m.posterPath"
                                :avgRating="m.avgRating"
                            ></movie>
                        </div>
                        <div v-if="!data_user">Hi~ 尚未有你喜欢的推荐结果，请为电影评分以解锁私人推荐~</div>
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
                        <div v-for="m in data_most">
                            <movie
                                :movieId="m.movieId"
                                :title="m.title"
                                :release="m.release"
                                :posterPath="m.posterPath"
                                :avgRating="m.avgRating"
                            ></movie>
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
                        <div v-for="m in data_rank">
                            <movie
                                :movieId="m.movieId"
                                :title="m.title"
                                :release="m.release"
                                :posterPath="m.posterPath"
                                :avgRating="m.avgRating"
                            ></movie>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
        <el-footer>
            <div style="padding-bottom: 20px; text-align: center">
                <span v-if="!userLogined"
                    ><a href="javascript:void(0);" @click="this.$router.push('/login')" style="color: powderblue"
                        >现在加入</a
                    >
                    Movielikes, 发现更多影视可能。</span
                >
                <span v-else>你好, {{ userName }}! 在Movielikes, 发现更多影视可能。</span>
                <br />
                <span>Copyright © 2021 Movielikes All Rights Reserved.</span>
            </div>
        </el-footer>
    </div>
</template>

<script>
import Movie from "@/components/Movie";
import cache from "@/plugins/cache";
import { getMost, getRank, getUserRecommend } from "@/api/recommend";

export default {
    data() {
        return {
            userName: "",
            userLogined: false,
            data_user: [],
            data_most: [],
            data_rank: [],
        };
    },
    components: {
        Movie,
    },
    mounted() {
        this.userName = cache.session.get("name") || "";
        this.userLogined = cache.session.get("logined") === "true" || this.$store.getters.logined === true;
        this.loadMost();
        this.loadRank();
        if (this.userLogined) {
            this.loadUserRecommend();
        }
    },
    methods: {
        loadMost() {
            getMost()
                .then((resp) => {
                    this.data_most = resp.data[0];
                })
                .catch((err) => {});
        },
        loadRank() {
            getRank()
                .then((resp) => {
                    this.data_rank = resp.data[0];
                })
                .catch((err) => {});
        },
        loadUserRecommend() {
          getUserRecommend()
              .then((resp) => {
                this.data_user = resp.data[0];
              })
              .catch((err) => {});
        },
    },
};
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
    box-shadow: 0 1px 3px rgb(0 0 0 / 50%);
    border-radius: 7px;
}

.welcome-login-link {
    cursor: pointer;
    user-select: none;
    text-decoration: none;
    color: #50afde;
}
.welcome-login-link:focus,
.welcome-login-link:hover {
    color: #1c83b4;
    text-decoration: none;
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
