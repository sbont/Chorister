// @ts-check

import eslint from "@eslint/js";
import { defineConfig } from "eslint/config";
import tseslint from "typescript-eslint";
import pluginVue from "eslint-plugin-vue";
import globals from "globals";
import typescriptEslint from "typescript-eslint";
import eslintConfigPrettier from "eslint-config-prettier";

export default defineConfig(
    eslint.configs.recommended,
    tseslint.configs.recommended,
    tseslint.configs.strict,
    ...pluginVue.configs["flat/recommended"],
    {
        rules: {
            "no-unused-vars": "off",
            "vue/multi-word-component-names": "off",
            "vue/no-undef-components": [
                "error",
                {
                    ignorePatterns: ['router-view'],
                },
            ],
            "vue/require-default-prop": "off",
        },
        languageOptions: {
            sourceType: "module",
            globals: {
                ...globals.browser,
            },
            parserOptions: {
                parser: typescriptEslint.parser,
            },
        },
    },
    eslintConfigPrettier,
);










