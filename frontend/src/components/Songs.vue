<template>
    <div class="p-2">
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{ error }}
        </div>
        <DataTable :value="songs" ref="datatable" v-model:selection="selectedRows" size="small" :loading="loading">
            <template #header>
                <div class="is-flex is-justify-content-space-between">
                    <div class="is-flex title">
                        {{ header }}
                    </div>
                    <div class="is-flex is-gap-2">
                        <Button type="button" class="button mr-2" label="Categorise" @click="toggleCategorizeMenu"
                            :disabled="!selectedRows.length" :class="{ 'is-loading': isSavingCategories }"
                            aria-haspopup="true" aria-controls="overlay_tmenu" unstyled></Button>
                        <TieredMenu ref="customizeMenu" id="overlay_tmenu" :model="categorizeMenuEntries" popup />

                        <router-link class="button is-primary" :to="{ name: 'NewSong' }" append tag="button">
                            <span class="icon is-small">
                                <i class="fas fa-plus"></i>
                            </span>
                            <span>Add</span>
                        </router-link>

                    </div>
                </div>
            </template>
            <template #empty v-if="loading == false"> No songs found. </template>

            <Column body-class="col-select" selection-mode="multiple"></Column>
            <Column body-class="col-no">
                <template #body="slotProps">
                    <span>
                        {{ oneBased(slotProps.index) }}
                    </span>
                </template>
            </Column>
            <Column header="Title" body-class="col-title">
                <template #body="slotProps">
                    <router-link :to="{ name: 'Song', params: { id: (slotProps.data as Song).id } }" append
                        class="has-text-weight-semibold">
                        {{ (slotProps.data as Song).title }}
                    </router-link>
                </template>
            </Column>
            <Column field="composer" header="Composer" body-class="col-composer"></Column>
            <Column field="lastEvent.date" header="Last Included" body-class="col-last-played">
                <template #body="slotProps">
                    {{ slotProps.data.lastEvent ? useDateFormat(slotProps.data.lastEvent?.date, 'DD-MM-YYYY') : '' }}
                </template>
            </Column>
            <Column header="Categories" body-class="col-category">
                <template #body="slotProps">
                    <div class="tags">
                        <span v-for="(category, index) in categoryStore.songCategories(slotProps.data.id)"
                            class="song-category tag is-normal" :key="index">
                            {{ category.name }}
                        </span>
                    </div>
                </template>
            </Column>
            <template #footer>
                <strong>{{ songs.length }}</strong> {{ pluralize(songs.length) }}
            </template>
        </DataTable>
    </div>
</template>

<script setup lang="ts">
import { computed, Ref, ref } from 'vue'
import { useSongs } from "@/application/songStore.js";
import { useRoute } from "vue-router";
import { Song } from "@/entities/song";
import DataTable, { DataTableRowSelectEvent } from "primevue/datatable";
import Column from "primevue/column";
import TieredMenu from "primevue/tieredmenu";
import Button from "primevue/button";
import { useCategories } from '@/application/categoryStore';
import { storeToRefs } from 'pinia';
import type { MenuItem, MenuItemCommandEvent } from 'primevue/menuitem';
import { Category } from '@/entities/category';
import { useDateFormat, useNow } from '@vueuse/core'

// Types
const songStore = useSongs();
const { fetchAll, fetchAllForCategory, allSongs } = songStore;
const categoryStore = useCategories();
const { categoriesByType } = storeToRefs(categoryStore);

const route = useRoute();
const customizeMenu = ref();

// state
categoryStore.initialize();
const header = ref("All songs");
const loading = ref(true);
const isSavingCategories = ref(false);
const error = ref<string | null>(null);
const datatable = ref();

const routeName = route.name;

let songsLoaded;
const songs = ref<Array<Song>>([]);

switch (routeName) {
    case "Repertoire":
        songsLoaded = fetchAll();
        songs.value = allSongs;
        if (songs.value.length)
            loading.value = false;

        break;
    case "Category":
        const categoryId = Number(route.params.id);
        categoryStore.get(categoryId).then((data) => {
            header.value = data?.name ?? "Category";
        });
        songsLoaded = fetchAllForCategory(categoryId);
        break;
}
songsLoaded!
    .then(data => {
        songs.value = data;
    })
    .finally(() => (loading.value = false));

const categorizeMenuEntries = computed(() => {
    if (categoriesByType.value.size) {
        return Array.from(categoriesByType.value, ([typeUri, categories]) => {
            return {
                label: categoryStore.categoryTypes.get(typeUri)?.name,
                items: categories.map(c => ({ 
                    label: c.name, 
                    key: c.uri ,
                    command: categorizeFn(c)
                })),
            } as MenuItem;
        });
    } else {
        return [{
            label: 'Loading...',
            icon: 'fas fa-spinner fa-pulse'
        }];
    }
});

const selectedRows = ref<Song[]>([]);


// Methods
const pluralize = function (n: number) {
    return n === 1 ? "song" : "songs";
}

const handleErrorClick = function () {
    error.value = null;
}

const oneBased = (index: number) => index + 1;

const toggleCategorizeMenu = (event: Event) => {
    customizeMenu.value.toggle(event);
}

const categorizeFn = (category: Category) => {
    return async (_: MenuItemCommandEvent) => {
        isSavingCategories.value = true;
        try {
            await categoryStore.addForSongs(selectedRows.value, category);
            selectedRows.value = [];
        } catch(e) {
            console.log(e);
            error.value = `${error}`;
        }
        isSavingCategories.value = false;
    }
} 

</script>

<style>
[v-cloak] {
    display: none;
}

.song-table {
    table-layout: fixed;
}

td.p-1b {
    padding: 0.3em;
}

.col-select {
    width: 3%;
}

.col-no {
    width: 3%;
}

.col-title {
    width: 25%;
}

.col-composer {
    width: 20%;
}

.col-songbook {
    width: 15%;
}

.col-songbook-no {
    width: 5%;
}

.col-last-played {
    width: 10%;
}

.col-category {
    width: 32%;
    max-width: 40%;
}

.col-category .tags {
    flex-wrap: initial;
}
</style>