package pt.unl.fct.iadi.novaevents

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.*
import pt.unl.fct.iadi.novaevents.repository.*
import pt.unl.fct.iadi.novaevents.service.EventService
import pt.unl.fct.iadi.novaevents.service.WeatherService
import java.time.LocalDate
import java.util.Optional

class TestEventService {

    private val eventRepository = mock(EventRepository::class.java)
    private val clubRepository = mock(ClubRepository::class.java)
    private val eventTypeRepository = mock(EventTypeRepository::class.java)
    private val userRepository = mock(UserRepository::class.java)
    private val weatherService = mock(WeatherService::class.java)

    private val eventService = EventService(
        eventRepository, clubRepository, eventTypeRepository, userRepository, weatherService
    )

    @BeforeEach
    fun setup() {
        SecurityContextHolder.getContext().authentication = 
            UsernamePasswordAuthenticationToken("user", "pass", emptyList())
    }

    @Test
    fun `create event successful`() {
        val dto = EventFormDto(name = "Test Event", date = LocalDate.now(), type = "Workshop", location = "Room A")
        val club = Club(id = 1, name = "Art Club")
        val owner = UserEntity(username = "user", passwordHash = "p")
        val type = EventType(name = "Workshop")

        `when`(eventRepository.existsByNameIgnoreCase("Test Event")).thenReturn(false)
        `when`(clubRepository.findById(1L)).thenReturn(Optional.of(club))
        `when`(eventTypeRepository.findByNameIgnoreCase("Workshop")).thenReturn(Optional.of(type))
        `when`(userRepository.findByUsernameIgnoreCase("user")).thenReturn(Optional.of(owner))
        `when`(eventRepository.save(any(Event::class.java))).thenAnswer { it.arguments[0] }

        val result = eventService.create(dto, 1L)

        assertEquals("Test Event", result.name)
        assertEquals("Room A", result.location)
    }

    @Test
    fun `create outdoor event fails if location is empty and club is Hiking`() {
        val dto = EventFormDto(name = "Hike", date = LocalDate.now(), type = "Workshop", location = "")
        val club = Club(id = 1, name = "Hiking & Outdoors Club")
        val owner = UserEntity(username = "user", passwordHash = "p")
        val type = EventType(name = "Workshop")

        `when`(eventRepository.existsByNameIgnoreCase("Hike")).thenReturn(false)
        `when`(clubRepository.findById(1L)).thenReturn(Optional.of(club))
        `when`(eventTypeRepository.findByNameIgnoreCase("Workshop")).thenReturn(Optional.of(type))
        `when`(userRepository.findByUsernameIgnoreCase("user")).thenReturn(Optional.of(owner))

        val err = assertThrows(IllegalArgumentException::class.java) {
            eventService.create(dto, 1L)
        }
        assertEquals("Location is required for outdoor events", err.message)
    }

    @Test
    fun `create outdoor event fails if raining`() {
        val dto = EventFormDto(name = "Hike", date = LocalDate.now(), type = "Workshop", location = "Mountain")
        val club = Club(id = 1, name = "Hiking & Outdoors Club")
        val owner = UserEntity(username = "user", passwordHash = "p")
        val type = EventType(name = "Workshop")

        `when`(eventRepository.existsByNameIgnoreCase("Hike")).thenReturn(false)
        `when`(clubRepository.findById(1L)).thenReturn(Optional.of(club))
        `when`(eventTypeRepository.findByNameIgnoreCase("Workshop")).thenReturn(Optional.of(type))
        `when`(userRepository.findByUsernameIgnoreCase("user")).thenReturn(Optional.of(owner))
        `when`(weatherService.isRaining("Mountain")).thenReturn(true)

        val err = assertThrows(IllegalArgumentException::class.java) {
            eventService.create(dto, 1L)
        }
        assertTrue(err.message!!.startsWith("It is currently raining at"))
    }
    
    @Test
    fun `update outdoor event checks weather`() {
        val dto = EventFormDto(name = "Hike Update", date = LocalDate.now(), type = "Workshop", location = "Mountain")
        val club = Club(id = 1, name = "Hiking & Outdoors Club")
        val event = Event(id = 2, club = club, name = "Hike", date = LocalDate.now(), type = EventType(name = "W"), owner = UserEntity())

        `when`(eventRepository.existsByNameIgnoreCaseAndIdNot("Hike Update", 2L)).thenReturn(false)
        `when`(eventRepository.findById(2L)).thenReturn(Optional.of(event))
        `when`(eventTypeRepository.findByNameIgnoreCase("Workshop")).thenReturn(Optional.of(EventType(name = "Workshop")))
        `when`(weatherService.isRaining("Mountain")).thenReturn(true)

        val err = assertThrows(IllegalArgumentException::class.java) {
            eventService.update(2L, dto)
        }
        assertTrue(err.message!!.startsWith("It is currently raining at"))
    }

    @Test
    fun `delete event successful`() {
        `when`(eventRepository.existsById(1L)).thenReturn(true)
        eventService.delete(1L)
        verify(eventRepository).deleteById(1L)
    }

    @Test
    fun `findAll events`() {
        `when`(eventRepository.findFiltered(null, null, null, null)).thenReturn(emptyList())
        val result = eventService.findAll()
        assertTrue(result.isEmpty())
    }
}
