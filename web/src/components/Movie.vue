<template>
    <div class="card">
        <div>
            <a href="/index" target="_self">
                <div>
                    <img
                        src="https://image.tmdb.org/t/p/w400/eEslKSwcqmiNS6va24Pbxf2UKmJ.jpg"
                        class="poster"
                        alt="poster"
                    />
                </div>
            </a>
            <div style="margin: 3px">
                <div class="poster-title">
                    <a href="/index" target="_self" class="title">
                        <span>{{ this.movieName }}</span>
                    </a>
                </div>
                <div style="height: 20px; padding-bottom: 3px">
                    <div style="float: left">
                        <p style="text-align: left; font-size: 1em; margin: 0; padding: 0; color: rgba(0, 0, 0, 0.6)">
                            {{ this.movieReleaseDate }}
                        </p>
                    </div>
                    <div style="float: right; padding-right: 10px; font-size: 16px;color: #ff9900">{{ this.movieScore }}</div>
                </div>
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
.card {
    width: 240px;
    box-shadow: 0 1px 3px rgb(0 0 0 / 50%);
}

.card:hover {
    box-shadow: 0 2px 18px rgb(0 0 0 / 50%);
}

.title:hover {
    color: green;
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

.poster-title a{
    color: black;
    text-decoration: none;
}

a:focus, a:hover {
    color: #1c83b4;
    text-decoration: underline;
}
a:active, a:hover {
    outline: 0;
}

</style>
