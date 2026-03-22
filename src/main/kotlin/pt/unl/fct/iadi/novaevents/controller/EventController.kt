package pt.unl.fct.iadi.novaevents.controller

import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.service.ClubService
import pt.unl.fct.iadi.novaevents.service.EventService
import java.time.LocalDate

@Controller
class EventController(
    private val eventService: EventService,
    private val clubService: ClubService
) {


    @GetMapping("/events")
    fun listEvents(
        @RequestParam(required = false) type: EventType?,
        @RequestParam(required = false) clubId: Long?,
        @RequestParam(required = false) from: LocalDate?,
        @RequestParam(required = false) to: LocalDate?,
        model: Model
    ): String {
        val events = eventService.findAll(type, clubId, from, to)
        val clubs = clubService.findAll()
        model.addAttribute("events", events)
        model.addAttribute("clubs", clubs)
        model.addAttribute("allTypes", EventType.values())
        model.addAttribute("selectedType", type)
        model.addAttribute("selectedClubId", clubId)
        model.addAttribute("selectedFrom", from)
        model.addAttribute("selectedTo", to)

        val clubMap = clubs.associateBy { it.id }
        model.addAttribute("clubMap", clubMap)

        return "events/list"
    }


    @GetMapping("/clubs/{clubId}/events/{eventId}")
    fun eventDetail(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        model: Model
    ): String {
        val club = clubService.findById(clubId)
        val event = eventService.findById(eventId)
        model.addAttribute("club", club)
        model.addAttribute("event", event)
        return "events/detail"
    }


    @GetMapping("/clubs/{clubId}/events/new")
    fun showCreateForm(@PathVariable clubId: Long, model: Model): String {
        val club = clubService.findById(clubId)
        model.addAttribute("club", club)
        if (!model.containsAttribute("eventForm")) {
            model.addAttribute("eventForm", EventFormDto())
        }
        model.addAttribute("allTypes", EventType.values())
        model.addAttribute("formAction", "/clubs/$clubId/events")
        model.addAttribute("httpMethod", "POST")
        model.addAttribute("pageTitle", "New Event")
        return "events/form"
    }

    @PostMapping("/clubs/{clubId}/events")
    fun createEvent(
        @PathVariable clubId: Long,
        @Valid @ModelAttribute("eventForm") dto: EventFormDto,
        bindingResult: BindingResult,
        redirectAttrs: RedirectAttributes,
        model: Model
    ): String {
        clubService.findById(clubId)

        if (!bindingResult.hasErrors()) {
            try {
                val event = eventService.create(dto, clubId)
                return "redirect:/clubs/$clubId/events/${event.id}"
            } catch (e: IllegalArgumentException) {
                bindingResult.rejectValue("name", "duplicate", e.message ?: "Duplicate name")
            }
        }

        val club = clubService.findById(clubId)
        model.addAttribute("club", club)
        model.addAttribute("allTypes", EventType.values())
        model.addAttribute("formAction", "/clubs/$clubId/events")
        model.addAttribute("httpMethod", "POST")
        model.addAttribute("pageTitle", "New Event")
        return "events/form"
    }


    @GetMapping("/clubs/{clubId}/events/{eventId}/edit")
    fun showEditForm(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        model: Model
    ): String {
        val club = clubService.findById(clubId)
        val event = eventService.findById(eventId)
        if (!model.containsAttribute("eventForm")) {
            model.addAttribute("eventForm", EventFormDto(
                name = event.name,
                date = event.date,
                location = event.location,
                type = event.type,
                description = event.description
            ))
        }
        model.addAttribute("club", club)
        model.addAttribute("event", event)
        model.addAttribute("allTypes", EventType.values())
        model.addAttribute("formAction", "/clubs/$clubId/events/$eventId")
        model.addAttribute("httpMethod", "PUT")
        model.addAttribute("pageTitle", "Edit Event")
        return "events/form"
    }

    @PutMapping("/clubs/{clubId}/events/{eventId}")
    fun updateEvent(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        @Valid @ModelAttribute("eventForm") dto: EventFormDto,
        bindingResult: BindingResult,
        redirectAttrs: RedirectAttributes,
        model: Model
    ): String {
        clubService.findById(clubId)
        eventService.findById(eventId)

        if (!bindingResult.hasErrors()) {
            try {
                eventService.update(eventId, dto)
                return "redirect:/clubs/$clubId/events/$eventId"
            } catch (e: IllegalArgumentException) {
                bindingResult.rejectValue("name", "duplicate", e.message ?: "Duplicate name")
            }
        }

        val club = clubService.findById(clubId)
        val event = eventService.findById(eventId)
        model.addAttribute("club", club)
        model.addAttribute("event", event)
        model.addAttribute("allTypes", EventType.values())
        model.addAttribute("formAction", "/clubs/$clubId/events/$eventId")
        model.addAttribute("httpMethod", "PUT")
        model.addAttribute("pageTitle", "Edit Event")
        return "events/form"
    }


    @GetMapping("/clubs/{clubId}/events/{eventId}/delete")
    fun showDeleteConfirmation(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        model: Model
    ): String {
        val club = clubService.findById(clubId)
        val event = eventService.findById(eventId)
        model.addAttribute("club", club)
        model.addAttribute("event", event)
        return "events/delete"
    }

    @DeleteMapping("/clubs/{clubId}/events/{eventId}")
    fun deleteEvent(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long
    ): String {
        clubService.findById(clubId)
        eventService.delete(eventId)
        return "redirect:/clubs/$clubId"
    }
}
