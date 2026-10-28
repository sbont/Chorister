<template>
  <div class="paged-content-container">
    <div class="hidden-content">
      <div ref="sourceContent">
        <slot></slot>
      </div>
    </div>
    <div v-if="!isReady" class="loading">Generating preview...</div>
    <div ref="previewContainer" class="pagedjs-output" :class="{ 'is-ready': isReady }"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, computed, nextTick } from "vue";
import { Previewer } from "pagedjs";

const props = defineProps<{
  targetPages: number;
  pageWidth?: string;
  pageHeight?: string;
  pageMargin?: string;
  showPageNumbers?: boolean;
  pageTitle?: string;
}>();

const sourceContent = ref<HTMLElement | null>(null);
const previewContainer = ref<HTMLElement | null>(null);
let paged: Previewer | null = null;
const isReady = ref(false);

const pageCss = computed(
  () => `
@page {
  size: ${props.pageWidth || "148mm"} ${props.pageHeight || "210mm"};
  margin: ${props.pageMargin || "8mm"};

  ${
    props.showPageNumbers !== false
      ? `
  @bottom-left {
    content: counter(page);
  }

  @bottom-center {
    content: string(title);
    text-transform: uppercase;
  }
  `
      : ""
  }
}

:root {
  --pagedjs-pagebox-width: ${props.pageWidth || "148mm"};
  --pagedjs-pagebox-height: ${props.pageHeight || "210mm"};
}`
);

function cleanupPreview() {
  console.log("[PagedContent] cleanupPreview");
  if (paged) {
    // chunker.destroy() only removes DOM nodes — it never disconnects the
    // per-page ResizeObservers. A stale observer fires one final underflow
    // callback (rAF-based) after teardown and crashes in findEndToken.
    // page.removeListeners() sets listening = false and disconnects the
    // observer, so both the sync and async guards bail out.
    paged.chunker.pages.forEach((page) => page.removeListeners());

    // chunker.destroy() removes .pagedjs_pages + page template,
    // polisher.destroy() removes the styles it inserted into <head>.
    paged.chunker.destroy();
    paged.polisher.destroy();
    paged = null;
  }
}

async function renderWithSize(fontSize: number) {
  console.log("[PagedContent] renderWithSize, fontSize:", fontSize);
  if (!sourceContent.value || !previewContainer.value) {
    console.log("[PagedContent] renderWithSize aborted, missing refs");
    return 0;
  }

  // Tear down the previous iteration FIRST. Its pages still have live
  // ResizeObservers attached (page.layout() adds them after rendering),
  // so changing --base-font-size before cleanup would reflow them and
  // fire the underflow path against pages we are about to discard.
  cleanupPreview();
  previewContainer.value.innerHTML = "";

  document.documentElement.style.setProperty("--base-font-size", `${fontSize}pt`);
  await nextTick();

  const content = sourceContent.value.cloneNode(true) as HTMLElement;

  paged = new Previewer();
  try {
    // Object form is required: a plain string stylesheet is fetched as a URL.
    // This feeds the @page rules through the Polisher without touching any
    // of the app's own stylesheets.
    const flow = await paged.preview(
      content,
      [{ "pagedjs-page.css": pageCss.value }],
      previewContainer.value
    );

    // The render is final; make the pages inert so their ResizeObservers
    // cannot react to later style or DOM changes. We re-render deliberately.
    paged.chunker.pages.forEach((page) => page.removeListeners());

    console.log("[PagedContent] preview complete, pages:", flow.total);
    return flow.total;
  } catch (error) {
    console.error("[PagedContent] error in paged.preview:", error);
    return 0;
  }
}

async function adjustFontSize() {
  console.log("[PagedContent] adjustFontSize, targetPages:", props.targetPages);
  if (!previewContainer.value) return;

  isReady.value = false;

  let fontSize = 20;
  let pages: number | null = null;
  let iteration = 0;
  const maxIterations = 20;

  do {
    console.log(`[PagedContent] iteration ${iteration}, fontSize ${fontSize}`);
    pages = await renderWithSize(fontSize);

    if (pages && pages !== props.targetPages) {
      const diff = props.targetPages - pages;
      const delta = Math.pow(2.2, Math.abs(diff)) * Math.sign(diff) * 0.1;
      console.log(`[PagedContent] pages: ${pages}, diff: ${diff}, delta: ${delta}`);
      fontSize += delta;
      fontSize = Math.max(4, Math.min(100, fontSize));
    }

    iteration++;
  } while ((!pages || pages !== props.targetPages) && iteration < maxIterations);

  console.log("[PagedContent] adjustment done, final pages:", pages);
  isReady.value = true;
}

watch(
  () => [props.targetPages, props.pageWidth, props.pageHeight, props.pageMargin],
  () => {
    console.log("[PagedContent] props changed, re-adjusting");
    adjustFontSize();
  },
  { deep: true }
);

onMounted(() => {
  console.log("[PagedContent] mounted");
  adjustFontSize();
});

onUnmounted(() => {
  cleanupPreview();
});
</script>

<style lang="scss">
.paged-content-container {
  position: relative;
}

.hidden-content {
  position: absolute;
  left: -99999px;
  visibility: hidden;
  width: 0;
  height: 0;
  overflow: hidden;
}

.loading {
  padding: 40px;
  text-align: center;
  color: #666;
  font-style: italic;
}

.pagedjs-output {
  opacity: 0;
}

.pagedjs-output.is-ready {
  opacity: 1;
}
</style>
