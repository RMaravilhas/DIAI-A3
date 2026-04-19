package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pt.unl.fct.iadi.novaevents.model.Club

interface ClubRepository : JpaRepository<Club, Long> {

    /**
     * Fetches all clubs with their events and each event's type in a single query.
     * DISTINCT prevents duplicated clubs when a club has multiple events.
     * This is the N+1-safe method for the clubs list page.
     */
    @Query("SELECT DISTINCT c FROM Club c LEFT JOIN FETCH c.events e LEFT JOIN FETCH e.type LEFT JOIN FETCH e.owner")
    fun findAllWithEvents(): List<Club>
}
