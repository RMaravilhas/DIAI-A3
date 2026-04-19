package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var username: String = "",

    @Column(nullable = false)
    var passwordHash: String = "",

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    var roles: MutableList<String> = mutableListOf()
)
