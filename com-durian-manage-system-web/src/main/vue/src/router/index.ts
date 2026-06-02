import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';
import { usePermissStore } from '../store/permiss';
import { useUserStore } from '../store/user';
import Home from '../views/home.vue';
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

const PUBLIC_PATHS = ['/login', '/register', '/403'];

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        redirect: '/dashboard',
    },
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [
            {
                path: '/dashboard',
                name: 'dashboard',
                meta: {
                    title: '系统首页',
                    permiss: '1',
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/dashboard.vue'),
            },
            {
                path: '/table',
                name: 'basetable',
                meta: {
                    title: '表格',
                    permiss: '2',
                },
                component: () => import(/* webpackChunkName: "table" */ '../views/table.vue'),
            },
            {
                path: '/charts',
                name: 'basecharts',
                meta: {
                    title: '图表',
                    permiss: '11',
                },
                component: () => import(/* webpackChunkName: "charts" */ '../views/charts.vue'),
            },
            {
                path: '/form',
                name: 'baseform',
                meta: {
                    title: '表单',
                    permiss: '5',
                },
                component: () => import(/* webpackChunkName: "form" */ '../views/form.vue'),
            },
            {
                path: '/tabs',
                name: 'tabs',
                meta: {
                    title: 'tab标签',
                    permiss: '3',
                },
                component: () => import(/* webpackChunkName: "tabs" */ '../views/tabs.vue'),
            },
            {
                path: '/soap',
                name: 'soap',
                meta: {
                    title: '手工皂生产',
                    permiss: '3',
                },
                component: () => import(/* webpackChunkName: "tabs" */ '../views/soap-production.vue'),
            },
            {
                path: '/packaging',
                name: 'packaging',
                meta: { title: '打包发货', permiss: '3' },
                component: () => import(/* webpackChunkName: "packaging" */ '../views/packaging.vue'),
            },
            {
                path: '/consumables',
                name: 'consumables',
                meta: {
                    title: '物料管理',
                    permiss: '3',
                },
                component: () => import(/* webpackChunkName: "tabs" */ '../views/consumables.vue'),
            },
            {
                path: '/oils',
                name: 'oils',
                meta: {
                    title: '配方灵感',
                    permiss: '3',
                },
                component: () => import(/* webpackChunkName: "tabs" */ '../views/oils.vue'),
            },
            {
                path: '/consumables-usage',
                name: 'consumables-usage',
                meta: {
                    title: '消耗记录',
                    permiss: '3',
                },
                component: () => import(/* webpackChunkName: "consumables-usage" */ '../views/consumables-usage.vue'),
            },
            {
                path: '/tools',
                name: 'tools',
                meta: {
                    title: '工具管理',
                    permiss: '3',
                },
                component: () => import(/* webpackChunkName: "tabs" */ '../views/tools.vue'),
            },
            {
                path: '/donate',
                name: 'donate',
                meta: {
                    title: '鼓励作者',
                    permiss: '14',
                },
                component: () => import(/* webpackChunkName: "donate" */ '../views/donate.vue'),
            },
            {
                path: '/permission',
                name: 'permission',
                meta: {
                    title: '权限管理',
                    permiss: 'user_manage',
                },
                component: () => import(/* webpackChunkName: "permission" */ '../views/permission.vue'),
            },
            {
                path: '/upload',
                name: 'upload',
                meta: {
                    title: '上传插件',
                    permiss: '6',
                },
                component: () => import(/* webpackChunkName: "upload" */ '../views/upload.vue'),
            },
            {
                path: '/icon',
                name: 'icon',
                meta: {
                    title: '自定义图标',
                    permiss: '10',
                },
                component: () => import(/* webpackChunkName: "icon" */ '../views/icon.vue'),
            },
            {
                path: '/user',
                name: 'user',
                meta: {
                    title: '个人中心',
                },
                component: () => import(/* webpackChunkName: "user" */ '../views/user.vue'),
            },
            {
                path: '/editor',
                name: 'editor',
                meta: {
                    title: '富文本编辑器',
                    permiss: '8',
                },
                component: () => import(/* webpackChunkName: "editor" */ '../views/editor.vue'),
            },
            {
                path: '/markdown',
                name: 'markdown',
                meta: {
                    title: 'markdown编辑器',
                    permiss: '9',
                },
                component: () => import(/* webpackChunkName: "markdown" */ '../views/markdown.vue'),
            },
            {
                path: '/export',
                name: 'export',
                meta: {
                    title: '导出Excel',
                    permiss: '2',
                },
                component: () => import(/* webpackChunkName: "export" */ '../views/export.vue'),
            },
            {
                path: '/import',
                name: 'import',
                meta: {
                    title: '导入Excel',
                    permiss: '2',
                },
                component: () => import(/* webpackChunkName: "import" */ '../views/import.vue'),
            },
        ],
    },
    {
        path: '/login',
        name: 'Login',
        meta: {
            title: '登录',
        },
        component: () => import(/* webpackChunkName: "login" */ '../views/login.vue'),
    },
    {
        path: '/register',
        name: 'Register',
        meta: {
            title: '注册',
        },
        component: () => import(/* webpackChunkName: "register" */ '../views/register.vue'),
    },
    {
        path: '/403',
        name: '403',
        meta: {
            title: '没有权限',
        },
        component: () => import(/* webpackChunkName: "403" */ '../views/403.vue'),
    },
];

const router = createRouter({
    history: createWebHashHistory(),
    routes,
});

router.beforeEach((to, _from, next) => {
    NProgress.start();
    const userStore = useUserStore();
    const permiss = usePermissStore();
    const isPublic = PUBLIC_PATHS.includes(to.path);

    if (!userStore.isLoggedIn) {
        if (isPublic) {
            next();
        } else {
            next('/login');
        }
        return;
    }

    if (to.meta.permiss && !permiss.key.includes(to.meta.permiss as string)) {
        next('/403');
        return;
    }
    next();
});

router.afterEach(() => {
    NProgress.done()
})

export default router;
