<template>
    <el-row>
        <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 20, offset: 2 }" :md="{ span: 18, offset: 3 }">
            <div v-if="userLogined">
                <div class="movie-list-title">个人收藏</div>
                <div class="movie-list-title-addon">不忘旧时光</div>
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
                    <div v-if="records.length === 0">
                        还没有收藏哦~ <a href="/rating">点击</a>前往浏览所有电影，或者搜索🔍 电影添加收藏。
                    </div>
                </div>
            </div>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="24">
            <div style="margin-top: 30px"></div>
        </el-col>
    </el-row>
</template>

<script>
import Movie from "@/components/Movie";
import cache from "@/plugins/cache";
import { getUserCollection } from "@/api/user";

export default {
    data() {
        return {
            userName: "",
            userLogined: false,
            records: [],
        };
    },
    components: {
        Movie,
    },
    mounted() {
        this.userName = cache.session.get("name") || "";
        this.userLogined = cache.session.get("logined") === "true" || this.$store.getters.logined === true;
        this.loadUserCollection()
    },
    methods: {
        loadUserCollection() {
            getUserCollection().then((resp) => {
                this.records = resp.data;
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
