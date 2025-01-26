<template>
    <aside class="menu p-2">
        <p class="menu-label">Repertoire</p>
        <ul class="menu-list">
            <li>
                <router-link :to="{ name: 'Repertoire' }">All songs</router-link>
            </li>
            <li v-if="ready">
                <div class="menu-header">By time of the year</div>
                <ul>
                    <li v-for="(category) in categories?.season" :key="category.id">
                        <router-link :to="{ name: 'CategorySeason', params: { id: category.id } }" append>{{
                            category.name
                        }}
                        </router-link>
                    </li>
                </ul>
            </li>
            <li v-if="ready">
                <div class="menu-header">By liturgical place</div>
                <ul>
                    <li v-for="(category) in categories?.liturgical" :key="category.id">
                        <router-link :to="{ name: 'CategoryLiturgical', params: { id: category.id } }" append>{{
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
const { categories } = storeToRefs(categoryStore);

// Computed
const ready = computed(() => !!categories);

onMounted(() => {
    categoryStore.fetchAll();
});
</script>
