package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.ClubCategory
import java.util.NoSuchElementException

@Service
class ClubService {

    private val clubs: List<Club> = listOf(
        Club(
            id = 1L,
            name = "Chess Club",
            description = "From beginner to advanced, our Chess Club welcomes all levels. We hold weekly sessions, " +
                    "analyse famous games, and compete in regional and national championships. " +
                    "Whether you want to sharpen your tactics or simply enjoy a friendly match, this is your place.",
            category = ClubCategory.ACADEMIC
        ),
        Club(
            id = 2L,
            name = "Robotics Club",
            description = "The Robotics Club is the place to turn ideas into machines. Members work on projects " +
                    "ranging from line-following robots and robotic arms to full autonomous systems that compete in " +
                    "national and international championships. We cover electronics, embedded programming, mechanical " +
                    "design, and computer vision — no prior experience required, just curiosity and a willingness to " +
                    "get your hands dirty.",
            category = ClubCategory.TECHNOLOGY
        ),
        Club(
            id = 3L,
            name = "Photography Club",
            description = "We are a community of visual storytellers passionate about capturing the world through a lens. " +
                    "From street photography and portraiture to landscape and macro, our members explore every genre. " +
                    "We organise photo walks, workshops, and exhibitions throughout the year.",
            category = ClubCategory.ARTS
        ),
        Club(
            id = 4L,
            name = "Hiking & Outdoors Club",
            description = "The Hiking & Outdoors Club organises regular excursions to Portugal's most scenic trails, " +
                    "from coastal paths and river valleys to mountain peaks. We welcome all fitness levels and prioritise " +
                    "safety, sustainability, and a love of the natural world.",
            category = ClubCategory.SPORTS
        ),
        Club(
            id = 5L,
            name = "Film Society",
            description = "The Film Society screens a curated selection of classic, art-house, and contemporary films " +
                    "followed by open discussion. We explore cinema as an art form, examining direction, cinematography, " +
                    "narrative, and cultural context.",
            category = ClubCategory.CULTURAL
        )
    )

    fun findAll(): List<Club> = clubs

    fun findById(id: Long): Club =
        clubs.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Club not found: $id")
}
