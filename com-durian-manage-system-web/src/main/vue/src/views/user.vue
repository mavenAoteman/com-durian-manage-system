<template>
  <div>
    <el-row :gutter="20">
      <!-- 左侧：基础信息预览 -->
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <span>基础信息</span>
          </template>
          <div class="info">
            <el-avatar :size="100" :src="avatarImg" />
            <div class="info-workshop">{{ userStore.displayWorkshopName }}</div>
            <div class="info-name">@{{ userStore.username }}</div>
            <el-tag size="small" :type="userStore.isSuperAdmin ? 'danger' : 'info'" class="info-role">
              {{ userStore.roleName }}
            </el-tag>
            <div class="info-bio">{{ userStore.bio || '这家伙很懒，什么也没留下…' }}</div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：编辑表单 -->
      <el-col :span="14">
        <!-- 资料编辑 -->
        <el-card shadow="hover" class="mb20">
          <template #header>
            <span>资料编辑</span>
          </template>
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px">
            <el-form-item label="用户名">
              <el-input :model-value="userStore.username" disabled />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input :model-value="email" disabled placeholder="未填写" />
            </el-form-item>
            <el-form-item label="皂坊名" prop="workshopName">
              <el-input
                v-model="profileForm.workshopName"
                placeholder="留空将恢复为 用户名+的皂坊"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input
                v-model="profileForm.bio"
                type="textarea"
                :rows="3"
                placeholder="说点什么吧"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingProfile" @click="onSaveProfile">保存资料</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 修改密码 -->
        <el-card shadow="hover">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="pwdForm.newPassword"
                type="password"
                show-password
                placeholder="8 位以上，必须含字母+数字"
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" :loading="changingPwd" @click="onChangePassword">修改密码</el-button>
              <span class="tips">改完会自动退出登录</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts" name="user">
import { reactive, ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { useRouter } from 'vue-router';
import avatar from '../assets/img/img.jpg';
import { useUserStore } from '../store/user';
import { usePermissStore } from '../store/permiss';
import { authMe, authUpdateProfile, authChangePassword } from '../api/index';

const userStore = useUserStore();
const permiss = usePermissStore();
const router = useRouter();

const avatarImg = ref(avatar);
const email = ref('');

const profileFormRef = ref<FormInstance>();
const pwdFormRef = ref<FormInstance>();

const profileForm = reactive({
  workshopName: userStore.workshopName,
  bio: userStore.bio,
});

const profileRules: FormRules = {
  workshopName: [{ max: 100, message: '皂坊名最多 100 个字符', trigger: 'blur' }],
  bio: [{ max: 500, message: '个人简介最多 500 个字符', trigger: 'blur' }],
};

const WEAK_PWDS = new Set([
  '12345678', '123456789', 'password', 'password1', 'qwerty123',
  'asdf1234', 'abc12345', '11111111', 'admin123',
]);

const validateNewPassword = (_rule: any, value: string, cb: (err?: Error) => void) => {
  if (!value) return cb(new Error('请输入新密码'));
  if (value.length < 8 || value.length > 32) return cb(new Error('密码长度需 8-32 位'));
  if (!/[A-Za-z]/.test(value) || !/\d/.test(value)) return cb(new Error('密码必须同时包含字母和数字'));
  if (WEAK_PWDS.has(value.toLowerCase())) return cb(new Error('密码过于简单'));
  if (userStore.username && value.toLowerCase() === userStore.username.toLowerCase()) {
    return cb(new Error('密码不能与用户名相同'));
  }
  cb();
};

const validateConfirm = (_rule: any, value: string, cb: (err?: Error) => void) => {
  if (!value) return cb(new Error('请再次输入新密码'));
  if (value !== pwdForm.newPassword) return cb(new Error('两次密码不一致'));
  cb();
};

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, validator: validateNewPassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirm, trigger: 'blur' }],
};

const savingProfile = ref(false);
const changingPwd = ref(false);

onMounted(async () => {
  // 拉一次 /me 同步最新字段
  try {
    const res = await authMe();
    const d = res.data || {};
    email.value = d.email || '';
    profileForm.workshopName = d.workshopName || '';
    profileForm.bio = d.bio || '';
    userStore.updateProfile(d.workshopName || '', d.bio || '');
  } catch (e) {
    // 拉取失败用本地缓存
  }
});

const onSaveProfile = () => {
  profileFormRef.value?.validate(async (valid) => {
    if (!valid) return;
    savingProfile.value = true;
    try {
      const res = await authUpdateProfile({
        workshopName: profileForm.workshopName,
        bio: profileForm.bio,
      });
      const d = res.data || {};
      userStore.updateProfile(d.workshopName || '', d.bio || '');
      profileForm.workshopName = d.workshopName || '';
      profileForm.bio = d.bio || '';
      ElMessage.success('保存成功');
    } catch (err: any) {
      ElMessage.error(err?.displayMessage || '保存失败');
    } finally {
      savingProfile.value = false;
    }
  });
};

const onChangePassword = () => {
  pwdFormRef.value?.validate(async (valid) => {
    if (!valid) return;
    try {
      await ElMessageBox.confirm('修改密码后需要重新登录，确认继续？', '提示', { type: 'warning' });
    } catch (e) {
      return;
    }
    changingPwd.value = true;
    try {
      await authChangePassword({
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword,
      });
      ElMessage.success('密码修改成功，请重新登录');
      userStore.logout();
      permiss.clear();
      router.push('/login');
    } catch (err: any) {
      ElMessage.error(err?.displayMessage || '修改失败');
    } finally {
      changingPwd.value = false;
    }
  });
};
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}
.info {
  text-align: center;
  padding: 30px 10px;
}
.info-workshop {
  margin-top: 16px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
.info-name {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}
.info-role {
  margin-top: 10px;
}
.info-bio {
  margin-top: 18px;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  padding: 0 20px;
  white-space: pre-line;
}
.tips {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style>
