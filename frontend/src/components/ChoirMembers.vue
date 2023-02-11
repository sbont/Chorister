<template>
    <div class="mychoir">
        <div v-if="!loading">
            <div class="p-3">
                <div class="members">
                    <h4 class="title is-4">Members</h4>
                    <table
                        class="table is-hoverable is-fullwidth"
                        v-if="!loading"
                        v-cloak
                    >
                        <thead>
                            <th>Name</th>
                            <th>Email</th>
                        </thead>
                        <tbody>
                            <tr v-for="member in members" class="member" :key="member.id">
                                <th>{{ member.displayName }}</th>
                                <td>{{ member.email }}</td>
                            </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table>
                </div>
                
                <!-- <div class="invites mt-5">
                    <h4 class="title is-4">Open invites</h4>
                    <table
                        class="table is-hoverable is-fullwidth"
                        v-if="!loading"
                        v-cloak
                    >
                        <thead>
                            <th>Email</th>
                            <th>Date sent</th>
                            <th class="has-text-right">Revoke</th>
                        </thead>
                        <tbody>
                            <tr v-for="invite in invites" class="invite" :key="invite.id">
                                <td>{{ invite.email }}</td>
                                <td>{{ formatDate(invite.createdDate) }}</td>
                                <td class="has-text-right">
                                    <button class="button is-small is-danger is-inverted" @click="revokeInvite(invite)">
                                        <span class="icon is-small">
                                            <i class="fas fa-times"></i>
                                        </span>
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table>
                </div> -->

                <div class="new-invite mt-5">
                    <h4 class="title is-4">Invite someone</h4>
                    <button class="button is-info mb-3" @click="generateToken" v-if="inviteLink == null">
                        Create invite link
                    </button>

                    <div class="is-flex mb-3" v-if="inviteLink">
                        <div class="control has-icons-right is-flex-grow-1 mr-2" @click="copyToken">
                            <input class="button input" v-on:focus="$event.target.select()" ref="token" readonly :value="inviteLink"/>
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

                    <!-- <div class="field">
                        <label class="label">Email</label>
                        <div class="control has-icons-left">
                            <input class="input" type="email" placeholder="hello@email.com">
                            <span class="icon is-small is-left">
                              <i class="fas fa-envelope"></i>
                            </span>
                        </div>
                    </div> -->
                </div>

            </div>
        </div>
    </div>
</template>

<script>
import api from "../api";
import { onMounted, ref } from 'vue'
import moment from "moment";

export default {
    props: {
        choir: Object
    },
    setup (props) {
        // state
        
        const members = ref([])
        const invites = ref([])
        const inviteLink = ref(null)
        const editing = ref(false)
        const draftValues = ref(null)
        const loading = ref(true)
        const saving = ref(false)
        const error = ref(null)

        // Computed

        onMounted(() => {
            let membersLoaded = api.getUsers()
                .then((response) => {
                    console.log("Members loaded: ", response.data);
                    members.value = response.data;
                })
                .catch((error) => {
                    console.log(error);
                    error.value = "Failed to load members";
                });
            let invitesLoaded = api.getInvites()
                .then((response) => {
                    console.log("Invites loaded: ", response.data);
                    let allInvites = response.data;
                    invites.value = allInvites.filter(i => !i.expired);
                })
                .catch((error) => {
                    console.log(error);
                    error.value = "Failed to load invites";
                });
            Promise.allSettled([membersLoaded, invitesLoaded]).then(()=> loading.value = false)
        })

        // methods
        const formatDate = function(date) {
            if (date) {
                return moment(String(date)).format('DD-MM-YYYY');
            }
        }

        const save = function() {
            saving.value = true;
            var choir = draftValues.value;
            if (!choir) {
                return;
            }
            const promise = api.updateChoirForId(choir.id, choir);
            promise.then(() => {
                editing.value = false;
                saving.value = false;
                choir.value = choir;
                draftValues.value = null;
            })
        }

        const edit = function() {
            draftValues.value = this.setlist;
            editing.value = true;
        }

        const cancelEdit = function() {
            draftValues.value = null;
            editing.value = false;
        }

        const generateToken = function() {
            api.getToken()
                .then((response) => {
                    console.log(response);
                    inviteLink.value = response.data;
                });
        }

        const deleteToken = function() {
            api.deleteToken()
                .then(() => inviteLink.value = null);
        }

        const copyToken = function() {
            this.$refs.token.focus();
            document.execCommand('copy');
        }

        const revokeInvite = function(invite) {
            console.log(invite);
            invites.value = invites.value.filter(i => i !== invite);
            invite.expired = true;
            api.updateInviteForId(invite.id, invite);
        }

        return { 
            members, invites, inviteLink, editing, draftValues, loading, saving, error,
            formatDate, save, edit, cancelEdit, generateToken, deleteToken, copyToken, revokeInvite
         };
    },
}


</script>