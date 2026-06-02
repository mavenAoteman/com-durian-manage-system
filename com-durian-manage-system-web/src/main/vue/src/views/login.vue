<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">手工皂坊管理系统</div>
            <el-form :model="param" :rules="rules" ref="loginForm" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.username" placeholder="用户名或邮箱" @blur="onUsernameBlur">
                        <template #prepend>
                            <el-button :icon="User"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input
                        type="password"
                        placeholder="密码"
                        v-model="param.password"
                        show-password
                        @keyup.enter="submitForm(loginForm)"
                    >
                        <template #prepend>
                            <el-button :icon="Lock"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item v-if="needCaptcha">
                    <slide-captcha v-model="captchaPassed" ref="captchaRef" />
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" :loading="submitting" @click="submitForm(loginForm)">登录</el-button>
                </div>
                <div class="login-foot">
                    <el-checkbox v-model="checked" label="记住用户名" size="small" />
                    <el-link type="primary" :underline="false" @click="router.push('/register')">免费注册</el-link>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue';
import { useTagsStore } from '../store/tags';
import { usePermissStore } from '../store/permiss';
import { useUserStore } from '../store/user';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { Lock, User } from '@element-plus/icons-vue';
import { authLogin, authPrecheck } from '../api/index';
import SlideCaptcha from '../components/slide-captcha.vue';

interface LoginInfo {
    username: string;
    password: string;
}

const lgStr = localStorage.getItem('login-username');
const checked = ref(!!lgStr);

const router = useRouter();
const param = reactive<LoginInfo>({
    username: lgStr || '',
    password: '',
});

const rules: FormRules = {
    username: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

const permiss = usePermissStore();
const userStore = useUserStore();
const loginForm = ref<FormInstance>();
const captchaRef = ref<{ reset: () => void } | null>(null);

const needCaptcha = ref(false);
const captchaPassed = ref(false);
const submitting = ref(false);

const onUsernameBlur = async () => {
    if (!param.username) return;
    try {
        const resp = await authPrecheck(param.username);
        const next = !!resp.data?.needCaptcha;
        if (next !== needCaptcha.value) {
            needCaptcha.value = next;
            captchaPassed.value = false;
            await nextTick();
            captchaRef.value?.reset();
        }
    } catch (e) {
        // 预检失败不影响登录流程
    }
};

const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    formEl.validate(async (valid: boolean) => {
        if (!valid) return;
        if (needCaptcha.value && !captchaPassed.value) {
            ElMessage.warning('请完成滑块验证');
            return;
        }
        submitting.value = true;
        try {
            const resp = await authLogin({
                username: param.username,
                password: param.password,
                captchaPassed: captchaPassed.value,
            });
            const data = resp.data;
            userStore.setLogin({
                token: data.token,
                userId: data.userId,
                username: data.username,
                role: data.role,
                roleName: data.roleName,
            });
            permiss.handleSet(data.permissKeys || []);
            // 兼容老代码：sidebar 等地方还在读 ms_username
            localStorage.setItem('ms_username', data.username);

            if (checked.value) {
                localStorage.setItem('login-username', param.username);
            } else {
                localStorage.removeItem('login-username');
            }
            ElMessage.success('登录成功');
            router.push('/');
        } catch (err: any) {
            ElMessage.error(err?.displayMessage || '登录失败');
            // 失败后下一次提交前需要重新预检
            if (needCaptcha.value) {
                captchaPassed.value = false;
                await nextTick();
                captchaRef.value?.reset();
            } else {
                // 失败后立即问一次是否要弹滑块
                onUsernameBlur();
            }
        } finally {
            submitting.value = false;
        }
    });
};

const tags = useTagsStore();
tags.clearTags();
</script>

<style scoped>
.login-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background-image: url(../assets/img/login-bg.jpg);
    background-size: cover;
}
.ms-title {
    line-height: 50px;
    text-align: center;
    font-size: 20px;
    color: #333;
    font-weight: bold;
    padding-top: 10px;
}
.ms-login {
    width: 350px;
    border-radius: 5px;
    background: #fff;
}
.ms-content {
    padding: 10px 30px 30px;
}
.login-btn {
    text-align: center;
}
.login-btn button {
    width: 100%;
    height: 36px;
    margin-bottom: 10px;
}
.login-foot {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
}
</style>
