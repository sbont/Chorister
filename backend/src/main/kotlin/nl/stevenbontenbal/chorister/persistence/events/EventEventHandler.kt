package nl.stevenbontenbal.chorister.persistence.events

import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.rites.ElementText
import nl.stevenbontenbal.chorister.domain.rites.IElementTextRepository
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component
import kotlin.collections.mapValues

@Component
@RepositoryEventHandler(Event::class)
class EventEventHandler(
    private val elementTextRepository: IElementTextRepository,
    private val userService: UserService
) {
    @HandleBeforeCreate
    fun handleEventCreate(event: Event) {
        event.linkChoir(userService)

        applyOrderOfService(event);
    }

    fun applyOrderOfService(event: Event) {
        val translationId = event.choir?.liturgicalBookTranslation?.id
        val oosId = event.orderOfService?.id
        if (translationId == null || oosId == null)
            return

        val elementTexts = elementTextRepository.findByOrderOfServiceAndTranslation(oosId, translationId)
        val eventEntries = event.orderOfService?.elements?.flatMap { element ->
            val elementEntry = EventEntry(event = event, orderOfServiceElement = element, label = element.name)
            val elementTextsBySequence = elementTexts
                .filter { text -> text.element == element }
                .sortedBy { it.sequence }
                .groupBy { it.sequence }
                .mapValues { it.value.first() }

            var nextIndex = 1
            val textsInFlow = mutableListOf<ElementText>()
            while (nextIndex > 0 && elementTextsBySequence.containsKey(nextIndex)) {
                val next = elementTextsBySequence[nextIndex]
                next?.let { textsInFlow.add(it) }
                nextIndex = next?.skipToStep ?: (nextIndex + 1)
            }
            val textEntries = textsInFlow.map { elementText -> EventEntry(event = event, orderOfServiceElement = element, elementText = elementText, label = elementText.shortDescription) }
            listOf(elementEntry) + textEntries
        }
        eventEntries?.forEachIndexed { index, entry -> entry.sequence = index + 1 }
        event.entries = (eventEntries?.toMutableList() ?: mutableListOf())
    }

}