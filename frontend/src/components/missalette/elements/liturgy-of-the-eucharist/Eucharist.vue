<script setup lang="ts">
import Communion from "./Communion.vue";
</script>

<template>
  <div class="dialogue">
    <p>
      Pray, brothers and sisters, that my sacrifice and yours may be acceptable to God,
      the almighty Father.
    </p>
    <blockquote>
      May the Lord accept the sacrifice at your hands for the praise and glory of his
      name, for our good and the good of all his holy Church.
    </blockquote>
  </div>

  <div class="collect">
    <pre>
Then the Priest says the Prayer over the Offerings, at the end of which the people acclaim:</pre
    >
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
    <blockquote>
      Holy, Holy, Holy Lord God of hosts.<br />
      Heaven and earth are full of your glory.<br />
      Hosanna in the highest.<br />
      Blessed is he who comes in the name of the Lord.<br />
      Hosanna in the highest.<br />

      or<br />

      Sanctus, Sanctus, Sanctus<br />
      Dominus Deus Sabaoth.<br />
      Pleni sunt cæli et terra gloria tua.<br />
      Hosanna in excelsis.<br />
      Benedictus qui venit in nomine Domini.<br />
      Hosanna in excelsis.
    </blockquote>
  </div>

  <div class="dialogue">
    <pre>After the words of Consecration the priest says:</pre>
    <p>The mystery of faith.</p>
    <blockquote>
      We proclaim your Death, O Lord, and profess your Resurrection until you come
      again.<br />
      Or:<br />
      When we eat this Bread and drink this Cup, we proclaim your Death, O Lord, until you
      come again.<br />
      Or:<br />
      Save us, Saviour of the world, for by your Cross and Resurrection you have set us
      free.
    </blockquote>
  </div>

  <div class="doxology">
    <pre>
At the conclusion of the prayer the Priest takes the chalice and the paten with the host and, raising both, he says:</pre
    >
    <p>
      Through him, and with him, and in him,<br />
      O God, almighty Father,<br />
      in the unity of the Holy Spirit,<br />
      all glory and honour is yours,<br />
      for ever and ever.
    </p>
    <blockquote>Amen.</blockquote>
  </div>

  <Communion />
</template>

<style lang="scss" scoped>
@import "../../missalette.scss";
</style>
