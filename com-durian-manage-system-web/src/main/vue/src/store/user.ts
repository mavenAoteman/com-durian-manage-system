import { defineStore } from 'pinia';

export interface UserState {
    token: string;
    userId: number | null;
    username: string;
    workshopName: string;
    bio: string;
    role: number | null; // 1=super_admin, 2=user
    roleName: string;
}

const TOKEN_KEY = 'ms_token';
const USER_KEY = 'ms_user';

function readUser(): UserState {
    const token = localStorage.getItem(TOKEN_KEY) || '';
    const raw = localStorage.getItem(USER_KEY);
    if (token && raw) {
        try {
            const parsed = JSON.parse(raw);
            return {
                token,
                userId: parsed.userId ?? null,
                username: parsed.username ?? '',
                workshopName: parsed.workshopName ?? '',
                bio: parsed.bio ?? '',
                role: parsed.role ?? null,
                roleName: parsed.roleName ?? '',
            };
        } catch (e) {
            // ignore
        }
    }
    return { token: '', userId: null, username: '', workshopName: '', bio: '', role: null, roleName: '' };
}

export const useUserStore = defineStore('user', {
    state: (): UserState => readUser(),
    getters: {
        isLoggedIn: (state) => !!state.token,
        isSuperAdmin: (state) => state.role === 1,
        /** 显示用的皂坊名：优先用 workshopName，没有则回退到 用户名+的皂坊 */
        displayWorkshopName: (state) => state.workshopName || (state.username ? `${state.username}的皂坊` : '手工皂坊管理系统'),
    },
    actions: {
        setLogin(data: { token: string; userId: number; username: string; workshopName?: string; bio?: string; role: number; roleName: string }) {
            this.token = data.token;
            this.userId = data.userId;
            this.username = data.username;
            this.workshopName = data.workshopName || '';
            this.bio = data.bio || '';
            this.role = data.role;
            this.roleName = data.roleName;
            this.persist();
        },
        updateProfile(workshopName: string, bio: string) {
            this.workshopName = workshopName || '';
            this.bio = bio || '';
            this.persist();
        },
        persist() {
            if (this.token) {
                localStorage.setItem(TOKEN_KEY, this.token);
            }
            localStorage.setItem(
                USER_KEY,
                JSON.stringify({
                    userId: this.userId,
                    username: this.username,
                    workshopName: this.workshopName,
                    bio: this.bio,
                    role: this.role,
                    roleName: this.roleName,
                })
            );
        },
        logout() {
            this.token = '';
            this.userId = null;
            this.username = '';
            this.workshopName = '';
            this.bio = '';
            this.role = null;
            this.roleName = '';
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
            localStorage.removeItem('ms_keys');
            localStorage.removeItem('ms_username');
        },
    },
});
