<!-- eslint-disable vue/no-v-html -->
<template>
  <div class="mass-order">
    <h1>The Introductory Rites</h1>

    <div class="song"><span class="song-heading">Entrance hymn: </span><span class="song-title"><strong>{{ songByIndex(1) }}</strong></span></div>

    <h2>Greeting</h2>

    <div class="dialogue">
      <p>In the name of the Father, and of the Son, and of the Holy Spirit.</p>
      <p><strong>Amen.</strong></p>
    </div>

    <div class="dialogue">
      <p>The grace of our Lord Jesus Christ, and the love of God, and the communion of the Holy Spirit be with you all.<br>
        Or:<br>
        Grace to you and peace from God our Father and the Lord Jesus Christ.<br>
        Or:<br>
        The Lord be with you.</p>
      <p><strong>And with your spirit.</strong></p>
    </div>

    <h2>The Collect</h2>

    <div class="collect">
      <p>Let us pray.</p>
      <p><em>And all pray in silence with the Priest for a moment. Then the Priest, with hands extended, says the Collect prayer, at the end of which the people acclaim:</em></p>
      <p><strong>Amen.</strong></p>
    </div>

    <h1>The Liturgy of the Word</h1>

    <h2>First Reading</h2>

    <div class="reading">
      <p v-html="readings?.reading1.source"></p>
      <p v-html="readings?.reading1.text"></p>
    </div>

    <div class="dialogue">
      <p>The word of the Lord.</p>
      <p><strong>Thanks be to God.</strong></p>
    </div>

    <h2>Psalm</h2>

    <div class="psalm">
      <p v-html="readings?.psalm.source"></p>
      <p v-html="readings?.psalm.text"></p>
    </div>

    <h2>Second Reading</h2>

    <div class="reading">
      <p v-html="readings?.reading2?.source"></p>
      <p v-html="readings?.reading2?.text"></p>
    </div>

    <div class="dialogue">
      <p>The word of the Lord.</p>
      <p><strong>Thanks be to God.</strong></p>
    </div>

    <h2>Gospel Acclamation</h2>

    <div class="gospel-acclamation">
      <p v-html="readings?.gospelAcclamation.text"></p>
    </div>

    <h2>Gospel</h2>

    <div class="dialogue">
      <p>The Lord be with you.</p>
      <p><strong>And with your spirit.</strong></p>
      <p>A reading from the holy Gospel according to {{ gospelAuthor }}.</p>
      <p><strong>Glory to you, O Lord.</strong></p>
    </div>

    <div class="gospel-reading">
      <p v-html="readings?.gospelReading.source"></p>
      <p v-html="readings?.gospelReading.text"></p>
    </div>

    <div class="dialogue">
      <p>The Gospel of the Lord.</p>
      <p><strong>Praise to you, Lord Jesus Christ.</strong></p>
    </div>

    <h2>The Homily</h2>

    <h2>Blessing of Ashes</h2>

    <h2>Distribution of Ashes</h2>

    <div class="distribution-of-ashes">
      <p><em>The Priest places ashes on the head of all those present who come to him, and says to each one:</em></p>
      <p>Repent, and believe in the Gospel.<br>
        Or:<br>
        Remember that you are dust, and to dust you shall return.</p>
    </div>

    <div class="song"><span class="song-heading">Ash Wednesday hymn: </span><span class="song-title"><strong>{{ songByIndex(2) }}</strong></span></div>

    <h2>The Prayer of the Faithful</h2>

    <div class="prayer-of-faithful">
      <p><em>After each intention there is a pause while the faithful pray.</em></p>
      <p>Lord, in your mercy.</p>
      <p><strong>Hear our prayer.</strong></p>
    </div>

    <h1>The Liturgy of the Eucharist</h1>

    <div class="song"><span class="song-heading">Offertory hymn: </span><span class="song-title"><strong>{{ songByIndex(3) }}</strong></span></div>

    <div class="dialogue">
      <p>Pray, brothers and sisters, that my sacrifice and yours may be acceptable to God, the almighty Father.</p>
      <p><strong>May the Lord accept the sacrifice at your hands for the praise and glory of his name, for our good and the good of all his holy Church.</strong></p>
    </div>

    <div class="collect">
      <p><em>Then the Priest says the Prayer over the Offerings, at the end of which the people acclaim:</em></p>
      <p><strong>Amen.</strong></p>
    </div>

    <h2>The Eucharistic Prayer</h2>

    <div class="dialogue">
      <p>The Lord be with you.</p>
      <p><strong>And with your spirit.</strong></p>
      <p>Lift up your hearts.</p>
      <p><strong>We lift them up to the Lord.</strong></p>
      <p>Let us give thanks to the Lord our God.</p>
      <p><strong>It is right and just.</strong></p>
    </div>

    <h3>The Preface</h3>

    <div class="preface">
      <p><em>The Priest concludes the Preface with the people, singing</em></p>
      <p><strong>Sanctus, Sanctus, Sanctus<br>
        Dominus Deus Sabaoth.<br>
        Pleni sunt cæli et terra gloria tua.<br>
        Hosanna in excelsis.<br>
        Benedictus qui venit in nomine Domini.<br>
        Hosanna in excelsis.</strong></p>
    </div>

    <div class="dialogue">
      <p><em>After the words of Consecration the priest says:</em></p>
      <p>The mystery of faith.</p>
      <p><strong>We proclaim your Death, O Lord, and profess your Resurrection until you come again.<br>
        Or:<br>
        When we eat this Bread and drink this Cup, we proclaim your Death, O Lord, until you come again.<br>
        Or:<br>
        Save us, Saviour of the world, for by your Cross and Resurrection you have set us free.</strong></p>
    </div>

    <div class="doxology">
      <p><em>At the conclusion of the prayer the Priest takes the chalice and the paten with the host and, raising both, he says:</em></p>
      <p>Through him, and with him, and in him,<br>
        O God, almighty Father,<br>
        in the unity of the Holy Spirit,<br>
        all glory and honour is yours,<br>
        for ever and ever.</p>
      <p><strong>Amen.</strong></p>
    </div>

    <h1>The Communion Rite</h1>

    <h2>The Lord's Prayer</h2>

    <div class="dialogue">
      <p>At the Saviour's command and formed by divine teaching, we dare to say:</p>
      <p><strong>Our Father, who art in heaven,<br>
        hallowed be thy name;<br>
        thy kingdom come,<br>
        thy will be done on earth as it is in heaven.<br>
        Give us this day our daily bread,<br>
        and forgive us our trespasses,<br>
        as we forgive those who trespass against us;<br>
        and lead us not into temptation,<br>
        but deliver us from evil.</strong></p>
    </div>

    <div class="dialogue">
      <p>Deliver us, Lord, we pray, from every evil…<br>
        and the coming of our Saviour, Jesus Christ.</p>
      <p><strong>For the kingdom, the power and the glory are yours now and for ever.</strong></p>
    </div>

    <div class="dialogue">
      <p>Lord Jesus Christ, who said to your Apostles…<br>
        Who live and reign for ever and ever.</p>
      <p><strong>Amen.</strong></p>
    </div>

    <div class="dialogue">
      <p>The peace of the Lord be with you always.</p>
      <p><strong>And with your spirit.</strong></p>
      <p>Let us offer each other the sign of peace.</p>
      <p><i>All offer one another the customary sign of peace.</i></p>
    </div>

    <h2>Breaking of the Bread</h2>

    <div class="lamb-of-god">
      <p><strong>Agnus Dei, qui tollis peccáta mundi: miserére nobis.<br>
        Agnus Dei, qui tollis peccáta mundi: miserére nobis.<br>
        Agnus Dei, qui tollis peccáta mundi: dona nobis pacem.</strong></p>
    </div>

    <h2>Communion</h2>

    <div class="dialogue">
      <p>Behold the Lamb of God, behold him who takes away the sins of the world.<br>
        Blessed are those called to the supper of the Lamb.</p>
      <p><strong>Lord, I am not worthy that you should enter under my roof, but only say the word and my soul shall be healed.</strong></p>
    </div>

    <div class="dialogue">
      <p><em>The communicants come forward in reverent procession, and make a preparatory act of reverence by bowing their head in honour of Christ's presence in the Sacrament.</em></p>
      <p>The Body of Christ.</p>
      <p><strong>Amen.</strong></p>
    </div>

    <div class="song"><span class="song-heading">Communion hymn: </span><span class="song-title"><strong>{{ songByIndex(6) }}</strong></span></div>
    <div class="song"><span class="song-heading">Thanksgiving hymn: </span><span class="song-title"><strong>{{ songByIndex(7) }}</strong></span></div>

    <h2>Prayer after Communion</h2>

    <div class="collect">
      <p>Let us pray.</p>
      <p><em>All pray in silence. Then the Priest says the Prayer after Communion, at the end of which the people acclaim:</em></p>
      <p><strong>Amen.</strong></p>
    </div>

    <div class="concluding-rites">
      <h1>The Concluding Rites</h1>
        <div class="dialogue">
        <p>The Lord be with you.</p>
        <p><strong>And with your spirit.</strong></p>
        <p>May almighty God bless you, the Father, and the Son, and the Holy Spirit.</p>
        <p><strong>Amen.</strong></p>
        <p>Go forth, the Mass is ended</p>
        <p><strong>Thanks be to God.</strong></p>
      </div>
    </div>

    <div class="song"><span class="song-heading">Recessional hymn: </span><span class="song-title"><strong>{{ songByIndex(8) }}</strong></span></div>

    <div class="copyright">
      <p v-html="readings?.copyright.text"></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { computed, ref } from "vue";
import { Event } from "@/entities/event";
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";
import { useReadings } from "@/application/readings.store";
import { Readings } from "@/entities/reading";

const eventStore = useEvents();
const readingsStore = useReadings();
const route = useRoute();

// state
const eventId = Number(route.params.id);
const { entries: getEntries } = storeToRefs(eventStore);
const event = ref<Event>();
const readings = ref<Readings>();
const entries = computed(() => event.value?.uri ? getEntries.value(event.value.uri) : []);
eventStore.fetch(eventId).then(async (result) => {
    event.value = result;
    if (result.date) {
      readings.value = await readingsStore.load(result.date);
    }
});
const gospelAuthor = computed(() => readings.value?.gospelReading.source.split(' ')[0]);
const songByIndex = computed(() => (index: number) => entries.value[index - 1]?.song?.embedded?.title ?? '');

</script>


<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Crimson+Text');

.crimson-text-regular {
  font-family: "Crimson Text", serif;
  font-weight: 400;
  font-style: normal;
}

.crimson-text-semibold {
  font-family: "Crimson Text", serif;
  font-weight: 600;
  font-style: normal;
}

.crimson-text-bold {
  font-family: "Crimson Text", serif;
  font-weight: 700;
  font-style: normal;
}

.crimson-text-regular-italic {
  font-family: "Crimson Text", serif;
  font-weight: 400;
  font-style: italic;
}

.crimson-text-semibold-italic {
  font-family: "Crimson Text", serif;
  font-weight: 600;
  font-style: italic;
}

.crimson-text-bold-italic {
  font-family: "Crimson Text", serif;
  font-weight: 700;
  font-style: italic;
}

.mass-order {
  font-family: "Crimson Text", serif;
  font-weight: 400;
  font-style: normal;
  line-height: 1.6;
  margin: 0 auto;
  padding: 20px;
  max-width: 400px;
}

h1 {
  font-size: 1.3em;
  margin: 1em 0 0..5em 0;
  padding-bottom: 0.5em;
  text-transform: uppercase;
  text-align: center;
}

h2 {
  font-size: 1.2em;
  margin: 1em 0 1em 0;
  text-align: center;
}

h3 {
  font-size: 1.2em;
  margin: 1em 0 0.5em 0;
  font-style: italic;
  text-align: center;
}

.song {
  text-align: center;
  
}
.song-heading {
  font-style: italic;
}
.song-title {
  font-style: bold;
}

.dialogue {
  margin: 1em 0;
}

.dialogue p {
  margin: 0.5em 0;
}

.collect {
  margin: 1em 0;
}

.gospel-acclamation {
  margin: 1em 0;
  /* text-align: center; */
  font-weight: bold;
}

.distribution-of-ashes, .prayer-of-faithful {
  margin: 1em 0;
}

.preface {
  margin: 1em 0;
}

.doxology {
  margin: 1em 0;
  /* text-align: center; */
  font-style: italic;
}

.lamb-of-god {
  margin: 1em 0;
  /* text-align: center; */
}


</style>