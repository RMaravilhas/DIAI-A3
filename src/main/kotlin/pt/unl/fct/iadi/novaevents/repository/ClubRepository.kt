package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pt.unl.fct.iadi.novaevents.model.Club

/** Spring Data JPA interface projection — avoids Object[] casting issues. */
interface ClubEventCountView {
    fun getClub(): Club
    fun getEventCount(): Long
}

interface ClubRepository : JpaRepository<Club, Long> {

    /**
     * Returns each club with its event count in a single GROUP BY query.
     * Uses an interface projection to avoid fragile Array<Any> casting.
     */
    @Query("SELECT c AS club, COUNT(e) AS eventCount FROM Club c LEFT JOIN Event e ON e.club = c GROUP BY c")
    fun findAllWithEventCount(): List<ClubEventCountView>
}
