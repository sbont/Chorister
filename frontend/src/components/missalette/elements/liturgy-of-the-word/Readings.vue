<!-- eslint-disable vue/no-v-html -->
<template>
  <table cellspacing="0" cellpadding="0" class="reading-header" width="100%">
    <tbody>
      <tr>
        <td><h2>First Reading</h2></td>
        <td><h4 v-html="readings?.reading1.source"></h4></td>
      </tr>
    </tbody>
  </table>

  <div class="reading">
    <p v-html="readings?.reading1.text"></p>
  </div>

  <div class="dialogue">
    <p>The word of the Lord.</p>
    <blockquote>Thanks be to God.</blockquote>
  </div>

  <table cellspacing="0" cellpadding="0" class="reading-header" width="100%">
    <tbody>
      <tr>
        <td><h2>Psalm</h2></td>
        <td><h4 v-html="readings?.psalm.source"></h4></td>
      </tr>
    </tbody>
  </table>

  <div class="psalm">
    <div v-html="readings?.psalm.text"></div>
  </div>

  <table cellspacing="0" cellpadding="0" class="reading-header" width="100%">
    <tbody>
      <tr>
        <td><h2>Second Reading</h2></td>
        <td><h4 v-html="readings?.reading2?.source"></h4></td>
      </tr>
    </tbody>
  </table>

  <div class="reading">
    <p v-html="readings?.reading2?.text"></p>
  </div>

  <div class="dialogue">
    <p>The word of the Lord.</p>
    <blockquote>Thanks be to God.</blockquote>
  </div>

  <div v-if="readings?.gospelAcclamation">
    <h2>Gospel Acclamation</h2>

    <div class="gospel-acclamation">
      <p v-html="readings?.gospelAcclamation?.text"></p>
    </div>
  </div>

  <table cellspacing="0" cellpadding="0" class="reading-header" width="100%">
    <tbody>
      <tr>
        <td><h2>Gospel</h2></td>
        <td><h4 v-html="readings?.gospelReading.source"></h4></td>
      </tr>
    </tbody>
  </table>

  <div class="dialogue">
    <p>The Lord be with you.</p>
    <blockquote>And with your spirit.</blockquote>
    <p>A reading from the holy Gospel according to {{ gospelAuthor }}.</p>
    <blockquote>Glory to you, O Lord.</blockquote>
  </div>

  <div class="gospel-reading">
    <p v-html="readings?.gospelReading.text"></p>
  </div>

  <div class="dialogue">
    <p>The Gospel of the Lord.</p>
    <blockquote>Praise to you, Lord Jesus Christ.</blockquote>
  </div>

  <div v-if="showCopyright" class="copyright">
    <p v-html="readings?.copyright.text"></p>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { Event } from "@/entities/event";
import { useReadings } from "@/application/readings.store";
import { Readings } from "@/entities/reading";

const props = defineProps<{
  event: Event;
  showCopyright: boolean;
}>();

const readingsStore = useReadings();

// state
const readings = ref<Readings>();
if (props.event.date) {
  readingsStore.load(props.event.date).then((loadedReadings) => {
    readings.value = loadedReadings;
    readings.value.psalm.text = transformResponsorial(loadedReadings.psalm.text);
    if (readings.value.gospelAcclamation) {
      readings.value.gospelAcclamation.text = transformResponsorial(
        readings.value.gospelAcclamation.text
      );
    }
  });
}

const gospelAuthor = computed(() => readings.value?.gospelReading.source.split(" ")[0]);

function transformResponsorial(html: string): string {
  const parser = new DOMParser().parseFromString(html, "text/html");
  const divs = parser.getElementsByTagName("div");

  if (divs.length) {
    const responseElement = document.createElement("blockquote");

    const firstChildIsItalic = (e: HTMLDivElement) =>
      e.childNodes.item(0).nodeName === "I";

    const responseIsItalic = firstChildIsItalic(divs[0]);
    const responseFirstLine = divs[0].innerText;
    responseElement.appendChild(document.createTextNode(responseFirstLine));

    if (responseIsItalic) {
      var i = 1;
      while (divs[i].childNodes.item(0).nodeName === "I") {
        responseElement.appendChild(document.createElement("br"));
        responseElement.appendChild(document.createTextNode(divs[i].innerText));
        i++;
      }
    }

    const elements: HTMLElement[] = [responseElement];
    var nextVerseElements: Node[] = [];

    for (var j = 0; j < divs.length; j++) {
      if (
        responseIsItalic
          ? firstChildIsItalic(divs[j])
          : divs[j].innerText === responseFirstLine
      ) {
        if (nextVerseElements.length) {
          const verseElement = document.createElement("p");
          verseElement.append(...nextVerseElements);
          elements.push(verseElement, responseElement);

          nextVerseElements = [];
        }
      } else {
        if (nextVerseElements.length) {
          nextVerseElements.push(document.createElement("br"));
        }
        nextVerseElements.push(document.createTextNode(divs[j].innerText));
      }
    }

    return elements.map((e) => e.outerHTML).join("\n");
  }

  return html;
}
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Crimson+Text");

.reading-header h2 {
  text-align: left;
}

blockquote,
:deep(blockquote) {
  font-family: "Crimson Text", serif;
  font-weight: 700;
  font-style: normal;
}

pre {
  font-family: "Crimson Text", serif;
  font-weight: 400;
  font-style: italic;
  font-size: 0.8em;
  -webkit-overflow-scrolling: initial;
  background-color: initial;
  padding: 0;
  white-space: initial;
  word-wrap: normal;
}

table.reading-header {
  width: 100%;
  border: none;
}

table.reading-header td {
  vertical-align: bottom;
}

.gospel-acclamation {
  margin: 1em 0;
}
</style>
