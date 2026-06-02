import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import VueSetupExtend from 'vite-plugin-vue-setup-extend';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
export default defineConfig({
	base: './',
	server: {
		host: '127.0.0.1',
		port: 5173,
		strictPort: false,
		// 挂载盘/远程目录下默认文件监听可能失效，改为轮询保证保存后自动热更新
		watch: {
			usePolling: true,
			interval: 300
		},
		hmr: {
			host: '127.0.0.1'
		}
	},
	plugins: [
		vue(),
		VueSetupExtend(),
		AutoImport({
			resolvers: [ElementPlusResolver()]
		}),
		Components({
			resolvers: [ElementPlusResolver()]
		})
	],
	optimizeDeps: {
		include: ['schart.js']
	}
});
