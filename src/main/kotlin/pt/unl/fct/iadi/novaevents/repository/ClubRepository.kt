package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pt.unl.fct.iadi.novaevents.model.Club

interface ClubRepository : JpaRepository<Club, Long> {

    /**
     * Returns each club paired with the number of events it has.
     * A single GROUP BY query — avoids the N+1 problem on the clubs list.
     */
    @Query("SELECT c, COUNT(e) FROM Club c LEFT JOIN Event e ON e.club = c GROUP BY c")
    fun findAllWithEventCount(): List<Array<Any>>
}
