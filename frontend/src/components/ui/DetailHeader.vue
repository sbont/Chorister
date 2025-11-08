<template>
    <section class="hero is-info">
        <div class="hero-body">
            <div class="is-flex">
                <div class="is-flex-grow-1">
                    <p class="subtitle" v-if="subtitle && subtitleOnTop">
                        {{ mode == "view" ? props.subtitle : "&nbsp;" }}
                    </p>
                    <p class="title">
                        {{ mode == "view" ? props.title : mode == "create" ? "Create new" : "Edit" }}
                    </p>
                    <p class="subtitle" v-if="subtitle && !subtitleOnTop">
                        {{ mode == "view" ? props.subtitle : "&nbsp;" }}
                    </p>
                </div>
                <div class="is-flex my-3 is-flex field is-grouped">
                    <p v-if="mode == 'view'" v-for="button in customButtons" class="control">
                        <button @click="button.action" class="button is-link is-inverted">{{ button.label }}</button>
                    </p>
                    <p v-if="onEdit && mode == 'view'" class="control">
                        <button @click="$emit('edit')" class="button is-link is-inverted" :disabled="editDisabled">Edit</button>
                    </p>
                    <p v-if="onCancelEdit && mode == 'edit'" class="control">
                        <button @click="$emit('cancelEdit')" class="button">Cancel</button>
                    </p>
                    <p v-if="onDelete && mode == 'view'" class="control">
                        <button @click="$emit('delete')" class="button is-danger is-inverted" :disabled="deleteDisabled">Delete</button>
                    </p>
                </div>
            </div>
        </div>
    </section>
</template>

<script setup lang="ts">
import { PageMode, PageState } from '@/types';

defineEmits(["edit", "delete", "cancelEdit"]);

interface Props {
    title?: string
    subtitle?: string
    subtitleOnTop?: boolean
    mode?: PageMode
    onEdit?: (_: MouseEvent) => void 
    editDisabled?: boolean
    onDelete?: (_: MouseEvent) => void
    deleteDisabled?: boolean
    onCancelEdit?: (_: MouseEvent) => void
    customButtons?: Array<{action: () => void, label: string, disabled?: boolean}>
}

const props = withDefaults(defineProps<Props>(), {
    subtitleOnTop: () => false,
    mode: "view",
    editDisabled: false,
    deleteDisabled: false
})

</script>

