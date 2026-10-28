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

async function rendered() {
  let paged = new Previewer();
  let pages: number | null = null;
  let fontSize = 15;
  let iteration = 1;

  do {
    const root = document.documentElement;
    const fontStyle = `--base-font-size: ${fontSize}pt;`;
    root.style.setProperty("--base-font-size", `${fontSize}pt`);

    const flow = await paged.preview(undefined, [fontStyle]);
    console.log("Rendered attempt", iteration, "with", flow.total, "pages.");
    pages = flow.total;

    if (pages && pages != 4) {
      let delta = Math.pow(2.4, Math.abs(4 - pages)) * Math.sign(4 - pages) * 0.1;
      console.log(Math.pow(2.4, Math.abs(4 - pages)));
      console.log(delta);

      fontSize += delta;
      console.log("new fontsize", fontSize);

      iteration++;
    }
  } while (!pages || (pages != 4 && iteration <= 12));
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

  /* --base-font-size: 8pt; */
  --base-line-height: 1.4em;

  font-size: var(--base-font-size);
  font-family: "Crimson Text", serif;
  font-weight: 400;
  font-style: normal;
}
</style>
