<template>
    <div>
        <el-row style="align-items: center">
            <el-col :xs="{ span: 0, offset: 0 }" :sm="{ span: 6, offset: 0 }" :md="{ span: 4, offset: 0 }">
                <div class="nav-logo">
                    <span>Movielikes</span>
                </div>
            </el-col>
            <el-col :xs="{ span: 12, offset: 0 }" :sm="{ span: 10, offset: 0 }" :md="{ span: 14, offset: 0 }">
                <div style="color: white; display: flex; gap: 20px">
                    <span class="nav-link" @click="router.push('/index')">主页</span>
                    <span v-if="userLogined" class="nav-link" @click="router.push('/rating')">继续评分</span>
                    <span v-if="userLogined" class="nav-link" @click="router.push('/collection')">个人收藏</span>
                </div>
            </el-col>
            <el-col :xs="{ span: 12, offset: 0 }" :sm="{ span: 8, offset: 0 }" :md="{ span: 6, offset: 0 }">
                <div
                    style="
                        display: flex;
                        justify-content: flex-end;
                        align-items: center;
                        gap: 20px;
                        padding-right: 20px;
                    "
                >
                    <div>
                        <el-autocomplete
                            :popper-append-to-body="false"
                            :trigger-on-focus="false"
                            placeholder="搜索电影名称"
                            @select=""
                        />
                    </div>
                    <div v-if="userLogined">
                        <span style="color: white">{{ userName }}</span>
                    </div>
                    <div>
                        <el-dropdown>
                            <el-icon :size="30" class="user-icon"><avatar /></el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item v-if="!userLogined" @click="router.push('/login')"
                                        >前往登录</el-dropdown-item
                                    >
                                    <el-dropdown-item v-if="userLogined" @click="router.push('/profile')"
                                        >个人信息</el-dropdown-item
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
const userLogined = cache.session.get("logined") === "true" || store.getters.logined === true;

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
.nav-logo {
    color: white;
    font-size: 30px;
    padding-left: 20px;
    font-family: "Gill Sans", "Gill Sans MT", Calibri, "Trebuchet MS", sans-serif;
    user-select: none;
}

.user-icon {
    color: white;
    cursor: pointer;
    border-radius: 20px;
    line-height: 40px;
    padding: 2px;
}

.user-icon:hover {
    box-shadow: white 0px 0px 15px 0px;
}

.nav-link {
    cursor: pointer;
    user-select: none;
}

.nav-link:focus,
.nav-link:hover {
    color: #1c83b4;
    text-decoration: underline;
}
</style>
