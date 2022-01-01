<template>
    <div style="padding-bottom: 20px; text-align: center;">
        <span v-if="userLogined === 'false'"><a href="javascript:void();" @click="login" style="color: powderblue">现在加入</a> Movielikes,发现更多影视可能。</span>
        <span v-else>你好, {{ userName }}! 在Movielikes, 发现更多影视可能。</span>
        <br>
        <span>Copyright © 2021 Movielikes All Rights Reserved.</span>
    </div>
</template>

<script setup>
import cache from "@/plugins/cache";
const userName = cache.session.get("name");
const userLogined = cache.session.get("logined") || store.getters.logined;
const store = useStore();
</script>

<style scoped>

</style>