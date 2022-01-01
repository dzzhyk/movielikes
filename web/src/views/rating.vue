<template>
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
                    ><a href="javascript:void();" @click="router.push('/login');" style="color: powderblue">现在加入</a>
                    Movielikes, 发现更多影视可能。</span
                >
                <span v-else>你好, {{ userName }}! 在Movielikes, 发现更多影视可能。</span>
                <br />
                <span>Copyright © 2021 Movielikes All Rights Reserved.</span>
            </div>
        </el-footer>
</template>

<script setup>
import Movie from "@/components/Movie";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import cache from "@/plugins/cache";

const store = useStore();
const router = useRouter();
const userName = cache.session.get("name");
const userLogined = cache.session.get("logined") === "true" || store.getters.logined === true;
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