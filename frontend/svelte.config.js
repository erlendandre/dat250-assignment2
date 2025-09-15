// import { vitePreprocess } from '@sveltejs/vite-plugin-svelte'

// /** @type {import("@sveltejs/vite-plugin-svelte").SvelteConfig} */
// export default {
//   // Consult https://svelte.dev/docs#compile-time-svelte-preprocess
//   // for more information about preprocessors
//   preprocess: vitePreprocess(),
// }


import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import("@sveltejs/vite-plugin-svelte").SvelteConfig} */
export default {
  // Preprocessing for Svelte
  preprocess: vitePreprocess(),

  // Dette gjør at gamle `new App({...})` koden fungerer i Svelte 5
  compilerOptions: {
    compatibility: {
      componentApi: 4
    }
  }
};