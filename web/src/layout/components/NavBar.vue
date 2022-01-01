<template>
    <div>
        <el-row style="align-items: center">
            <el-col :xs="{ span: 14, offset: 0 }" :sm="{ span: 16, offset: 0 }" :md="{ span: 18, offset: 0 }">
                <div style="color: white; font-size: 24px; padding-left: 12vw">Movielikes</div>
            </el-col>
            <el-col :xs="{ span: 10, offset: 0 }" :sm="{ span: 8, offset: 0 }" :md="{ span: 6, offset: 0 }">
                <div style="padding-right: 10vw">
                    <el-button v-if="userLogined === 'false'" @click="login" style="float: right"> 登录 </el-button>
                    <div v-if="userLogined === 'true'" style="display: flex; flex-direction: row; align-items: center">
                        <div style="color: white; padding-right: 1vw">当前在线: {{ userName }}</div>
                        <el-button @click="logout" style="float: right"> 登出 </el-button>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import cache from "@/plugins/cache";
import { ElMessageBox } from "element-plus";

const store = useStore();
const router = useRouter();
const userName = cache.session.get("name");
const userLogined = cache.session.get("logined") || store.getters.logined;

function login() {
    router.push("/login");
}

function logout() {
    ElMessageBox.confirm("确定退出吗?", "Warning", {
        confirmButtonText: "OK",
        cancelButtonText: "Cancel",
        type: "warning",
    }).then(() => {
        store.dispatch("LogOut").then(() => {
            window.location.href = "/index";
        });
    });
}
</script>

<style scoped>

</style>