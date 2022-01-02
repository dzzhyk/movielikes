<template>
    <div class="card">
        <div>
            <a :href="'/detail/' + this.movieId" target="_self">
                <div>
                    <el-image
                        @load="noloading()"
                        lazy
                        :src="this.posterPath === '' ? '' : 'https://image.tmdb.org/t/p/w400/' + this.posterPath"
                        class="poster"
                        alt="poster"
                    >
                        <template #error>
                            <div class="image-slot">
                                <el-icon><icon-picture /></el-icon>
                            </div>
                        </template>
                    </el-image>
                </div>
            </a>
            <div style="margin: 3px">
                <div class="poster-title">
                    <a :href="'/detail/' + this.movieId" target="_self">
                        <span>{{ this.title }}</span>
                    </a>
                </div>
                <div style="height: 20px; padding-bottom: 3px">
                    <div style="float: left">
                        <p style="text-align: left; font-size: 1em; margin: 0; padding: 0; color: rgba(0, 0, 0, 0.6)">
                            {{ this.release }}
                        </p>
                    </div>
                    <div style="float: right; padding-right: 10px; font-size: 16px">{{ this.avgRating }}</div>
                </div>
            </div>
            <!-- <el-skeleton style="width: 240px; height: 360px" :loading="loading" animated>
            <template #template>
                <el-skeleton-item variant="image" style="width: 240px; height: 300px" />
                <div style="padding: 5px">
                    <el-skeleton-item variant="text" style="width: 80%" />
                    <div
                        style="
                            display: flex;
                            align-items: center;
                            justify-items: space-between;
                            margin-top: 10px;
                            height: 16px;
                        "
                    >
                        <el-skeleton-item variant="text" style="margin-right: 16px" />
                        <el-skeleton-item variant="text" style="width: 40%" />
                    </div>
                </div>
            </template>
            <template #default>

                </div>
            </template>
        </el-skeleton> -->
        </div>
    </div>
</template>

<script>
import { Picture as IconPicture } from "@element-plus/icons-vue";

export default {
    name: "Movie",
    props: {
        movieId: Number,
        title: String,
        release: String,
        posterPath: String,
        avgRating: String,
    },
    data() {
        return {
            loading: true,
        };
    },
    components: {
        IconPicture,
    },
    methods: {
        noloading() {
            this.loading = false;
        },
    },
};
</script>

<style scoped>
.image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 360px;
    background: #f5f7fa;
}
.card {
    width: 240px;
    box-shadow: 0 1px 3px rgb(0 0 0 / 50%);
    background-color: white;
}

.card:hover {
    box-shadow: 0 2px 18px rgb(0 0 0 / 50%);
}

.poster {
    display: flex;
    flex-direction: column;
    margin: 0;
    width: 100%;
    height: 100%;
    user-select: none;
    -webkit-user-drag: none;
}

.poster-title {
    text-align: left;
    font-size: 1em;
    margin-top: 0;
    margin-bottom: 0;
}

.poster-title a {
    color: black;
    text-decoration: none;
}

a:focus,
a:hover {
    color: #1c83b4;
    text-decoration: underline;
}
a:active,
a:hover {
    outline: 0;
}
</style>
