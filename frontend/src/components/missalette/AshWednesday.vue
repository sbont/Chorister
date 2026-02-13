<template>
  <div class="mass-order">
    <h1>The Introductory Rites</h1>

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
      <p><i>And all pray in silence with the Priest for a moment. Then the Priest, with hands extended, says the Collect prayer, at the end of which the people acclaim:</i></p>
      <p><strong>Amen.</strong></p>
    </div>

    <h1>The Liturgy of the Word</h1>

    <h2>First Reading</h2>

    <div class="reading">
      <p>{{ firstReading }}</p>
    </div>

    <div class="dialogue">
      <p>The word of the Lord.</p>
      <p><strong>Thanks be to God.</strong></p>
    </div>

    <h2>Psalm</h2>

    <div class="psalm">
      <p>{{ psalm }}</p>
    </div>

    <h2>Second Reading</h2>

    <div class="reading">
      <p>{{ secondReading }}</p>
    </div>

    <div class="dialogue">
      <p>The word of the Lord.</p>
      <p><strong>Thanks be to God.</strong></p>
    </div>

    <h2>Gospel</h2>

    <div class="gospel-acclamation">
      <p>{{ gospelAcclamation }}</p>
      <p>{{ gospelAcclamationVerse }}</p>
      <p>{{ gospelAcclamation }}</p>
    </div>

    <div class="dialogue">
      <p>The Lord be with you.</p>
      <p><strong>And with your spirit.</strong></p>
      <p>A reading from the holy Gospel according to {{ gospelAuthor }}.</p>
      <p><strong>Glory to you, O Lord.</strong></p>
    </div>

    <div class="gospel-reading">
      <p>{{ gospelReading }}</p>
    </div>

    <div class="dialogue">
      <p>The Gospel of the Lord.</p>
      <p><strong>Praise to you, Lord Jesus Christ.</strong></p>
    </div>

    <h2>The Homily</h2>

    <h2>The Prayer of the Faithful</h2>

    <div class="prayer-of-faithful">
      <p><i>After each intention there is a pause while the faithful pray.</i></p>
      <p>Lord, in your mercy.</p>
      <p><strong>Hear our prayer.</strong></p>
    </div>

    <h1>The Liturgy of the Eucharist</h1>

    <div class="dialogue">
      <p>Pray, brothers and sisters, that my sacrifice and yours may be acceptable to God, the almighty Father.</p>
      <p><strong>May the Lord accept the sacrifice at your hands for the praise and glory of his name, for our good and the good of all his holy Church.</strong></p>
    </div>

    <div class="collect">
      <p><i>Then the Priest says the Prayer over the Offerings, at the end of which the people acclaim:</i></p>
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
      <p><i>The Priest concludes the Preface with the people, singing</i></p>
      <p><strong>Holy, Holy, Holy Lord God of hosts.<br>
        Heaven and earth are full of your glory.<br>
        Hosanna in the highest.<br>
        Blessed is he who comes in the name of the Lord.<br>
        Hosanna in the highest.</strong></p>
    </div>

    <div class="dialogue">
      <p><i>After the words of Consecration the priest says:</i></p>
      <p>The mystery of faith.</p>
      <p><strong>We proclaim your Death, O Lord, and profess your Resurrection until you come again.<br>
        Or:<br>
        When we eat this Bread and drink this Cup, we proclaim your Death, O Lord, until you come again.<br>
        Or:<br>
        Save us, Saviour of the world, for by your Cross and Resurrection you have set us free.</strong></p>
    </div>

    <div class="doxology">
      <p><i>At the conclusion of the prayer the Priest takes the chalice and the paten with the host and, raising both, he says:</i></p>
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
      <p><strong>Lamb of God, you take away the sins of the world, have mercy on us.<br>
        Lamb of God, you take away the sins of the world, have mercy on us.<br>
        Lamb of God, you take away the sins of the world, grant us peace.</strong></p>
    </div>

    <h2>Communion</h2>

    <div class="dialogue">
      <p>Behold the Lamb of God, behold him who takes away the sins of the world.<br>
        Blessed are those called to the supper of the Lamb.</p>
      <p><strong>Lord, I am not worthy that you should enter under my roof, but only say the word and my soul shall be healed.</strong></p>
    </div>

    <div class="instruction">
      <p>The communicants come forward in reverent procession, and make a preparatory act of reverence by bowing their head in honour of Christ's presence in the Sacrament.</p>
    </div>

    <div class="dialogue">
      <p>The Body of Christ.</p>
      <p><strong>Amen.</strong></p>
    </div>

    <h2>Prayer after Communion</h2>

    <div class="collect">
      <p>Let us pray.</p>
      <p><i>All pray in silence. Then the Priest says the Prayer after Communion, at the end of which the people acclaim:</i></p>
      <p><strong>Amen.</strong></p>
    </div>
  </div>
</template>

<style scoped>
.mass-order {
  font-family: 'Times New Roman', serif;
  line-height: 1.6;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: #8B0000;
  font-size: 1.8em;
  margin: 2em 0 1em 0;
  border-bottom: 2px solid #8B0000;
  padding-bottom: 0.5em;
}

h2 {
  color: #A0522D;
  font-size: 1.4em;
  margin: 1.5em 0 1em 0;
}

h3 {
  color: #CD853F;
  font-size: 1.2em;
  margin: 1em 0 0.5em 0;
  font-style: italic;
}

.dialogue {
  margin: 1em 0;
  padding-left: 20px;
}

.dialogue p {
  margin: 0.5em 0;
}

.collect {
  margin: 1em 0;
  font-style: italic;
}

.reading, .psalm, .gospel-reading {
  margin: 1em 0;
  padding: 15px;
  background-color: #F5F5F5;
  border-left: 4px solid #8B0000;
}

.gospel-acclamation {
  margin: 1em 0;
  text-align: center;
  font-weight: bold;
}

.preface {
  margin: 1em 0;
  text-align: center;
  font-weight: bold;
  color: #8B0000;
}

.doxology {
  margin: 1em 0;
  text-align: center;
  font-style: italic;
}

.lamb-of-god {
  margin: 1em 0;
  text-align: center;
}

.instruction {
  margin: 1em 0;
  font-style: italic;
  color: #696969;
  font-size: 0.9em;
}

strong {
  color: #8B0000;
}
</style>