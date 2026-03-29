package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.*

@Entity
@Table(name = "event_types")
class EventType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String = ""
)
