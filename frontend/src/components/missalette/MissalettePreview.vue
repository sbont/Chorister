<template>
  <Suspense @resolve="rendered()">
    <SundayInEastertide class="missalette-preview"></SundayInEastertide>
  </Suspense>
</template>

<script setup lang="ts">
import SundayInEastertide from "./SundayInEastertide.vue";
import { onMounted } from "vue";
import { Previewer } from "pagedjs";

onMounted(() => {});

function rendered() {
  let paged = new Previewer();
  let pages: number | null = null;
  let fontSize = 9;
  let iteration = 0;

  // do {
  paged.preview(DOMContent).then((flow) => {
    console.log("Rendered", flow.total, "pages.");
    pages = flow.total;

    if (pages && pages != 4) {
      const root = document.documentElement;
      let delta = (4 - pages) * 0.1;
      fontSize += delta;
      console.log("new fontsize", fontSize);

      root.style.setProperty("--base-font-size", `${fontSize}pt`);
      iteration++;

      paged.preview().then((flow) => {
        console.log("Rendered", flow.total, "pages.");
      });
    }
  });

  // } while (!pages || (pages != 4 && iteration < 3));
}
</script>

<style lang="scss">
@page {
  size: 148mm 210mm;
  margin-top: 8mm;
  margin-right: 8mm;
  margin-bottom: 8mm;
  margin-left: 8mm;

  @bottom-left {
    content: counter(page);
  }

  @bottom-center {
    content: string(title);
    text-transform: uppercase;
  }
}

.missalette-preview {
  columns: 2;
  gap: 5mm;

  --base-font-size: 8pt;
  --base-line-height: 1.4em;
}
</style>
