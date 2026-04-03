<!-- eslint-disable vue/no-v-html -->
<template>
  <div v-if="readings">
    <ReadingComponent
      v-if="readings.reading1"
      header="First Reading"
      :reading="readings.reading1"
    />
    <Psalm :psalm="readings.psalm" />

    <ReadingComponent
      v-if="readings.reading2"
      header="Second Reading"
      :reading="readings.reading2"
    />
    <Psalm v-if="readings.psalm2" :psalm="readings.psalm2" />

    <ReadingComponent
      v-if="readings.reading3"
      header="Third Reading"
      :reading="readings.reading3"
    />
    <Psalm v-if="readings.psalm3" :psalm="readings.psalm3" />

    <ReadingComponent
      v-if="readings.reading4"
      header="Fourth Reading"
      :reading="readings.reading4"
    />
    <Psalm v-if="readings.psalm4" :psalm="readings.psalm4" />

    <ReadingComponent
      v-if="readings.reading5"
      header="Fifth Reading"
      :reading="readings.reading5"
    />
    <Psalm v-if="readings.psalm5" :psalm="readings.psalm5" />

    <ReadingComponent
      v-if="readings.reading6"
      header="Sixth Reading"
      :reading="readings.reading6"
    />
    <Psalm v-if="readings.psalm6" :psalm="readings.psalm6" />

    <ReadingComponent
      v-if="readings.reading7"
      header="Seventh Reading"
      :reading="readings.reading7"
    />
    <Psalm v-if="readings.psalm7" :psalm="readings.psalm7" />

    <Gloria v-if="isEaster === true" />
    <Collect v-if="isEaster === true" />

    <ReadingComponent
      v-if="readings.reading8"
      header="Reading from the Epistle"
      :reading="readings.reading8"
    />

    <div v-if="isEaster === true">
      <p>
        Alleluia. <strong>Alleluia.</strong><br />
        Alleluia. <strong>Alleluia.</strong><br />
        Alleluia. <strong>Alleluia.</strong>
      </p>
    </div>

    <div v-if="readings?.gospelAcclamation">
      <h2>Gospel Acclamation</h2>

      <div class="gospel-acclamation">
        <p v-html="readings?.gospelAcclamation?.text"></p>
      </div>
    </div>

    <Psalm v-if="readings.psalm8" :psalm="readings.psalm8" />

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
      <p v-html="readings.gospelReading.text"></p>
    </div>

    <div class="dialogue">
      <p>The Gospel of the Lord.</p>
      <blockquote>Praise to you, Lord Jesus Christ.</blockquote>
    </div>

    <div v-if="showCopyright" class="copyright">
      <p v-html="readings.copyright.text"></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { Event } from "@/entities/event";
import { useReadings } from "@/application/readings.store";
import { Reading, Readings } from "@/entities/reading";
import ReadingComponent from "./Reading.vue";
import Psalm from "./Psalm.vue";
import Gloria from "../introductory-rites/Gloria.vue";
import Collect from "../introductory-rites/Collect.vue";

const props = defineProps<{
  event: Event;
  isEaster?: boolean;
  showCopyright: boolean;
}>();

const readingsStore = useReadings();

// state
const readings = ref<Readings>();
if (props.event.date) {
  readingsStore.load(props.event.date).then((loadedReadings) => {
    readings.value = transform(loadedReadings);
  });
}

const gospelAuthor = computed(() => readings.value?.gospelReading.source.split(" ")[0]);

function transform(readings: Readings): Readings {
  return {
    ...readings,
    psalm: transformReading(readings.psalm),
    psalm2: readings.psalm2 && transformReading(readings.psalm2),
    psalm3: readings.psalm3 && transformReading(readings.psalm3),
    psalm4: readings.psalm4 && transformReading(readings.psalm4),
    psalm5: readings.psalm5 && transformReading(readings.psalm5),
    psalm6: readings.psalm6 && transformReading(readings.psalm6),
    psalm7: readings.psalm7 && transformReading(readings.psalm7),
    psalm8: readings.psalm8 && transformReading(readings.psalm8),
    gospelAcclamation:
      readings.gospelAcclamation && transformReading(readings.gospelAcclamation),
  };
}

function transformReading(reading: Reading): Reading {
  return {
    ...reading,
    text: transformResponsorial(reading.text),
  };
}

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
