<template>
    <div class="add-row has-background-light" @mouseover="() => { if (state == State.Ready) state = State.MouseOver }"
        @mouseleave="() => { if (state == State.MouseOver) state = State.Ready }">
        <div v-if="state == State.Ready">
            <span class="icon">
                <i class="fas fa-plus"></i>
            </span>
            Add
        </div>

        <div v-if="state == State.MouseOver" class="columns">
            <div class="column has-background-info-light" @click="addSong">
                <span class="icon">
                    <i class="fas fa-plus"></i>
                </span>
                Add song
            </div>
            <div class="column has-background-primary-light" @click="addHeader">
                <span class="icon">
                    <i class="fas fa-plus"></i>
                </span>
                Add header
            </div>
        </div>

        <div v-if="state == State.AddSong" class="form">
            <div class="spacer"></div>
            <Select v-model="selectedSongId" filter :options="allSongs" option-label="title" option-value="id"
                placeholder="Select song..." class="selector mr-2" />
            <button class="button is-primary" @click="save">Add</button>

        </div>
    </div>
</template>

<script setup lang="ts">
import { useEvents } from '@/application/eventStore';
import { useSongs } from '@/application/songStore';
import { ref } from 'vue';
import Select from 'primevue/select';
import { storeToRefs } from 'pinia';

const props = defineProps<{ eventId: number }>();
const emit = defineEmits(["add"]);

enum State {
    Ready,
    MouseOver,
    AddSong,
    AddHeader
}

const state = ref<State>(State.Ready)
const selectedSongId = ref<number>();
const headerName = ref<string>();

const eventStore = useEvents();
const songStore = useSongs();
songStore.fetchAll();
const { allSongs } = storeToRefs(songStore);

// Methods
const addHeader = () => {
    state.value = State.AddHeader;
}
const addSong = () => {
    state.value = State.AddSong;
}

const addEntry = () => {
    state.value = State.AddHeader;
}

const save = async () => {
    if (state.value == State.AddSong && selectedSongId.value) {
        const entry = await eventStore.addEventEntry(props.eventId, { songId: selectedSongId.value })
        console.log(entry);

        emit("add", entry);
    } else if (state.value == State.AddHeader) {
        emit("add", { label: headerName.value });
    }

}

</script>

<style>
.selector {
    min-width: 25rem;
}

.form {
    display: flex;
    align-items: center;
}

.spacer {
    width: 6%;
}
</style>