<template>
    <el-row>
        <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
            <div>
                <div class="movie-list-title">电影详情</div>
                <el-divider :always="true"></el-divider>
                <div style="display: flex; justify-content: center">
                    <detail-board :userLogined="this.userLogined"
                        :movieId="this.movie_info.movie.movieId"
                        :title="this.movie_info.movie.title"
                        :genres="this.movie_info.movie.genres"
                        :imdbId="this.movie_info.movie.imdbId"
                        :tmdbId="this.movie_info.movie.tmdbId"
                        :release="this.movie_info.movie.release"
                        :runtime="this.movie_info.movie.runtime"
                        :overview="this.movie_info.movie.overview"
                        :posterPath="this.movie_info.movie.posterPath"
                        :avgRating="this.movie_info.avgRating"
                    ></detail-board>
                </div>
            </div>
        </el-col>
    </el-row>
    <el-row>
        <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
            <div>
                <div class="movie-list-title">相似电影</div>
                <el-divider :always="true"></el-divider>
                <div class="movie-list">
                    <div v-for="m in this.movie_info.simMovies">
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
                ><a href="javascript:void();" @click="this.$router.push('/login')" style="color: powderblue"
                    >现在加入</a
                >
                Movielikes, 发现更多影视可能。</span
            >
            <span v-else>你好, {{ userName }}! 在Movielikes, 发现更多影视可能。</span>
            <br />
            <span>Copyright © 2021 Movielikes All Rights Reserved.</span>
        </div>
    </el-footer>
</template>

<script>
import Movie from "@/components/Movie";
import DetailBoard from "@/components/DetailBoard";
import { ElMessage } from "element-plus";
import { getMovieDetail } from "@/api/movie";
import cache from "@/plugins/cache";

export default {
    data() {
        return {
            userName: "",
            userLogined: false,
            movie_info: {
                movie: {
                    movieId: -1,
                    title: "",
                    genres: "",
                    imdbId: "",
                    tmdbId: "",
                    release: "",
                    runtime: 0,
                    overview: "",
                    posterPath: ""
                },
                simMovies: [],
                avgRating: "0",
            },
        };
    },
    components: {
        Movie,
        DetailBoard,
    },
    mounted() {
        this.userName = cache.session.get("name") || "";
        this.userLogined = cache.session.get("logined") === "true" || this.$store.getters.logined === true;
        let { params, query } = this.$route;
        let { movieId } = params;
        this.loadMovieDetail(movieId);
    },
    methods: {
        loadMovieDetail(id) {
            getMovieDetail(id)
                .then((resp) => {
                    this.movie_info = resp.data;
                    console.log(this.movie_info);
                })
                .catch((err) => {
                });
        },
    },
};
</script>

<style scoped>
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
