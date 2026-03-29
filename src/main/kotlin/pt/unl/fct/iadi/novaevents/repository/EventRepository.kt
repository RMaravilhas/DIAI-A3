package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pt.unl.fct.iadi.novaevents.model.Event
import java.time.LocalDate

interface EventRepository : JpaRepository<Event, Long> {

    /** Used when creating: name must be globally unique (case-insensitive). */
    fun existsByNameIgnoreCase(name: String): Boolean

    /** Used when editing: same uniqueness check but excludes the event being edited. */
    fun existsByNameIgnoreCaseAndIdNot(name: String, id: Long): Boolean

    /** Used on the club detail page — events belonging to a single club, sorted by date. */
    fun findByClubIdOrderByDateAsc(clubId: Long): List<Event>

    /**
     * Filtered event listing pushed entirely to the database.
     * Any null parameter is ignored (acts as "no filter for this dimension").
     */
    @Query("""
        SELECT e FROM Event e
        WHERE (:clubId IS NULL OR e.club.id = :clubId)
          AND (:typeId  IS NULL OR e.type.id  = :typeId)
          AND (:from    IS NULL OR e.date     >= :from)
          AND (:to      IS NULL OR e.date     <= :to)
        ORDER BY e.date ASC
    """)
    fun findFiltered(
        @Param("clubId") clubId: Long?,
        @Param("typeId")  typeId:  Long?,
        @Param("from")    from:    LocalDate?,
        @Param("to")      to:      LocalDate?
    ): List<Event>
}
