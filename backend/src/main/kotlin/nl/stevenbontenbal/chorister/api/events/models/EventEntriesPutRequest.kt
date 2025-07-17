package nl.stevenbontenbal.chorister.api.events.models

import nl.stevenbontenbal.chorister.domain.events.EventEntry

data class EventEntriesPutRequest(
    var entries: ArrayList<EventEntry>?
)