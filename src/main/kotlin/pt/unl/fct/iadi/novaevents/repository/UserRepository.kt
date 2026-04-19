package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import pt.unl.fct.iadi.novaevents.model.UserEntity
import java.util.Optional

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsernameIgnoreCase(username: String): Optional<UserEntity>
}
