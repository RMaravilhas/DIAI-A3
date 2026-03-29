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

    /**
     * Used on the club detail page — events belonging to a single club, sorted by date.
     * JOIN FETCH avoids N+1 when rendering type badges.
     */
    @Query("""
        SELECT e FROM Event e
        JOIN FETCH e.type
        JOIN FETCH e.club
        WHERE e.club.id = :clubId
        ORDER BY e.date ASC
    """)
    fun findByClubIdOrderByDateAsc(@Param("clubId") clubId: Long): List<Event>

    /**
     * Filtered event listing pushed entirely to the database.
     * JOIN FETCH on e.type and e.club prevents N+1 when rendering the list.
     * Any null parameter is ignored (acts as "no filter for this dimension").
     * Type is matched by name (e.g. "WORKSHOP") — consistent with the form field.
     */
    @Query("""
        SELECT e FROM Event e
        JOIN FETCH e.type t
        JOIN FETCH e.club c
        WHERE (:clubId   IS NULL OR c.id   = :clubId)
          AND (:typeName IS NULL OR UPPER(t.name) = UPPER(:typeName))
          AND (:from     IS NULL OR e.date >= :from)
          AND (:to       IS NULL OR e.date <= :to)
        ORDER BY e.date ASC
    """)
    fun findFiltered(
        @Param("clubId")    clubId:    Long?,
        @Param("typeName")  typeName:  String?,
        @Param("from")      from:      LocalDate?,
        @Param("to")        to:        LocalDate?
    ): List<Event>
}
