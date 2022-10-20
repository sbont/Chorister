package nl.stevenbontenbal.chorister.model.entities

import java.io.Serializable
import javax.persistence.Column

import javax.persistence.Embeddable

@Embeddable
data class SetlistEntryId(
    @Column(name = "SETLIST_ID")
    var setlistId: Long? = null,
    @Column(name = "SONG_ID")
    var songId: Long? = null
): Serializable