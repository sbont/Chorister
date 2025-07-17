package nl.stevenbontenbal.chorister.domain.songs

import com.fasterxml.jackson.annotation.JsonValue

enum class Key(
    @JsonValue
    val literal: String
) {
    C("C"),
    DB("Db"),
    D("D"),
    EB("Eb"),
    E("E"),
    F("F"),
    GB("Gb"),
    G("G"),
    AB("Ab"),
    A("A"),
    BB("Bb"),
    B("B"),
}