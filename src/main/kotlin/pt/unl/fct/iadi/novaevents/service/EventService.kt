package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import pt.unl.fct.iadi.novaevents.repository.EventTypeRepository
import java.time.LocalDate
import java.util.NoSuchElementException

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val clubRepository: ClubRepository,
    private val eventTypeRepository: EventTypeRepository
) {

    fun findAllTypes(): List<EventType> = eventTypeRepository.findAll()

    fun findAll(
        typeId: Long? = null,
        clubId: Long? = null,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): List<Event> = eventRepository.findFiltered(clubId, typeId, from, to)

    fun findById(id: Long): Event =
        eventRepository.findById(id).orElseThrow {
            NoSuchElementException("Event not found: $id")
        }

    fun findByClubId(clubId: Long): List<Event> =
        eventRepository.findByClubIdOrderByDateAsc(clubId)

    @Transactional
    fun create(dto: EventFormDto, clubId: Long): Event {
        val name = dto.name!!.trim()
        checkNameUnique(name, excludeId = null)

        val club = clubRepository.findById(clubId).orElseThrow {
            NoSuchElementException("Club not found: $clubId")
        }
        val type = eventTypeRepository.findById(dto.typeId!!).orElseThrow {
            NoSuchElementException("Event type not found: ${dto.typeId}")
        }

        val event = Event(
            club = club,
            name = name,
            date = dto.date!!,
            location = dto.location?.takeIf { it.isNotBlank() },
            type = type,
            description = dto.description?.takeIf { it.isNotBlank() }
        )
        return eventRepository.save(event)
    }

    @Transactional
    fun update(id: Long, dto: EventFormDto): Event {
        val name = dto.name!!.trim()
        checkNameUnique(name, excludeId = id)

        val event = findById(id)
        val type = eventTypeRepository.findById(dto.typeId!!).orElseThrow {
            NoSuchElementException("Event type not found: ${dto.typeId}")
        }

        event.name = name
        event.date = dto.date!!
        event.location = dto.location?.takeIf { it.isNotBlank() }
        event.type = type
        event.description = dto.description?.takeIf { it.isNotBlank() }

        return eventRepository.save(event)
    }

    @Transactional
    fun delete(id: Long) {
        if (!eventRepository.existsById(id)) throw NoSuchElementException("Event not found: $id")
        eventRepository.deleteById(id)
    }

    private fun checkNameUnique(name: String, excludeId: Long?) {
        val duplicate = if (excludeId == null) {
            eventRepository.existsByNameIgnoreCase(name)
        } else {
            eventRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId)
        }
        if (duplicate) throw IllegalArgumentException("An event with this name already exists")
    }
}
