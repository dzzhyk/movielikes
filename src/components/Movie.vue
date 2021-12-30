<template>
  <div class="container">
    <div class="leftText">
       <p>
         {{this.movieName}}
       </p>
      <p>
        {{this.movieCategory}}
      </p>
      <p>
        {{this.movieScore}}
      </p>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "Movie",
  props: {
    movieName:String,
    movieCategory: String,
    movieScore: String
  }
}
</script>

<style scoped>
.container {
  float: left;
  text-align: left;
  justify-content: space-between;
  box-shadow: 0 0 3px 1px #ececec;
  margin: 5px;
  border-radius: 12px;
  padding: 4px 6px 4px 6px;
  elevation: 1deg;
  width: 150px;
  height: 260px;
}

.leftText {
  width: 100%;
  flex-direction: column;
}

</style>
