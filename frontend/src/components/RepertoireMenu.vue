<template>
    <aside class="menu p-2">
        <p class="menu-label">Repertoire</p>
        <ul class="menu-list" v-if="ready">
            <li>
                <router-link :to="{ name: 'Repertoire' }">All songs</router-link>
            </li>
            <li v-for="entry in categories.entries()">
                <div class="menu-header">By {{ categoryTypes.get(entry[0])?.name.toLowerCase() }}</div>
                <ul v-if="!!categories">
                    <li v-for="category in entry[1]" :key="category.id">
                        <router-link :to="{ name: 'Category', params: { id: category.id } }" append>{{
                            category.name
                            }}
                        </router-link>
                    </li>
                </ul>
            </li>
        </ul>
    </aside>
</template>

<script setup lang="ts">
import { useCategories } from "@/application/categoryStore";
import { storeToRefs } from "pinia";
import { computed, onMounted } from 'vue';

const categoryStore = useCategories();

// State
const { categoriesByType: categories, categoryTypes } = storeToRefs(categoryStore);
categoryStore.fetchAll();

// Computed
const ready = computed(() => !!categories);
</script>
