<template>
    <div>
        <img src="https://image.tmdb.org/t/p/w400/8gU0UxhiSfC8Y2QBkXxscG3Hd6s.jpg" class="poster" alt="poster" style="width: 100%" />
        <div class="leftText">
            <p>
                {{ this.movieName }}
            </p>
            <p>
                {{ this.movieReleaseDate }}
            </p>
            <p>
                {{ this.movieScore }}
            </p>
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
    /* box-shadow: 0 0 3px 1px #ececec; */
    margin: 10px;
    border-radius: 12px;
    width: 300px;
    height: 400px;
}
</style>
