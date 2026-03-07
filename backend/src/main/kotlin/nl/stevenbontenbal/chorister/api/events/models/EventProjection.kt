package nl.stevenbontenbal.chorister.api.events.models

import nl.stevenbontenbal.chorister.domain.events.Event
import org.springframework.data.rest.core.config.Projection
import java.time.LocalDate

@Projection(name = "defaultProjection", types = [Event::class])
interface EventProjection {
    var id: Long?
    var name: String
    var date: LocalDate?
    var entries: MutableList<EventEntryProjection>
}