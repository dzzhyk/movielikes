<template>
    <el-row>
        <el-col :span="14" :offset="5">
            <el-tabs class="profile-container" type="border-card" tab-position="left">
                <el-tab-pane label="账户设置" style="padding-top: 30px">
                    <el-form
                        ref="profileRef"
                        :rules="updateProfileRules"
                        label-position="right"
                        :model="updateProfileForm"
                        :label-width="100"
                        style="max-width: 460px"
                    >
                        <el-form-item label="用户名" prop="username">
                            <el-input
                                type="text"
                                v-model="updateProfileForm.username"
                                :placeholder="updateProfileForm.username"
                                clearable
                            ></el-input>
                        </el-form-item>
                        <el-form-item label="邮箱" prop="email">
                            <el-input
                                type="email"
                                v-model="updateProfileForm.email"
                                :placeholder="updateProfileForm.email"
                                clearable
                            ></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button :loading="loading" size="medium" type="primary" @click.prevent="updateProfile">
                                <span v-if="!loading">提 交</span>
                                <span v-else>提 交 中...</span>
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="修改密码" style="padding-top: 30px">
                    <el-form
                        ref="pwdRef"
                        :model="updatePwdForm"
                        label-position="right"
                        :rules="updatePwdRules"
                        :label-width="100"
                        style="max-width: 460px"
                    >
                        <el-form-item label="新密码" prop="password1">
                            <el-input type="password" v-model="updatePwdForm.password1" clearable></el-input>
                        </el-form-item>
                        <el-form-item label="确认密码" prop="password2">
                            <el-input type="password" v-model="updatePwdForm.password2" clearable></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button :loading="loading" size="medium" type="primary" @click.prevent="updatePwd">
                                <span v-if="!loading">提 交</span>
                                <span v-else>提 交 中...</span>
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>
        </el-col>
    </el-row>
</template>

<script setup>
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import cache from "@/plugins/cache";
import { validEmail } from "@/utils/validate";
import { updateUserProfile, updateUserPwd } from "@/api/user";
import { ElMessage } from "element-plus";
import { getCurrentInstance } from "vue";

const loading = ref(false);
const store = useStore();
const {proxy} = getCurrentInstance();

const updateProfileForm = ref({
    username: cache.session.get("name"),
    email: cache.session.get("email"),
});

const testEmail = (rule, value, callback) => {
    if (!validEmail(value)) {
        callback(new Error("邮箱格式错误"));
    } else {
        callback();
    }
};

const updateProfileRules = {
    username: [
        { required: true, trigger: "blur", message: "请输入用户名", trigger: "blur" },
        { required: true, min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" },
    ],
    email: [
        { required: true, trigger: "blur", message: "请输入邮箱", trigger: "blur" },
        { required: true, validator: testEmail, trigger: "blur" },
    ],
};

function updateProfile() {
    let data = updateProfileForm.value;
    proxy.$refs.profileRef.validate((valid) => {
        loading.value = true;
        if (!valid) {
            ElMessage({
                message: "数据不合法",
                type: "error",
            });
            loading.value = false;
        } else {
            updateUserProfile(data).then((response) => {
                if (response.code === 200) {
                    // 更新浏览器用户信息
                    store.commit("SET_NAME", data.username);
                    store.commit("SET_EMAIL", data.email);
                    ElMessage({
                        message: response.msg,
                        type: "success",
                    });
                } else {
                    ElMessage({
                        message: response.msg,
                        type: "error",
                    });
                }
                loading.value = false;
            });
        }
    });
}
const updatePwdForm = ref({
    password1: "",
    password2: "",
});

const equalToPassword = (rule, value, callback) => {
    if (updatePwdForm.value.password1 !== value) {
        callback(new Error("两次输入的密码不一致"));
    } else {
        callback();
    }
};

const updatePwdRules = {
    password1: [
        { required: true, trigger: "blur", message: "请输入新密码" },
        { required: true, min: 6, max: 20, message: "用户密码长度必须介于 6 和 20 之间", trigger: "blur" },
    ],
    password2: [
        { required: true, trigger: "blur", message: "请再次输入新密码" },
        { required: true, validator: equalToPassword, trigger: "blur" },
    ],
};

function updatePwd() {
    console.log(proxy.$refs.pwdRef)
    proxy.$refs.pwdRef.validate((valid) => {
        loading.value = true;
        if (!valid) {
            ElMessage({
                message: "数据不合法",
                type: "error",
            });
            loading.value = false;
        } else {
            updateUserPwd(updatePwdForm.value.password1).then((response) => {
                if (response.code === 200) {
                    ElMessage({
                        message: response.msg,
                        type: "success",
                    });
                } else {
                    ElMessage({
                        message: response.msg,
                        type: "error",
                    });
                }
                loading.value = false;
            });
        }
    });
}
</script>

<style scoped>
.profile-container {
    margin-top: 30px;
    height: 500px;
}
</style>
