package nl.stevenbontenbal.chorister.model.dto

import nl.stevenbontenbal.chorister.model.entities.EventEntry

data class EventEntriesPutRequest(
    var entries: ArrayList<EventEntry>?
)