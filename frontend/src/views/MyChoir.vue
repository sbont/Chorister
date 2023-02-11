<template>
    <div class="mychoir">
        <div>
            <section class="hero is-medium is-info">
                <div class="hero-body">
                    <p class="subtitle">My choir</p>
                    <p class="title">{{ choir.name }}</p>
                </div>
            </section>
            <div class="p-3">

                <div class="tabs">
                    <ul>
                        <li :class="{ 'is-active': activeTab == 'members' }"><a v-on:click="activeTab = 'members'">Members</a></li>
                        <li :class="{ 'is-active': activeTab == 'categories' }"><a v-on:click="activeTab = 'categories'">Categories</a></li>
                    </ul>
                </div>

                <div class="new-members" v-if="activeTab == 'members'">
                    <ChoirMembers :choir="choir"/>
                </div>

                <div class="categories" v-if="activeTab == 'categories'">
                    hoi
                </div>

            </div>
        </div>
    </div>
</template>

<script>
import ChoirMembers from "../components/ChoirMembers.vue";
import api from "../api";
import moment from "moment";

const MyChoir = {
    name: "MyChoir",

    data: function () {
        return {
            choir: {},
            editing: false,
            draftValues: null,
            loading: true,
            saving: false,
            error: null,
            activeTab: "members"
        };
    },

    computed: {
    },

    mounted: function () {
        api.getChoirs()
            .then((response) => {
                this.choir = response.data[0];
                if (this.choir.inviteToken) {
                    let baseUrl = window.location.origin;
                    this.inviteLink = baseUrl + "/signup?invite=" + this.choir.inviteToken;
                }
                this.$log.debug("Choir loaded: ", response.data[0]);
                this.loading = false
            })
            .catch((error) => {
                this.$log.debug(error);
                this.error = "Failed to load choir";
            });
    },

    methods: {
        formatDate: function(date) {
            if (date) {
                return moment(String(date)).format('DD-MM-YYYY');
            }
        },

        save: function() {
            this.saving = true;
            var choir = this.draftValues;
            if (!choir) {
                return;
            }
            const promise = api.updateChoirForId(choir.id, choir);
            promise.then(() => {
                this.editing = false;
                this.saving = false;
                this.choir = choir;
                this.draftValues = null;
            })
        },

        edit: function() {
            this.draftValues = this.setlist;
            this.editing = true;
        },

        cancelEdit: function() {
            this.draftValues = null;
            this.editing = false;
        },

    
    },
    components: {
        ChoirMembers
    }
}

export default MyChoir;
</script>