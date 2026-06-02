import { defineStore } from 'pinia';

const KEY_STORAGE = 'ms_keys';

function readKeys(): string[] {
    const raw = localStorage.getItem(KEY_STORAGE);
    if (!raw) return [];
    try {
        const parsed = JSON.parse(raw);
        return Array.isArray(parsed) ? parsed : [];
    } catch (e) {
        return [];
    }
}

export const usePermissStore = defineStore('permiss', {
    state: () => ({
        key: readKeys() as string[],
    }),
    actions: {
        handleSet(val: string[]) {
            this.key = val;
            localStorage.setItem(KEY_STORAGE, JSON.stringify(val));
        },
        clear() {
            this.key = [];
            localStorage.removeItem(KEY_STORAGE);
        },
    },
});
