<template>
    <el-container>
        <el-header>
            <NavBar></NavBar>
        </el-header>
        <el-main>
            <router-view />
        </el-main>
    </el-container>
</template>

<script setup>
import { NavBar } from "@/layout/components";
</script>

<style scoped>
.el-container {
    padding: 0;
    margin: 0;
    height: 100%;
}

.el-header {
    background-color: #0d243f;
    padding: 10px;
    text-align: left;
    align-items: center;
}

.el-main {
    background-image: url("@/assets/images/bg.png");
    background-size: cover;
    background-repeat: no-repeat;
    background-attachment: fixed;
    background-position: left center;
    height: inherit;
    padding: 0;
    padding-top: 2px;
}

</style>
