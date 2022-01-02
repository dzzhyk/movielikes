<template>
    <el-row>
        <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
            <div v-if="userLogined">
                <div class="movie-list-title">继续为电影评分</div>
                <div class="movie-list-title-addon">帮助 Movielikes 不断改进</div>
                <el-divider :always="true"></el-divider>
                <div class="movie-list">
                    <div v-for="m in records">
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
        <el-col :span="16" :offset="8">
            <div style="margin-top: 30px; margin-bottom: 30px">
                <el-pagination @current-change="handleCurrentChange" background layout="prev, pager, next, jumper" :page-count="this.pages"></el-pagination>
            </div>
        </el-col>
    </el-row>
</template>

<script>
import Movie from "@/components/Movie";
import cache from "@/plugins/cache";
import { getMovieList } from "@/api/movie";

export default {
    data() {
        return {
            userName: "",
            userLogined: false,
            records: [],
            curr: 1,
            size: 20,
            pages: 1
        };
    },
    components: {
        Movie,
    },
    mounted() {
        this.userName = cache.session.get("name") || "";
        this.userLogined = cache.session.get("logined") === "true" || this.$store.getters.logined === true;
        this.loadMovieList(this.curr, this.size);
    },
    methods: {
        loadMovieList(curr, size) {
            getMovieList(curr, size).then((resp) => {
                this.pages = resp.data.pages;
                this.curr = resp.data.curr;
                this.size = resp.data.size;
                this.records = resp.data.records;
            });
        },
        handleCurrentChange(val){
            this.loadMovieList(val, this.size)
        }
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
</style>
