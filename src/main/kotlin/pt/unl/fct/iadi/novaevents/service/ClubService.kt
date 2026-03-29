package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import java.util.NoSuchElementException

data class ClubWithEventCount(val club: Club, val eventCount: Long)

@Service
class ClubService(
    private val clubRepository: ClubRepository,
    private val eventRepository: EventRepository
) {

    fun findAll(): List<Club> = clubRepository.findAll()

    fun findAllWithEventCount(): List<ClubWithEventCount> =
        clubRepository.findAll().map { club ->
            ClubWithEventCount(club, eventRepository.countByClubId(club.id))
        }

    fun findById(id: Long): Club =
        clubRepository.findById(id).orElseThrow {
            NoSuchElementException("Club not found: $id")
        }
}
