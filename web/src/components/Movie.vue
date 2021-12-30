<template>
    <div>
        <img src="https://image.tmdb.org/t/p/w400/8gU0UxhiSfC8Y2QBkXxscG3Hd6s.jpg" class="poster" alt="poster" />
        <div style="margin: 3px;">
            <div>
                <h2 style="text-align: left; font-size: 1em; margin-top: 0; margin-bottom: 0">
                    {{ this.movieName }}
                </h2>
            </div>
            <div style="height: 20px; padding-bottom: 3px;">
                <div style="float: left">
                    <p style="text-align: left; font-size: 1em; margin: 0; padding: 0; color: rgba(0, 0, 0, 0.6)">
                        {{ this.movieReleaseDate }}
                    </p>
                </div>
                <div style="float: right; padding-right: 10px; font-size: 14px">Score: {{ this.movieScore }}</div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "Movie",
    props: {
        movieName: String,
        movieReleaseDate: String,
        movieScore: String,
    },
};
</script>

<style scoped>
.poster {
    display: flex;
    flex-direction: column;
    margin: 0;
    border-radius: 12px;
    width: 300px;
    height: 400px;
}
</style>
