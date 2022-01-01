<template>
    <div>
        <el-row style="align-items: center">
            <el-col :xs="{ span: 16, offset: 0 }" :sm="{ span: 16, offset: 0 }" :md="{ span: 20, offset: 0 }">
                <div style="color: white; font-size: 30px; padding-left: 20px" @click="index">
                    <span style="cursor: pointer">Movielikes</span>
                </div>
            </el-col>
            <el-col :xs="{ span: 10, offset: 0 }" :sm="{ span: 8, offset: 0 }" :md="{ span: 4, offset: 0 }">
                <div style="float: right; margin-right: 20px">
                    <div>
                        <!-- <el-autocomplete
                            :popper-append-to-body="false"
                            trigger-on-focus="false"
                            placeholder="搜索感兴趣的电影名称、imdb编号、tmdb编号"
                            @select=""
                        /> -->
                    </div>
                    <div>
                        <el-dropdown>
                            <el-icon :size="40" class="user-icon"><avatar /></el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item v-if="!userLogined" @click="login">前往登录</el-dropdown-item>
                                    <el-dropdown-item v-if="userLogined" @click="profile"
                                        >在线: {{ userName }}</el-dropdown-item
                                    >
                                    <el-dropdown-item v-if="userLogined" @click="logout">退出登录</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import cache from "@/plugins/cache";
import { ElMessageBox } from "element-plus";
import { Avatar } from "@element-plus/icons-vue";

const store = useStore();
const router = useRouter();

const userName = cache.session.get("name");
const userLogined = cache.session.get("logined") === 'true' || store.getters.logined === true;

function index() {
    router.push("/index");
}

function profile() {
    router.push("/profile");
}

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
.user-icon {
    font-size: 30px;
    color: white;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 20px;
    line-height: 40px;
}

.user-icon:hover {
    box-shadow: white 0px 0px 15px 0px;
}
</style>
