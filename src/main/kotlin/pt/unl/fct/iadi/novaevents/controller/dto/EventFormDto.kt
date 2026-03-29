package pt.unl.fct.iadi.novaevents.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class EventFormDto(
    @field:NotBlank(message = "Name is required")
    var name: String? = null,

    @field:NotNull(message = "Date is required")
    var date: LocalDate? = null,

    var location: String? = null,

    @field:NotBlank(message = "Event type is required")
    var type: String? = null,

    var description: String? = null
)
