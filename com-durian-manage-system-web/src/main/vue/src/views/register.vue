<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">注册一个账号</div>
            <el-form :model="param" :rules="rules" ref="regForm" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.username" placeholder="用户名（3-20位字母数字下划线）或邮箱">
                        <template #prepend>
                            <el-button :icon="User"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input
                        type="password"
                        placeholder="密码（8位以上，必须含字母+数字）"
                        v-model="param.password"
                        show-password
                    >
                        <template #prepend>
                            <el-button :icon="Lock"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="confirmPassword">
                    <el-input
                        type="password"
                        placeholder="再次输入密码"
                        v-model="param.confirmPassword"
                        show-password
                    >
                        <template #prepend>
                            <el-button :icon="Lock"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="email">
                    <el-input v-model="param.email" placeholder="邮箱（选填）">
                        <template #prepend>
                            <el-button :icon="Message"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <slide-captcha v-model="captchaPassed" ref="captchaRef" />
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" :loading="submitting" @click="submitForm(regForm)">注册</el-button>
                </div>
                <div class="login-foot">
                    <span>已有账号？</span>
                    <el-link type="primary" :underline="false" @click="router.push('/login')">立即登录</el-link>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { Lock, User, Message } from '@element-plus/icons-vue';
import { authRegister } from '../api/index';
import SlideCaptcha from '../components/slide-captcha.vue';

const router = useRouter();
const captchaRef = ref<{ reset: () => void } | null>(null);
const captchaPassed = ref(false);
const submitting = ref(false);

const param = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
});

const USERNAME_RE = /^([a-zA-Z0-9_]{3,20})$|^([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,})$/;
const WEAK_PWDS = new Set([
    '12345678', '123456789', '1234567890',
    'password', 'password1', 'passw0rd',
    'qwerty123', 'asdf1234', 'abc12345', 'abcd1234',
    '11111111', '00000000', '88888888', '66666666',
    'iloveyou', 'welcome1', 'admin123', 'admin1234',
    '1qaz2wsx', '1q2w3e4r',
]);

const validateUsername = (_rule: any, value: string, callback: (error?: Error) => void) => {
    if (!value) return callback(new Error('请输入用户名'));
    if (!USERNAME_RE.test(value)) {
        return callback(new Error('用户名需 3-20 位字母/数字/下划线，或合法邮箱'));
    }
    callback();
};

const validatePassword = (_rule: any, value: string, callback: (error?: Error) => void) => {
    if (!value) return callback(new Error('请输入密码'));
    if (value.length < 8 || value.length > 32) {
        return callback(new Error('密码长度需 8-32 位'));
    }
    if (!/[A-Za-z]/.test(value) || !/\d/.test(value)) {
        return callback(new Error('密码必须同时包含字母和数字'));
    }
    if (WEAK_PWDS.has(value.toLowerCase())) {
        return callback(new Error('密码过于简单，请换一个'));
    }
    if (param.username && value.toLowerCase() === param.username.toLowerCase()) {
        return callback(new Error('密码不能与用户名相同'));
    }
    callback();
};

const validatePass2 = (_rule: any, value: string, callback: (error?: Error) => void) => {
    if (!value) {
        callback(new Error('请再次输入密码'));
    } else if (value !== param.password) {
        callback(new Error('两次密码不一致'));
    } else {
        callback();
    }
};

const rules: FormRules = {
    username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
    password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
    confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }],
    email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
};

const regForm = ref<FormInstance>();

const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    formEl.validate(async (valid: boolean) => {
        if (!valid) return;
        if (!captchaPassed.value) {
            ElMessage.warning('请完成滑块验证');
            return;
        }
        submitting.value = true;
        try {
            await authRegister({
                username: param.username,
                password: param.password,
                email: param.email || undefined,
                captchaPassed: true,
            });
            ElMessage.success('注册成功，请登录');
            router.push('/login');
        } catch (err: any) {
            ElMessage.error(err?.displayMessage || '注册失败');
            captchaPassed.value = false;
            await nextTick();
            captchaRef.value?.reset();
        } finally {
            submitting.value = false;
        }
    });
};
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
    color: #666;
}
</style>
