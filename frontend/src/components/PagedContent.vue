<template>
  <div class="paged-content-container">
    <div class="hidden-content">
      <div ref="sourceContent">
        <slot></slot>
      </div>
    </div>
    <div class="paged-content-toolbar">
      <button
        class="print-button"
        type="button"
        :disabled="!isReady"
        @click="printResult"
      >
        Print…
      </button>
    </div>
    <div v-if="!isReady" class="loading">Generating preview...</div>
    <div
      ref="previewContainer"
      class="pagedjs-output"
      :class="{ 'is-ready': isReady }"
    ></div>
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

// The adjustment loop is async and long-running. Only the most recent run
// may render: prop changes supersede earlier runs, unmounting stops them.
let runToken = 0;
let disposed = false;

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
    content: ${props.pageTitle ? `"${props.pageTitle}"` : "string(title)"};
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

  const token = ++runToken;
  isReady.value = false;

  let fontSize = 30;
  let lastRendered: number | null = null;

  // Bounds learned from renders. Once both are known, the fit/spill
  // boundary is bracketed and bisection pins it down deterministically.
  //   minFont: pages <= target — the content fits, the font can grow
  //   maxFont: pages > target — the content spills, the font must shrink
  let minFont: number | null = null;
  let maxFont: number | null = null;

  // Objective: the LARGEST font size that still fits within targetPages
  // (bigger font = fuller pages = most readable). An exact page count is
  // therefore NOT a stop condition — a slightly larger font may produce
  // the same count with a fuller final page. The search continues until
  // the boundary is exhausted.
  let bestExactFont: number | null = null; // exact count, largest font seen
  // Fallback if the exact count is unreachable (the page count is a
  // discrete step function, e.g. it may jump 3 → 5 with no 4 in between).
  let bestFallbackFont = fontSize;
  let bestFallbackPages = -Infinity;

  const isBetterFallback = (candidate: number) =>
    Math.abs(candidate - props.targetPages) <
      Math.abs(bestFallbackPages - props.targetPages) ||
    (Math.abs(candidate - props.targetPages) ===
      Math.abs(bestFallbackPages - props.targetPages) &&
      // On a tie prefer fitting within the target over spilling.
      candidate <= props.targetPages);

  for (let iteration = 0; iteration < 20; iteration++) {
    if (disposed || token !== runToken) {
      console.log("[PagedContent] adjustment superseded, aborting");
      return;
    }

    console.log(
      `[PagedContent] iteration ${iteration}, fontSize ${fontSize.toFixed(2)},` +
        ` bracket [${minFont?.toFixed(2) ?? "-"}, ${maxFont?.toFixed(2) ?? "-"}]`
    );
    const pages = await renderWithSize(fontSize);
    lastRendered = fontSize;

    if (!pages) {
      console.error("[PagedContent] render failed, stopping adjustment");
      break;
    }

    if (pages === props.targetPages) {
      if (bestExactFont === null || fontSize > bestExactFont) {
        bestExactFont = fontSize;
      }
    } else if (isBetterFallback(pages)) {
      bestFallbackFont = fontSize;
      bestFallbackPages = pages;
    }

    if (pages > props.targetPages) {
      maxFont = fontSize;
    } else {
      minFont = fontSize;
    }

    if (minFont !== null && maxFont !== null) {
      // Bracketed: halve the interval. The step function guarantees a
      // decision in O(log) renders regardless of any model error.
      if (maxFont - minFont < 0.05) {
        // The fit/spill boundary is pinned as finely as font sizes are
        // meaningful; the best render is restored after the loop.
        break;
      }
      fontSize = (minFont + maxFont) / 2;
    } else if (pages === props.targetPages) {
      // Fits exactly: probe upward for an even fuller fit at the same
      // page count.
      fontSize *= 1.05;
    } else {
      // Unbracketed: scale along pages ∝ fontSize² (a page's capacity
      // shrinks with the square of the font: fewer chars per line AND
      // fewer lines per page). This lands near the optimum in one step
      // from any distance, e.g. 10 pages at 30pt → ×√(4/10) → 19pt.
      const estimate = fontSize * Math.sqrt(props.targetPages / pages);

      // Plateau guard: if the estimate barely moved, jump toward the
      // unknown side so every render teaches us something new.
      fontSize =
        Math.abs(estimate - fontSize) < 0.05
          ? minFont !== null
            ? fontSize * 1.25
            : fontSize * 0.8
          : estimate;
    }

    fontSize = Math.max(4, Math.min(100, fontSize));
  }

  const chosenFont = bestExactFont ?? bestFallbackFont;
  console.log(
    "[PagedContent] adjustment done, chosen font:",
    chosenFont.toFixed(2),
    bestExactFont !== null
      ? `(exact ${props.targetPages} pages, fullest fit)`
      : `(closest reachable: ${bestFallbackPages} pages)`
  );

  if (disposed || token !== runToken) return;

  // Restore the chosen render if the loop's final state is not it.
  if (bestFallbackPages > 0 || bestExactFont !== null) {
    if (lastRendered !== chosenFont) {
      console.log("[PagedContent] restoring chosen render at", chosenFont.toFixed(2));
      await renderWithSize(chosenFont);
    }
  }

  if (token === runToken && !disposed) {
    isReady.value = true;
  }
}

watch(
  () => [props.targetPages, props.pageWidth, props.pageHeight, props.pageMargin],
  () => {
    console.log("[PagedContent] props changed, re-adjusting");
    adjustFontSize();
  }
);

function printResult() {
  console.log("[PagedContent] printResult");
  if (!previewContainer.value || !isReady.value) return;

  // Must run synchronously in the click handler so popup blockers
  // attribute it to the user gesture.
  const printWindow = window.open("", "paged-content-print", "width=900,height=1100");
  if (!printWindow) {
    console.error("[PagedContent] popup blocked — allow popups for this site to print");
    return;
  }

  // The popup must be a self-contained snapshot: copy every stylesheet
  // the rendered pages depend on — the app's own styles (content
  // typography, fonts) plus the base and processed page styles the
  // Paged.js Polisher inserted into <head>.
  const styles = Array.from(
    document.head.querySelectorAll("style, link[rel='stylesheet']")
  )
    .map((element) => element.outerHTML)
    .join("\n");

  const fontSize =
    document.documentElement.style.getPropertyValue("--base-font-size") ||
    "16pt";
  const title = props.pageTitle || "Paged content";

  printWindow.document.open();
  printWindow.document.write(`<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    ${styles}
    <style>
      :root { --base-font-size: ${fontSize}; }

      /* Paged.js builds the page margins INTO its page boxes, so the
         physical sheet itself needs no margin. */
      @page { size: ${props.pageWidth || "148mm"} ${props.pageHeight || "210mm"}; margin: 0; }

      body { background: #808080; margin: 0; padding: 24px 0; display: flex; flex-direction: column; align-items: center; gap: 24px; }
      .pagedjs_page { box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4); background: white; }

      .print-toolbar {
        position: fixed; top: 12px; right: 12px; z-index: 1000;
        padding: 8px 20px; font: 14px sans-serif;
        border: none; border-radius: 4px; cursor: pointer;
        background: #2563eb; color: white;
      }

      @media print {
        body { background: white; padding: 0; gap: 0; display: block; }
        .pagedjs_page { box-shadow: none; break-after: page; }
        .pagedjs_page:last-child { break-after: auto; }
        .print-toolbar { display: none; }
      }
    </style>
  </head>
  <body>
    <button class="print-toolbar" type="button" onclick="window.print()">Print</button>
    ${previewContainer.value.innerHTML}
  </body>
</html>`);
  printWindow.document.close();
}

onMounted(() => {
  console.log("[PagedContent] mounted");
  adjustFontSize();
});

onUnmounted(() => {
  disposed = true;
  runToken++; // invalidate any in-flight adjustment loop
  cleanupPreview();
});
</script>

<style lang="scss">
.paged-content-container {
  position: relative;
}

.paged-content-toolbar {
  display: flex;
  justify-content: flex-end;
}

.print-button {
  padding: 8px 20px;
  font: 14px sans-serif;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: #2563eb;
  color: white;

  &:disabled {
    background: #9ca3af;
    cursor: not-allowed;
  }
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
