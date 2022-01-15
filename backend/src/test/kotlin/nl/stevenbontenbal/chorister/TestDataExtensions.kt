package nl.stevenbontenbal.chorister

import nl.stevenbontenbal.chorister.model.Song

fun Song.Companion.create(): Song {
    return Song(
        title = "My Song",
        composer = "John Doe",
        recordingUrl = "http://www.youtube.com/1234",
        scoreUrl = "http://www.hymnary.com/5678",
        songbook = null,
        songbookNumber = 23,
        choir =  null,
        addedBy = null,
    )
}
// TODO: https://betterprogramming.pub/test-data-creation-using-the-power-of-kotlin-dsl-9526a1fad05b