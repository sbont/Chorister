package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*

@Entity
@Table(name = "SETLIST_ENTRY")
class SetlistEntry(
    @EmbeddedId
    var id: SetlistEntryId = SetlistEntryId(),
    @ManyToOne
    @MapsId("setlistId")
    var setlist: Setlist,
    @ManyToOne
    @MapsId("songId")
    var song: Song,
    var number: Int = setlist.entries.size + 1
) {
    companion object
}