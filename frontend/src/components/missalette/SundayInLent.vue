<!-- eslint-disable vue/no-v-html -->
<template>
  <div class="mass-order">
    <h1>The Introductory Rites</h1>

    <div class="song">
      <h5>Entrance hymn</h5>
      <h6>{{ songByIndex(1) }}</h6>
    </div>

    <h2>Greeting</h2>

    <div class="dialogue">
      <p>In the name of the Father, and of the Son, and of the Holy Spirit.</p>
      <blockquote>Amen.</blockquote>
    </div>

    <div class="dialogue">
      <p>The grace of our Lord Jesus Christ, and the love of God, and the communion of the Holy Spirit be with you all.<br>
        Or:<br>
        Grace to you and peace from God our Father and the Lord Jesus Christ.<br>
        Or:<br>
        The Lord be with you.<br>
        Or:
      </p>
        <pre>Then the Priest greets the people, to which they reply:</pre>
      <blockquote>And with your spirit.</blockquote>
    </div>

    <h2>Penitential Act</h2>

    <div class="dialogue">
      <blockquote>I confess to almighty God and to you, my brothers and sisters, that I have greatly sinned, in my thoughts and in my words, in what I have done and in what I have failed to do, 
        <i>through my fault, through my fault, through my most grievous fault;</i>
        therefore I ask blessed Mary ever-Virgin, all the Angels and Saints, and you, my brothers and sisters, to pray for me to the Lord our God.</blockquote>
      <p>May almighty God have mercy on us, forgive us our sins, and bring us to everlasting life.</p>
      <blockquote>Amen.</blockquote>
    </div>

    <div class="kyrie">
      <p>Lord, have mercy. <strong>Lord, have mercy.</strong></p>
      <p>Christ, have mercy. <strong>Christ, have mercy.</strong></p>
      <p>Lord, have mercy. <strong>Lord, have mercy.</strong></p>
      
      <p>or</p>

      <p>Kyrie, eleison. <strong>Kyrie, eleison.</strong></p>
      <p>Christe, eleison. <strong>Christe, eleison.</strong></p>
      <p>Kyrie, eleison. <strong>Kyrie, eleison.</strong></p>
    </div>

    <h2>The Collect</h2>

    <div class="collect">
      <p>Let us pray.</p>
      <pre>All pray in silence with the Priest for a moment. Then the Priest says the Collect prayer, after which the people acclaim:</pre>
      <blockquote>Amen.</blockquote>
    </div>

    <h1>The Liturgy of the Word</h1>

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

    <h2>Gospel Acclamation</h2>

    <div class="gospel-acclamation">
      <p v-html="readings?.gospelAcclamation.text"></p>
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

    <h2>The Homily</h2>

    <h2>The Nicene Creed</h2>

    <div class="creed">
      <blockquote>I believe in one God, the Father almighty,<br>
        maker of heaven and earth,
        of all things visible and invisible.<br>
        I believe in one Lord Jesus Christ,
        the Only Begotten Son of God,
        born of the Father before all ages. <br>
        God from God, Light from Light,<br> 
        true God from true God,<br>
        begotten, not made,
        consubstantial with the Father;
        through him all things were made.<br>
        For us men and for our salvation
        he came down from heaven,<br>
        <em>and by the Holy Spirit was incarnate<br>
        of the Virgin Mary, and became man.</em><br>
        For our sake he was crucified under 
        Pontius Pilate, he suffered death and
        was buried, and rose again on the third day
        in accordance with the Scriptures.<br>
        He ascended into heaven and is seated 
        at the right hand of the Father.
        He will come again in glory
        to judge the living and the dead
        and his kingdom will have no end.<br>
        I believe in the Holy Spirit, the Lord, the giver of life,
        who proceeds from the Father and the Son,
        who with the Father and the Son is adored and glorified,
        who has spoken through the prophets.<br>
        I believe in one, holy, catholic and apostolic Church.<br>
        I confess one baptism for the forgiveness of sins
        and I look forward to the resurrection of the dead
        and the life of the world to come.<br>
        Amen.
      </blockquote>
    </div>
    
    <p>or</p>

    <h2>The Apostles' Creed</h2>

    <div class="creed">
      <blockquote>I believe in God, the Father almighty, Creator of heaven and earth, and in Jesus Christ, his only Son, our Lord, 
        who was conceived by the Holy Spirit, born of the Virgin Mary, 
        suffered under Pontius Pilate, was crucified, died and was buried; he descended into hell; on the third day he rose again from the dead; 
        he ascended into heaven, and is seated at the right hand of God the Father almighty; from there he will come to judge the living and the dead. 
        I believe in the Holy Spirit, the holy catholic Church, the communion of saints, the forgiveness of sins, the resurrection of the body, and life everlasting. 
        Amen.</blockquote>
    </div>

    <h2>The Prayer of the Faithful</h2>

    <div class="prayer-of-faithful">
      <pre>After each intention there is a pause while the faithful pray, after which the minister adds:</pre>
      <p>Lord, in your mercy.</p>
      <blockquote>Hear our prayer.</blockquote>

      or

      <p>We pray to the Lord.</p>
      <blockquote>Lord, hear our prayer.</blockquote>
    </div>

    <h1>The Liturgy of the Eucharist</h1>

    <div class="song">
      <h5>Offertory hymn</h5>
      <h6>{{ songByIndex(3) }}</h6>
    </div>

    <div class="dialogue">
      <p>Pray, brothers and sisters, that my sacrifice and yours may be acceptable to God, the almighty Father.</p>
      <blockquote>May the Lord accept the sacrifice at your hands for the praise and glory of his name, for our good and the good of all his holy Church.</blockquote>
    </div>

    <div class="collect">
      <pre>Then the Priest says the Prayer over the Offerings, at the end of which the people acclaim:</pre>
      <blockquote>Amen.</blockquote>
    </div>

    <h2>The Eucharistic Prayer</h2>

    <div class="dialogue">
      <p>The Lord be with you.</p>
      <blockquote>And with your spirit.</blockquote>
      <p>Lift up your hearts.</p>
      <blockquote>We lift them up to the Lord.</blockquote>
      <p>Let us give thanks to the Lord our God.</p>
      <blockquote>It is right and just.</blockquote>
    </div>

    <h3>The Preface</h3>

    <div class="preface">
      <pre>The Priest concludes the Preface with the people, singing</pre>
      <blockquote>Holy, Holy, Holy Lord God of hosts.<br>
        Heaven and earth are full of your glory.<br>
        Hosanna in the highest.<br>
        Blessed is he who comes in the name of the Lord.<br>
        Hosanna in the highest.<br>

        or<br>

        Sanctus, Sanctus, Sanctus<br>
        Dominus Deus Sabaoth.<br>
        Pleni sunt cæli et terra gloria tua.<br>
        Hosanna in excelsis.<br>
        Benedictus qui venit in nomine Domini.<br>
        Hosanna in excelsis.
      </blockquote>
    </div>

    <div class="dialogue">
      <pre>After the words of Consecration the priest says:</pre>
      <p>The mystery of faith.</p>
      <blockquote>We proclaim your Death, O Lord, and profess your Resurrection until you come again.<br>
        Or:<br>
        When we eat this Bread and drink this Cup, we proclaim your Death, O Lord, until you come again.<br>
        Or:<br>
        Save us, Saviour of the world, for by your Cross and Resurrection you have set us free.</blockquote>
    </div>

    <div class="doxology">
      <pre>At the conclusion of the prayer the Priest takes the chalice and the paten with the host and, raising both, he says:</pre>
      <p>Through him, and with him, and in him,<br>
        O God, almighty Father,<br>
        in the unity of the Holy Spirit,<br>
        all glory and honour is yours,<br>
        for ever and ever.</p>
      <blockquote>Amen.</blockquote>
    </div>

    <h1>The Communion Rite</h1>

    <h2>The Lord's Prayer</h2>

    <div class="dialogue">
      <p>At the Saviour's command and formed by divine teaching, we dare to say:</p>
      <blockquote>Our Father, who art in heaven,<br>
        hallowed be thy name;<br>
        thy kingdom come,<br>
        thy will be done on earth as it is in heaven.<br>
        Give us this day our daily bread,<br>
        and forgive us our trespasses,<br>
        as we forgive those who trespass against us;<br>
        and lead us not into temptation,<br>
        but deliver us from evil.</blockquote>
    </div>

    <div class="dialogue">
      <p>Deliver us, Lord, we pray, from every evil…<br>
        and the coming of our Saviour, Jesus Christ.</p>
      <blockquote>For the kingdom, the power and the glory are yours now and for ever.</blockquote>
    </div>

    <div class="dialogue">
      <p>Lord Jesus Christ, who said to your Apostles…<br>
        Who live and reign for ever and ever.</p>
      <blockquote>Amen.</blockquote>
    </div>

    <div class="dialogue">
      <p>The peace of the Lord be with you always.</p>
      <blockquote>And with your spirit.</blockquote>
      <p>Let us offer each other the sign of peace.</p>
      <pre>All offer one another the customary sign of peace.</pre>
    </div>

    <h2>Breaking of the Bread</h2>

    <div class="lamb-of-god">
      <blockquote>Lamb of God, you take away the sins of the world, have mercy on us.<br>
        Lamb of God, you take away the sins of the world, have mercy on us.<br>
        Lamb of God, you take away the sins of the world, grant us peace.<br>

        or<br>

        Agnus Dei, qui tollis peccáta mundi: miserére nobis.<br>
        Agnus Dei, qui tollis peccáta mundi: miserére nobis.<br>
        Agnus Dei, qui tollis peccáta mundi: dona nobis pacem.      
      </blockquote>
    </div>

    <h2>Communion</h2>

    <div class="dialogue">
      <p>Behold the Lamb of God, behold him who takes away the sins of the world.<br>
        Blessed are those called to the supper of the Lamb.</p>
      <blockquote>Lord, I am not worthy that you should enter under my roof, but only say the word and my soul shall be healed.</blockquote>
    </div>

    <div class="dialogue">
      <pre>The communicants come forward in reverent procession, and make a preparatory act of reverence by bowing their head in honour of Christ's presence in the Sacrament.</pre>
      <p>The Body of Christ. <strong>Amen.</strong></p>
    </div>

    <div class="song">
      <h5>Communion hymn</h5>
      <h6>{{ songByIndex(6) }}</h6>
    </div>

    <div class="song">
      <h5>Thanksgiving hymn</h5>
      <h6>{{ songByIndex(7) }}</h6>
    </div>

    <h2>Prayer after Communion</h2>

    <div class="collect">
      <p>Let us pray.</p>
      <pre>All pray in silence. Then the Priest says the Prayer after Communion, at the end of which the people acclaim:</pre>
      <blockquote>Amen.</blockquote>
    </div>

    <div class="concluding-rites">
      <h1>The Concluding Rites</h1>
        <div class="dialogue">
        <p>The Lord be with you.</p>
        <blockquote>And with your spirit.</blockquote>
        <p>May almighty God bless you, the Father, and the Son, and the Holy Spirit.</p>
        <blockquote>Amen.</blockquote>
        <p>Go forth, the Mass is ended.</p>
        <blockquote>Thanks be to God.</blockquote>
      </div>
    </div>

    <div class="song">
      <h5>Recessional hymn</h5>
      <h6>{{ songByIndex(8) }}</h6>
    </div>

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
      const loadedReadings = await readingsStore.load(result.date);
      
      readings.value = loadedReadings;
      readings.value.psalm.text = transformResponsorial(loadedReadings.psalm.text);
      readings.value.gospelAcclamation.text = transformResponsorial(loadedReadings.gospelAcclamation.text);
    }
});
const gospelAuthor = computed(() => readings.value?.gospelReading.source.split(' ')[0]);
const songByIndex = computed(() => (index: number) => {
  const song = entries.value[index - 1]?.song?.embedded;
  return song ? `${song.songbookNumber ? song.songbookNumber + '. ' : ''}${song.title}` : '';
});

function transformResponsorial(html: string): string {
  const parser = new DOMParser().parseFromString(html, "text/html");
  const divs = parser.getElementsByTagName('div');
  
  if (divs.length) {
    const responseElement = document.createElement('blockquote');

    const firstChildIsItalic = (e: HTMLDivElement) => e.childNodes.item(0).nodeName === 'I';

    const responseIsItalic = firstChildIsItalic(divs[0]);
    const responseFirstLine = divs[0].innerText;
    responseElement.appendChild(document.createTextNode(responseFirstLine))
    
    if (responseIsItalic) {
      var i = 1;
      while(divs[i].childNodes.item(0).nodeName === 'I') {
        responseElement.appendChild(document.createElement('br'))
        responseElement.appendChild(document.createTextNode(divs[i].innerText))
        i++;
      } 
    }

    const elements: HTMLElement[] = [ responseElement ];
    var nextVerseElements: Node[] = [];

    for (var j = 0; j < divs.length; j++) {
      if (responseIsItalic ? firstChildIsItalic(divs[j]) : divs[j].innerText === responseFirstLine) {
        if (nextVerseElements.length) {
          const verseElement = document.createElement('p');
          verseElement.append(...nextVerseElements);
          elements.push(verseElement, responseElement)

          nextVerseElements = [];
        }
      } else {
        if (nextVerseElements.length) {
          nextVerseElements.push(document.createElement('br'));
        }
        nextVerseElements.push(document.createTextNode(divs[j].innerText));
      }
    } 

    return elements.map(e => e.outerHTML).join('\n');    
  }

  return html;
}

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
  margin: 1rem 0 0..5rem 0;
  padding-bottom: 0.5rem;
  text-transform: uppercase;
  text-align: center;
}

h2 {
  font-size: 1.2em;
  margin: 1rem 0 1rem 0;
  text-align: center;
}

.reading-header h2 {
  text-align: left; 
}

h3 {
  font-size: 1.2em;
  margin: 1rem 0 0.5rem 0;
  font-style: italic;
  text-align: center;
}

/* used as reading's source text */
h4 {
  margin: 1em 0 1em 0;
  font-style: italic;
  text-align: right;
}

/* used as hymn header */
h5 {
  font-size: 1em;
  font-style: italic;
  text-align: center;
}

/* used as hymn title */
h6 {
  font-size: 1em;
  font-weight: 700;
  text-align: center;
  text-transform: uppercase;
}

blockquote, :deep(blockquote) {
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
}

.prayer-of-faithful {
  margin: 1em 0;
}

.preface {
  margin: 1em 0;
}

.doxology {
  margin: 1em 0;
}

.lamb-of-god {
  margin: 1em 0;
}

</style>