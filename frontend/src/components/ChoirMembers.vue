<template>
    <div class="mychoir">
        <div v-if="!loading">
            <div class="p-3">
                <div class="members">
                    <h4 class="title is-4">Members</h4>
                    <table class="table is-hoverable is-fullwidth" v-if="!loading" v-cloak>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th v-if="role && role >= Role.MANAGER">Email</th>
                                <th>Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="member in members" class="member" :key="member.id">
                                <th>{{ member.firstName }} {{ member.lastName }}</th>
                                <td v-if="role && role >= Role.MANAGER">{{ member.email }}</td>
                                <td>{{ displayRoles(member.roles) }}</td>
                            </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table>
                </div>
                <div class="new-invite mt-5" v-if="userCan('create', 'user')">
                    <h4 class="title is-4">Invite someone</h4>
                    <button class="button is-info mb-3" @click="generateToken" v-if="inviteLink == undefined">
                        Create invite link
                    </button>

                    <div class="is-flex mb-3" v-if="inviteLink">
                        <div class="control has-icons-right is-flex-grow-1 mr-2" @click="copyToken">
                            <input class="button input" v-on:focus="selectToken" ref="token" readonly
                                :value="inviteLink" />
                            <span class="icon is-small is-right">
                                <i class="fas fa-copy"></i>
                            </span>
                        </div>
                        <button class="button is-danger is-outlined" @click="deleteToken">
                            <span class="icon is-small">
                                <i class="fas fa-times"></i>
                            </span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { capitalize, computed, ref } from "vue";
import { User } from "@/entities/user";
import { useUsers } from "@/application/userStore.js";
import { useChoir } from "@/application/choirStore";
import { useAuth } from "@/application/authStore";
import { storeToRefs } from "pinia";
import { Role } from "@/types/role";

// state
const authStore = useAuth();
const { userCan } = authStore;
const { role } = storeToRefs(authStore);
const userStore = useUsers();
const choirStore = useChoir();
const inviteLink = computed(() => {
    if (choirStore.inviteToken == undefined)
        return undefined;

    let baseUrl = window.location.origin;
    return baseUrl + "/signup?invite=" + choirStore.inviteToken;
})

const members = ref<Array<User>>([]);
const token = ref();
const loading = ref(true);

userStore
    .fetchAll()
    .then((data) => {
        members.value = data;
    })
    .catch((error) => {
        console.error(error);
        error.value = "Failed to load members";
    })
    .finally(() => (loading.value = false));

const displayRoles = (roles: Role[]) => roles.map(role => capitalize(role.toLocaleString().toLocaleLowerCase())).join(", ")

const generateToken = () => choirStore.generateToken();

const deleteToken = () => choirStore.deleteToken();

const selectToken = (event: Event) => (event.target as HTMLInputElement).select();

const copyToken = function () {
    token.value.focus();
    document.execCommand("copy");
};
</script>