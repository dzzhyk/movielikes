<template>
    <el-container>
        <el-header>
            <NavBar></NavBar>
        </el-header>
        <el-main>
            <router-view />
        <el-footer>
            <FootBar></FootBar>
        </el-footer>
        </el-main>
    </el-container>
</template>

<script setup>
import { NavBar, FootBar } from "@/layout/components";
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
