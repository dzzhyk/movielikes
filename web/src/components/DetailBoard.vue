<template>
    <div class="detail-card">
        <div style="display: flex; justify-content: center">
            <el-image
                :src="this.posterPath === '' ? '' : 'http://image.tmdb.org/t/p/w400/' + this.posterPath"
                class="detail-poster"
                alt="poster"
            >
                <template #error>
                    <div class="image-slot">
                        <el-icon><icon-picture /></el-icon>
                    </div>
                </template>
            </el-image>
        </div>
        <div style="height: 100%">
            <el-divider direction="vertical"></el-divider>
        </div>
        <div class="detail-content">
            <div class="detail-content-title">
                {{ this.title }}
            </div>
            <div>
                <p>
                    <el-rate
                        v-model="this.avgRating"
                        disabled
                        show-score
                        text-color="#ff9900"
                        score-template="avg. {value}"
                    ></el-rate>
                </p>
            </div>
            <div style="padding-top: 10px; padding-bottom: 10px">
                <span>{{ this.genres.replaceAll("&&", " | ") }}</span>
            </div>
            <div>
                <span>{{ this.release }}</span>
                <span style="padding-left: 20px">{{ this.runtime }}min</span>
            </div>
            <div>
                <p>{{ this.overview }}</p>
            </div>
            <div v-if="userLogined">
                <el-button @click="handleCollect" :loading="this.loading" v-if="!this.alreadyCollect" type="primary"
                    >收藏</el-button
                >
                <el-button
                    @click="handleCancelCollect"
                    :loading="this.loading"
                    v-if="this.alreadyCollect"
                    type="warning"
                    >取消收藏</el-button
                >
                <div style="margin-top: 20px">
                    <div style="display: inline-block">我的打分:</div>
                    <el-rate
                        style="margin-top: 10px; display: inline-block; margin-left: 10px"
                        v-model="this.userRating"
                        :text-color="'#F7BA2A'"
                        @change="handleRating"
                        show-score
                        allow-half
                    ></el-rate>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { Picture as IconPicture } from "@element-plus/icons-vue";
import { checkIfUserCollection, addUserCollection, deleteUserCollection } from "@/api/user";
import { updateUserRating, checkUserRating } from "../api/movie";
import { ElMessage } from "element-plus";

export default {
    name: "DetailBoard",
    props: {
        userLogined: Boolean,
        title: String,
        genres: String,
        imdbId: String,
        tmdbId: String,
        release: String,
        runtime: Number,
        overview: String,
        posterPath: String,
        avgRating: String,
    },
    data() {
        return {
            movieId: -1,
            alreadyCollect: false,
            loading: false,
            userRating: 0,
        };
    },
    mounted() {
        let { params, query } = this.$route;
        let { movieId } = params;
        this.movieId = movieId;
        this.loadCollection();
        this.loadUserRating();
    },
    components: { IconPicture },
    methods: {
        loadCollection() {
            checkIfUserCollection(this.movieId).then((resp) => {
                this.alreadyCollect = resp.data !== null;
            });
        },
        loadUserRating() {
            checkUserRating(this.movieId).then((resp) => {
                this.userRating = resp.data;
            });
        },
        handleCollect() {
            this.loading = true;
            addUserCollection(this.movieId)
                .then((resp) => {
                    this.loading = false;
                    this.loadCollection();
                    ElMessage({
                        message: "收藏成功",
                        type: "success",
                        duration: 600,
                    });
                })
                .catch((err) => {
                    this.loading = false;
                    ElMessage({
                        message: "收藏失败",
                        type: "error",
                        duration: 600,
                    });
                });
            this.loadCollection();
        },
        handleCancelCollect() {
            this.loading = true;
            deleteUserCollection(this.movieId)
                .then((resp) => {
                    this.loading = false;
                    this.loadCollection();
                    ElMessage({
                        message: "取消收藏成功",
                        type: "success",
                        duration: 600,
                    });
                })
                .catch((err) => {
                    this.loading = false;
                    ElMessage({
                        message: "取消收藏失败",
                        type: "error",
                        duration: 600,
                    });
                });
        },
        handleRating() {
            updateUserRating(this.movieId, this.userRating)
                .then((resp) => {
                    if (resp.code === 200) {
                        ElMessage({
                            message: "打分成功!",
                            type: "success",
                        });
                    } else {
                        ElMessage({
                            message: "打分失败!",
                            type: "error",
                        });
                    }
                })
                .catch((error) => {
                    ElMessage({
                        message: error,
                        type: "error",
                    });
                });
        },
    },
};
</script>

<style scoped>
.image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 260px;
    height: 360px;
    background: #f5f7fa;
}
.detail-card {
    display: flex;
    flex-direction: row;
    align-items: center;
    height: 500px;
    width: 90%;
    box-shadow: 0 1px 3px rgb(0 0 0 / 50%);
    background-color: #111f36;
    border-radius: 7px;
}

.detail-poster {
    display: flex;
    flex-direction: column;
    margin: 0;
    width: 95%;
    user-select: none;
    -webkit-user-drag: none;
    user-select: none;
    border-radius: 7px;
    padding: 15px;
}

.detail-content {
    display: flex;
    flex-direction: column;
    width: 100%;
    color: white;
    justify-content: center;
    margin-left: 10px;
    margin-right: 40px;
}

.el-divider--vertical {
    height: 100%;
}

.detail-content-title {
    font-size: xx-large;
    font-family: "Source Sans Pro", Arial, sans-serif;
}
</style>
