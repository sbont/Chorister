<template>
    <div class="view">
                    <label @dblclick="editSong(song)">{{ song.title }}</label>
                    <button class="destroy" @click="removeSong(song)"></button>

                <input class="edit" type="text"
                        v-model="song.title"
                        v-song-focus="song == editedSong"
                        @blur="doneEdit(song)"
                        @keyup.enter="doneEdit(song)"
                        @keyup.esc="cancelEdit(song)">
    </div>
</template>