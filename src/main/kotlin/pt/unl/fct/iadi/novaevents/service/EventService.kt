package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import pt.unl.fct.iadi.novaevents.repository.EventTypeRepository
import pt.unl.fct.iadi.novaevents.repository.UserRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDate
import java.util.NoSuchElementException

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val clubRepository: ClubRepository,
    private val eventTypeRepository: EventTypeRepository,
    private val userRepository: UserRepository,
    private val weatherService: WeatherService
) {

    fun findAllTypes(): List<EventType> = eventTypeRepository.findAll()

    fun findAll(
        typeName: String? = null,
        clubId: Long? = null,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): List<Event> = eventRepository.findFiltered(clubId, typeName, from, to)

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
        val type = resolveType(dto.type!!)

        val auth = SecurityContextHolder.getContext().authentication
        val username = auth?.name ?: throw IllegalStateException("No authenticated user")
        val owner = userRepository.findByUsernameIgnoreCase(username).orElseThrow {
            NoSuchElementException("User not found: $username")
        }

        validateLocationAndWeather(dto.location, club.name)

        val event = Event(
            club = club,
            name = name,
            date = dto.date!!,
            location = dto.location?.takeIf { it.isNotBlank() },
            type = type,
            description = dto.description?.takeIf { it.isNotBlank() },
            owner = owner
        )
        return eventRepository.save(event)
    }

    @Transactional
    fun update(id: Long, dto: EventFormDto): Event {
        val name = dto.name!!.trim()
        checkNameUnique(name, excludeId = id)

        val event = findById(id)
        val type = resolveType(dto.type!!)

        validateLocationAndWeather(dto.location, event.club.name)

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

    private fun resolveType(typeName: String): EventType =
        eventTypeRepository.findByNameIgnoreCase(typeName.trim()).orElseThrow {
            NoSuchElementException("Event type not found: $typeName")
        }

    private fun checkNameUnique(name: String, excludeId: Long?) {
        val duplicate = if (excludeId == null) {
            eventRepository.existsByNameIgnoreCase(name)
        } else {
            eventRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId)
        }
        if (duplicate) throw IllegalArgumentException("An event with this name already exists")
    }

    private fun validateLocationAndWeather(location: String?, clubName: String) {
        if (clubName == "Hiking & Outdoors Club") {
            if (location.isNullOrBlank()) {
                throw IllegalArgumentException("Location is required for outdoor events")
            }
            if (weatherService.isRaining(location) == true) {
                throw IllegalArgumentException("It is currently raining at \"$location\" — outdoor events cannot be created in bad weather")
            }
        }
    }
}
