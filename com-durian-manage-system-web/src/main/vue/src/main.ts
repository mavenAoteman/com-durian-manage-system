import { createApp } from 'vue';
import { createPinia } from 'pinia';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import App from './App.vue';
import router from './router';
import { usePermissStore } from './store/permiss';
import 'element-plus/dist/index.css';
import './assets/css/icon.css';

// 基础造了一个app
const app = createApp(App);
app.use(createPinia());
app.use(router);

// 注册elementplus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}
// 自定义权限指令：在 mounted 和 updated 两个钩子都重新校验，保证登录后菜单实时刷新
const permiss = usePermissStore();
function applyPermiss(el: HTMLElement, binding: any) {
    const key = String(binding.value);
    if (!permiss.key.includes(key)) {
        el.hidden = true;
    } else {
        el.hidden = false;
    }
}
app.directive('permiss', {
    mounted: applyPermiss,
    updated: applyPermiss,
});
//应用实例必须在调用了 .mount() 方法后才会渲染出来
app.mount('#app');
