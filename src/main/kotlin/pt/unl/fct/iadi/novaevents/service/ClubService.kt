package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import java.util.NoSuchElementException

@Service
class ClubService(private val clubRepository: ClubRepository) {

    /** Returns all clubs with their events eagerly fetched (single JOIN FETCH query). */
    fun findAll(): List<Club> = clubRepository.findAllWithEvents()

    fun findById(id: Long): Club =
        clubRepository.findById(id).orElseThrow {
            NoSuchElementException("Club not found: $id")
        }
}
