<template>
    <section class="hero is-info">
        <div class="hero-body">
            <div class="is-flex">
                <div class="is-flex-grow-1">
                    <p class="subtitle" v-if="subtitle && subtitleOnTop">
                        {{ mode == "view" ? subtitle : "&nbsp;" }}
                    </p>
                    <p class="title">
                        {{ mode == "view" ? title : mode == "create" ? "Create new" : "Edit" }}
                    </p>
                    <p class="subtitle" v-if="subtitle && !subtitleOnTop">
                        {{ mode == "view" ? subtitle : "&nbsp;" }}
                    </p>
                </div>
                <div class="is-flex my-3 is-flex field is-grouped">
                    <p v-if="mode == 'view'" v-for="button in accessibleActions" class="control">
                        <button @click="button.action" class="button is-link is-inverted">{{ button.label }}</button>
                    </p>
                    <p v-if="onEdit && mode == 'view' && authStore.userCan('update', entity)" class="control">
                        <button @click="$emit('edit')" class="button is-link is-inverted"
                            :disabled="editDisabled">Edit</button>
                    </p>
                    <p v-if="onCancelEdit && mode == 'edit'" class="control">
                        <button @click="$emit('cancelEdit')" class="button">Cancel</button>
                    </p>
                    <p v-if="onDelete && mode == 'view' && authStore.userCan('delete', entity)" class="control">
                        <button @click="$emit('delete')" class="button is-danger is-inverted"
                            :disabled="deleteDisabled">Delete</button>
                    </p>
                </div>
            </div>
        </div>
    </section>
</template>

<script setup lang="ts">
import { EntityType } from '@/application/authorization';
import { useAuth } from '@/application/authStore';
import { PageMode, PageState } from '@/types';
import { AccessLevel } from '@/types/access-level';
import { storeToRefs } from 'pinia';

defineEmits(["edit", "delete", "cancelEdit"]);

interface Props {
    title?: string
    subtitle?: string
    subtitleOnTop?: boolean
    mode?: PageMode
    entity: EntityType
    onEdit?: (_: MouseEvent) => void
    editDisabled?: boolean
    onDelete?: (_: MouseEvent) => void
    deleteDisabled?: boolean
    onCancelEdit?: (_: MouseEvent) => void
    customActions?: Array<{ action: () => void, label: string, disabled?: boolean, accessLevel: AccessLevel }>
}

const props = withDefaults(defineProps<Props>(), {
    subtitleOnTop: () => false,
    mode: "view",
    editDisabled: false,
    deleteDisabled: false
})

const authStore = useAuth();
const { accessLevel } = storeToRefs(authStore); 

const accessibleActions = props.customActions?.filter(action => !accessLevel.value || action.accessLevel <= accessLevel.value);

</script>
