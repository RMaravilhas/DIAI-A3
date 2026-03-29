package pt.unl.fct.iadi.novaevents

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.ClubCategory
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import pt.unl.fct.iadi.novaevents.repository.EventTypeRepository
import java.time.LocalDate

@Component
class DataInitializer(
    private val eventTypeRepository: EventTypeRepository,
    private val clubRepository: ClubRepository,
    private val eventRepository: EventRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        // Guard: skip seeding if data already exists
        if (clubRepository.count() > 0) return

        // ── Event Types ──────────────────────────────────────────────────────
        val workshop    = eventTypeRepository.save(EventType(name = "WORKSHOP"))
        val talk        = eventTypeRepository.save(EventType(name = "TALK"))
        val competition = eventTypeRepository.save(EventType(name = "COMPETITION"))
        val social      = eventTypeRepository.save(EventType(name = "SOCIAL"))
        val meeting     = eventTypeRepository.save(EventType(name = "MEETING"))
        val other       = eventTypeRepository.save(EventType(name = "OTHER"))

        // ── Clubs ────────────────────────────────────────────────────────────
        val chess = clubRepository.save(Club(
            name = "Chess Club",
            description = "From beginner to advanced, our Chess Club welcomes all levels. We hold weekly sessions, " +
                    "analyse famous games, and compete in regional and national championships. " +
                    "Whether you want to sharpen your tactics or simply enjoy a friendly match, this is your place.",
            category = ClubCategory.ACADEMIC
        ))
        val robotics = clubRepository.save(Club(
            name = "Robotics Club",
            description = "The Robotics Club is the place to turn ideas into machines. Members work on projects " +
                    "ranging from line-following robots and robotic arms to full autonomous systems that compete in " +
                    "national and international championships. We cover electronics, embedded programming, mechanical " +
                    "design, and computer vision — no prior experience required, just curiosity and a willingness to " +
                    "get your hands dirty.",
            category = ClubCategory.TECHNOLOGY
        ))
        val photography = clubRepository.save(Club(
            name = "Photography Club",
            description = "We are a community of visual storytellers passionate about capturing the world through a lens. " +
                    "From street photography and portraiture to landscape and macro, our members explore every genre. " +
                    "We organise photo walks, workshops, and exhibitions throughout the year.",
            category = ClubCategory.ARTS
        ))
        val hiking = clubRepository.save(Club(
            name = "Hiking & Outdoors Club",
            description = "The Hiking & Outdoors Club organises regular excursions to Portugal's most scenic trails, " +
                    "from coastal paths and river valleys to mountain peaks. We welcome all fitness levels and prioritise " +
                    "safety, sustainability, and a love of the natural world.",
            category = ClubCategory.SPORTS
        ))
        val film = clubRepository.save(Club(
            name = "Film Society",
            description = "The Film Society screens a curated selection of classic, art-house, and contemporary films " +
                    "followed by open discussion. We explore cinema as an art form, examining direction, cinematography, " +
                    "narrative, and cultural context.",
            category = ClubCategory.CULTURAL
        ))

        // ── Events ───────────────────────────────────────────────────────────
        // Chess Club (3 events)
        eventRepository.save(Event(club = chess, name = "Beginner's Chess Workshop",
            date = LocalDate.of(2026, 3, 10), location = "Room A101", type = workshop,
            description = "An introductory session covering basic rules, piece movement, and opening principles."))
        eventRepository.save(Event(club = chess, name = "Spring Chess Tournament",
            date = LocalDate.of(2026, 4, 5), location = "Main Hall", type = competition,
            description = "Our annual spring tournament — all skill levels welcome. Swiss format, 5 rounds."))
        eventRepository.save(Event(club = chess, name = "Advanced Openings Talk",
            date = LocalDate.of(2026, 5, 20), location = "Room A101", type = talk,
            description = "Grandmaster-level discussion of the Sicilian Defence and King's Indian Attack."))

        // Robotics Club (4 events)
        eventRepository.save(Event(club = robotics, name = "Arduino Intro Workshop",
            date = LocalDate.of(2026, 3, 15), location = "Engineering Lab 2", type = workshop,
            description = "Hands-on introduction to Arduino: blinking LEDs, sensors, and servo motors."))
        eventRepository.save(Event(club = robotics, name = "RoboCup Preparation Meeting",
            date = LocalDate.of(2026, 3, 28), location = "Engineering Lab 1", type = meeting,
            description = "Team meeting to plan our strategy and task distribution for the upcoming RoboCup."))
        eventRepository.save(Event(club = robotics, name = "Sensor Integration Talk",
            date = LocalDate.of(2026, 4, 22), location = "Auditorium B", type = talk,
            description = "Deep dive into integrating ultrasonic, IR, and LiDAR sensors in autonomous robots."))
        eventRepository.save(Event(club = robotics, name = "Regional Robotics Competition",
            date = LocalDate.of(2026, 6, 1), location = "Sports Hall", type = competition,
            description = "Regional qualifier for the national robotics championship."))

        // Photography Club (3 events)
        eventRepository.save(Event(club = photography, name = "Night Photography Workshop",
            date = LocalDate.of(2026, 3, 22), location = "Campus Rooftop", type = workshop,
            description = "Learn long-exposure techniques: light painting, star trails, and cityscapes."))
        eventRepository.save(Event(club = photography, name = "Portrait Photography Talk",
            date = LocalDate.of(2026, 4, 14), location = "Arts Studio 3", type = talk,
            description = "Lighting setups, posing tips, and post-processing for stunning portraits."))
        eventRepository.save(Event(club = photography, name = "Photo Walk & Social",
            date = LocalDate.of(2026, 5, 9), location = "Main Entrance", type = social,
            description = "Casual photo walk around campus followed by a social gathering and photo review."))

        // Hiking & Outdoors Club (3 events)
        eventRepository.save(Event(club = hiking, name = "Serra da Arrábida Hike",
            date = LocalDate.of(2026, 3, 29), location = "Bus Stop Central", type = other,
            description = "A scenic hike through Arrábida Natural Park, approximately 12 km. Bring water and sunscreen."))
        eventRepository.save(Event(club = hiking, name = "Trail Safety Workshop",
            date = LocalDate.of(2026, 4, 8), location = "Room C205", type = workshop,
            description = "Essential first aid, navigation, and safety protocols for outdoor hiking."))
        eventRepository.save(Event(club = hiking, name = "Spring Camping Trip",
            date = LocalDate.of(2026, 5, 15), location = "Bus Stop Central", type = social,
            description = "Weekend camping trip to Sintra hills. All equipment provided. Limited spots."))

        // Film Society (2 events)
        eventRepository.save(Event(club = film, name = "Kubrick Retrospective Screening",
            date = LocalDate.of(2026, 3, 18), location = "Cinema Room", type = social,
            description = "Screening of '2001: A Space Odyssey' followed by a discussion on Kubrick's visual language."))
        eventRepository.save(Event(club = film, name = "Screenwriting Workshop",
            date = LocalDate.of(2026, 4, 30), location = "Arts Studio 1", type = workshop,
            description = "Practical workshop on three-act structure, character arcs, and dialogue writing."))
    }
}
