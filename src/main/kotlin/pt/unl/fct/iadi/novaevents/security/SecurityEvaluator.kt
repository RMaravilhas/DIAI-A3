package pt.unl.fct.iadi.novaevents.security

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import java.util.NoSuchElementException

@Service("securityService")
class SecurityEvaluator(
    private val eventRepository: EventRepository
) {
    fun isEventOwner(username: String, eventId: Long): Boolean {
        val event = eventRepository.findById(eventId).orElseThrow {
            NoSuchElementException("Event not found: $eventId")
        }
        return event.owner?.username == username
    }
}
