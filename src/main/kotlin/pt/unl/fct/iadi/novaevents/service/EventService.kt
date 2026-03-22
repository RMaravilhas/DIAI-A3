package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import java.time.LocalDate
import java.util.NoSuchElementException
import java.util.concurrent.atomic.AtomicLong

@Service
class EventService {

    private val idCounter = AtomicLong(16L)

    private val events: MutableList<Event> = mutableListOf(
        Event(id = 1L,  clubId = 1L, name = "Beginner's Chess Workshop",    date = LocalDate.of(2026, 3, 10),  location = "Room A101",         type = EventType.WORKSHOP,     description = "An introductory session covering basic rules, piece movement, and opening principles."),
        Event(id = 2L,  clubId = 1L, name = "Spring Chess Tournament",       date = LocalDate.of(2026, 4, 5),   location = "Main Hall",          type = EventType.COMPETITION,  description = "Our annual spring tournament — all skill levels welcome. Swiss format, 5 rounds."),
        Event(id = 3L,  clubId = 1L, name = "Advanced Openings Talk",        date = LocalDate.of(2026, 5, 20),  location = "Room A101",          type = EventType.TALK,         description = "Grandmaster-level discussion of the Sicilian Defence and King's Indian Attack."),
        Event(id = 4L,  clubId = 2L, name = "Arduino Intro Workshop",        date = LocalDate.of(2026, 3, 15),  location = "Engineering Lab 2",  type = EventType.WORKSHOP,     description = "Hands-on introduction to Arduino: blinking LEDs, sensors, and servo motors."),
        Event(id = 5L,  clubId = 2L, name = "RoboCup Preparation Meeting",   date = LocalDate.of(2026, 3, 28),  location = "Engineering Lab 1",  type = EventType.MEETING,      description = "Team meeting to plan our strategy and task distribution for the upcoming RoboCup."),
        Event(id = 6L,  clubId = 2L, name = "Sensor Integration Talk",       date = LocalDate.of(2026, 4, 22),  location = "Auditorium B",       type = EventType.TALK,         description = "Deep dive into integrating ultrasonic, IR, and LiDAR sensors in autonomous robots."),
        Event(id = 7L,  clubId = 2L, name = "Regional Robotics Competition", date = LocalDate.of(2026, 6, 1),   location = "Sports Hall",        type = EventType.COMPETITION,  description = "Regional qualifier for the national robotics championship."),
        Event(id = 8L,  clubId = 3L, name = "Night Photography Workshop",    date = LocalDate.of(2026, 3, 22),  location = "Campus Rooftop",     type = EventType.WORKSHOP,     description = "Learn long-exposure techniques: light painting, star trails, and cityscapes."),
        Event(id = 9L,  clubId = 3L, name = "Portrait Photography Talk",     date = LocalDate.of(2026, 4, 14),  location = "Arts Studio 3",      type = EventType.TALK,         description = "Lighting setups, posing tips, and post-processing for stunning portraits."),
        Event(id = 10L, clubId = 3L, name = "Photo Walk & Social",           date = LocalDate.of(2026, 5, 9),   location = "Main Entrance",      type = EventType.SOCIAL,       description = "Casual photo walk around campus followed by a social gathering and photo review."),
        Event(id = 11L, clubId = 4L, name = "Serra da Arrábida Hike",        date = LocalDate.of(2026, 3, 29),  location = "Bus Stop Central",   type = EventType.OTHER,        description = "A scenic hike through Arrábida Natural Park, approximately 12 km. Bring water and sunscreen."),
        Event(id = 12L, clubId = 4L, name = "Trail Safety Workshop",         date = LocalDate.of(2026, 4, 8),   location = "Room C205",          type = EventType.WORKSHOP,     description = "Essential first aid, navigation, and safety protocols for outdoor hiking."),
        Event(id = 13L, clubId = 4L, name = "Spring Camping Trip",           date = LocalDate.of(2026, 5, 15),  location = "Bus Stop Central",   type = EventType.SOCIAL,       description = "Weekend camping trip to Sintra hills. All equipment provided. Limited spots."),
        Event(id = 14L, clubId = 5L, name = "Kubrick Retrospective Screening", date = LocalDate.of(2026, 3, 18), location = "Cinema Room",       type = EventType.SOCIAL,       description = "Screening of '2001: A Space Odyssey' followed by a discussion on Kubrick's visual language."),
        Event(id = 15L, clubId = 5L, name = "Screenwriting Workshop",        date = LocalDate.of(2026, 4, 30),  location = "Arts Studio 1",      type = EventType.WORKSHOP,     description = "Practical workshop on three-act structure, character arcs, and dialogue writing.")
    )

    fun findAll(
        type: EventType? = null,
        clubId: Long? = null,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): List<Event> = events.filter { event ->
        (type == null || event.type == type) &&
        (clubId == null || event.clubId == clubId) &&
        (from == null || !event.date.isBefore(from)) &&
        (to == null || !event.date.isAfter(to))
    }.sortedBy { it.date }

    fun findById(id: Long): Event =
        events.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Event not found: $id")

    fun findByClubId(clubId: Long): List<Event> =
        events.filter { it.clubId == clubId }.sortedBy { it.date }

    fun create(dto: EventFormDto, clubId: Long): Event {
        val name = dto.name!!
        val type = dto.type!!
        val date = dto.date!!
        checkNameUnique(name, excludeId = null)
        val event = Event(
            id = idCounter.getAndIncrement(),
            clubId = clubId,
            name = name,
            date = date,
            location = dto.location?.takeIf { it.isNotBlank() },
            type = type,
            description = dto.description?.takeIf { it.isNotBlank() }
        )
        events.add(event)
        return event
    }

    fun update(id: Long, dto: EventFormDto): Event {
        val name = dto.name!!
        val type = dto.type!!
        val date = dto.date!!
        val existing = findById(id)
        checkNameUnique(name, excludeId = id)
        val updated = existing.copy(
            name = name,
            date = date,
            location = dto.location?.takeIf { it.isNotBlank() },
            type = type,
            description = dto.description?.takeIf { it.isNotBlank() }
        )
        val index = events.indexOfFirst { it.id == id }
        events[index] = updated
        return updated
    }

    fun delete(id: Long) {
        val existed = events.removeIf { it.id == id }
        if (!existed) throw NoSuchElementException("Event not found: $id")
    }

    private fun checkNameUnique(name: String, excludeId: Long?) {
        val duplicate = events.any { event ->
            event.name.equals(name.trim(), ignoreCase = true) && event.id != excludeId
        }
        if (duplicate) throw IllegalArgumentException("An event with this name already exists")
    }
}
