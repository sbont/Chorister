<template>
  <div>
    <section class="hero is-info missalette-header">
      <div class="hero-body">
        <div class="header-row">
          <div class="header-title">
            <p class="title">{{ event?.name }}</p>
            <p v-if="event" class="subtitle">{{ format(event.date) }}</p>
          </div>
          <div class="header-actions">
            <div class="control">
              <button
                class="button is-link is-inverted"
                type="button"
                @click="goBack"
              >
                Back
              </button>
            </div>
            <div class="control">
              <span class="select is-link">
                <select v-model.number="pages" aria-label="Missalette page count">
                  <option :value="2">2 pages</option>
                  <option :value="4">4 pages</option>
                  <option :value="8">8 pages</option>
                  <option :value="12">12 pages</option>
                </select>
              </span>
            </div>
            <div class="control">
              <button
                class="button is-link is-inverted"
                type="button"
                :disabled="!isPagedReady"
                @click="pagedContent?.printResult()"
              >
                Print
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- SundayInEastertide is async (it fetches its event data): the
         Suspense keeps PagedContent unmounted until the content has
         resolved, so its adjustment loop paginates the complete markup
         rather than an empty or half-rendered missalette. -->
    <Suspense>
      <PagedContent
        ref="pagedContent"
        :target-pages="pages"
        page-width="148mm"
        page-height="210mm"
        page-margin="8mm"
      >
        <SundayInEastertide class="missalette-preview" />
      </PagedContent>
      <template #fallback>
        <div class="loading">Loading missalette…</div>
      </template>
    </Suspense>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import SundayInEastertide from "./SundayInEastertide.vue";
import PagedContent from "../PagedContent.vue";
import { useEvents } from "@/application/eventStore";
import { Event } from "@/entities/event";

const route = useRoute();
const router = useRouter();

// The parent Event page has usually already fetched this event into the
// store; `get` returns the cached value when present.
const eventStore = useEvents();
const event = ref<Event>();
eventStore.get(Number(route.params.id)).then((value) => {
  event.value = value;
});

const format = (date: Date) => new Date(date).toLocaleDateString();

const pageOptions = [2, 4, 8, 12];
const targetPages = computed(() => {
  const value = Number(route.query.pages);
  return pageOptions.includes(value) ? value : 4;
});

// Two-way binding: the dropdown writes the `pages` query parameter,
// PagedContent watches the resulting target and re-adjusts.
const pages = computed({
  get: () => targetPages.value,
  set: (value: number) => {
    if (value === targetPages.value) return;
    router.replace({ query: { ...route.query, pages: value } });
  },
});

const pagedContent = ref<InstanceType<typeof PagedContent> | null>(null);
const isPagedReady = computed(() => pagedContent.value?.isReady === true);

const goBack = () => {
  router.push({ name: "Event", params: { id: route.params.id } });
};
</script>

<style lang="scss">
.missalette-header .hero-body {
  padding: 1rem 1.5rem;
}

.header-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.header-title {
  flex: 1 1 auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.header-actions {
  flex: 1 1 auto;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 0.5rem;
}

.missalette-preview {
  columns: 2;
  gap: 5mm;

  --base-line-height: 1.4em;

  font-size: var(--base-font-size);
  font-family: "Crimson Text", serif;
  font-weight: 400;
  font-style: normal;
}

.loading {
  padding: 40px;
  text-align: center;
  color: #666;
  font-style: italic;
}
</style>
