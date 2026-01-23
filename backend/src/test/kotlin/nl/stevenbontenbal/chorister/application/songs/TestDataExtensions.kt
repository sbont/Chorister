package nl.stevenbontenbal.chorister.application.songs

import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.songs.CategoryType
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.domain.users.Choir

fun Song.Companion.create(choir: Choir): Song {
    return Song(
        title = "My Song",
        composer = "John Doe",
        recordingUrl = "http://www.youtube.com/1234",
        scoreUrl = "http://www.hymnary.com/5678",
        songbook = null,
        songbookNumber = 23,
        choir = choir,
        text = ""
    )
}

fun Category.Companion.create(choir: Choir, categoryType: CategoryType): Category {
    return Category(
        name = "Christmas",
        choir = choir,
        categoryType = categoryType
    )
}

fun CategoryType.Companion.create(choir: Choir): CategoryType {
    return CategoryType(
        name = "Season",
        choir = choir
    )
}
