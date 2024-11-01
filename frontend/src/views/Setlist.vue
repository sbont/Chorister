<template>
    <div>
        <div class="setlist-detail-container">
            <SetlistDetail/>
        </div>
        <div class="songs-container">
            <div class="p-2">
                <progress v-if="state == State.Loading" class="progress is-medium is-info" max="100"></progress>
                <table class="table is-hoverable is-fullwidth song-table" v-if="state != State.Loading" v-cloak>
                    <thead>
                    <tr>
                        <th class="col0" title="order"></th>
                        <th class="col1" title="number"></th>
                        <th class="col2">Title</th>
                        <th class="col3">Composer</th>
                        <th class="col4">Songbook</th>
                        <th class="col5">No.</th>
                        <th class="col6"></th>
                    </tr>
                    </thead>
                    <tbody>

                    <tr v-for="(entry, i) in entries" class="song" :key="entry.setlistEntryUri" draggable="true">
                        <td class="col0">
                            <div class="move-container">
                                <div class="icon is-small" @click="moveUp(entry)" :class="{ 'is-disabled': i == 0 }">
                                    <i class="fas fa-caret-up"></i>
                                </div>

                                <div class="icon is-small" @click.prevent="moveDown(entry)"
                                     :class="{ 'is-disabled': isLast(entry) }">
                                    <i class="fas fa-caret-down"></i>
                                </div>
                            </div>
                        </td>
                        <td>{{ entry.number }}</td>
                        <th>
                            <router-link :to="{ name: 'Song', params: { id: entry.song?.id } }" append>
                                {{ entry.song.title }}
                            </router-link>
                        </th>
                        <td>{{ entry.song.composer }}</td>
                        <td>{{ entry.song.songbook?.title }}</td>
                        <td>{{ entry.song.songbookNumber }}</td>
                        <td class="p-1b remove">
                            <button class="button is-danger is-inverted is-small"
                                    :class="{ 'is-loading': state == State.Deleting }"
                                    @click="removeSongFromSetlist(entry as SetlistSong)">
                                <span class="icon is-small">
										<i class="fas fa-times"></i>
									</span>
                            </button>
                        </td>
                    </tr>
                    </tbody>
                    <tfoot></tfoot>
                </table>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import SetlistDetail from '@/components/SetlistDetail.vue';
import { Song } from '@/types'
import api from "@/api";
import { useRoute } from "vue-router";
import { useSetlists } from "@/stores/setlistStore";
import { computed, ref } from "vue";

type SetlistSong = {
    number: number
    song: Song
    setlistEntryUri: string
}

enum State {
    Ready,
    Loading,
    Deleting,
    Dragging
}

const state = ref<State>(State.Loading)
const route = useRoute();
const setlistStore = useSetlists()
const setlistId = Number(route.params.id);
const entries = computed(() => {
    return (setlistStore.entriesBySetlistId.get(setlistId) ?? []).map(entry => <SetlistSong>{
        number: entry.number,
        song: entry._embedded.song,
        setlistEntryUri: entry?._links?.self.href
    }) as Array<SetlistSong>
})

setlistStore.fetchEntries(setlistId).finally(() => state.value = State.Ready);

// Methods
const removeSongFromSetlist = function (song: SetlistSong) {
    state.value = State.Deleting
    let entryUri = song.setlistEntryUri;
    api.deleteByUri(entryUri)
        .then((response) => {
            removeSong(song);
        })
        .catch((error) => {
            error.value = "Failed to remove entry";
        })
        .finally(() => {
            state.value = State.Ready
        });
}

const removeSong = function (song: SetlistSong) {
    // notice NOT using "=>" syntax
    entries.value.splice(entries.value.indexOf(song), 1);
}

const moveUp = (setlistEntry: SetlistSong) => move(setlistEntry, -1)

const moveDown = (setlistEntry: SetlistSong) => move(setlistEntry, 1)

const move = (setlistEntry: SetlistSong, direction: number) => {
    setlistStore.move(setlistId, setlistEntry.number, direction)
}

const isLast = (setlistEntry: SetlistSong) => setlistEntry.number == entries.value.length

</script>

<style>
[v-cloak] {
    display: none;
}

.move-container {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.icon.is-disabled {
    color: rgba(0, 0, 0, 0.3);
}

table td.col0 {
    padding: 0.25em 0.75em;
}

table td.remove {
    padding: 0.25em 0.75em;
}

.col0 {
    width: 2%;
}

.col1 {
    width: 2%;
}

.col2 {
    width: 35%;
}

.col3 {
    width: 25%;
}

.col4 {
    width: 20%;
}

.col5 {
    width: 10%;
}

.col6 {
    width: 4%;
}

</style>