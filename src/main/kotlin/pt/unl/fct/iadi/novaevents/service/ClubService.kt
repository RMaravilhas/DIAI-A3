package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.repository.ClubEventCountView
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import java.util.NoSuchElementException

data class ClubWithEventCount(val club: Club, val eventCount: Long)

@Service
class ClubService(private val clubRepository: ClubRepository) {

    fun findAll(): List<Club> = clubRepository.findAll()

    fun findAllWithEventCount(): List<ClubWithEventCount> =
        clubRepository.findAllWithEventCount().map { view ->
            ClubWithEventCount(
                club = view.getClub(),
                eventCount = view.getEventCount()
            )
        }

    fun findById(id: Long): Club =
        clubRepository.findById(id).orElseThrow {
            NoSuchElementException("Club not found: $id")
        }
}
