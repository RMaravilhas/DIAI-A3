package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "events")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    var club: Club = Club(),

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var date: LocalDate = LocalDate.now(),

    @Column
    var location: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    var type: EventType = EventType(),

    @Column(length = 2000)
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: UserEntity? = null
)
