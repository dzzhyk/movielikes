<template>
  <div class="container">
    <div class="leftText">
       <div>
         电影名称
       </div>
      <div>
        电影分类
      </div>
      <div>
        电影评分
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Movie"
}
</script>

<style scoped>
.container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 0 3px 1px #ececec;
  margin: 16px;
  border-radius: 12px;
  padding: 8px 20px 8px 12px;
  elevation: 1deg;
  width: 150px;
  height: 300px;
}

.leftText {
  width: 80%;
  display: flex;
  flex-direction: column;
}

</style>
