package pt.unl.fct.iadi.novaevents.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.UserEntity
import pt.unl.fct.iadi.novaevents.repository.UserRepository

@Service
class DomainUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsManager {

    override fun loadUserByUsername(username: String): UserDetails {
        val userEntity = userRepository.findByUsernameIgnoreCase(username).orElseThrow {
            UsernameNotFoundException("User not found: $username")
        }
        return org.springframework.security.core.userdetails.User.builder()
            .username(userEntity.username)
            .password(userEntity.passwordHash)
            .authorities(*userEntity.roles.toTypedArray())
            .build()
    }

    override fun createUser(user: UserDetails) {
        val entity = UserEntity(
            username = user.username,
            passwordHash = user.password,
            roles = user.authorities.map { it.authority }.toMutableList()
        )
        userRepository.save(entity)
    }

    override fun updateUser(user: UserDetails) {
        val entity = userRepository.findByUsernameIgnoreCase(user.username).orElseThrow {
            UsernameNotFoundException("User not found: ${user.username}")
        }
        entity.passwordHash = user.password
        entity.roles = user.authorities.map { it.authority }.toMutableList()
        userRepository.save(entity)
    }

    override fun deleteUser(username: String) {
        val entity = userRepository.findByUsernameIgnoreCase(username).orElseThrow {
            UsernameNotFoundException("User not found: $username")
        }
        userRepository.delete(entity)
    }

    override fun changePassword(oldPassword: String, newPassword: String) {
        throw UnsupportedOperationException("Change password not implemented")
    }

    override fun userExists(username: String): Boolean {
        return userRepository.findByUsernameIgnoreCase(username).isPresent
    }
}
