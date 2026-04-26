package pt.unl.fct.iadi.novaevents

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import pt.unl.fct.iadi.novaevents.model.UserEntity
import pt.unl.fct.iadi.novaevents.repository.UserRepository
import pt.unl.fct.iadi.novaevents.security.DomainUserDetailsService
import java.util.Optional
import org.junit.jupiter.api.Assertions.*

class TestDomainUserDetailsService {

    private val userRepository = org.mockito.Mockito.mock(UserRepository::class.java)
    private val userDetailsService = DomainUserDetailsService(userRepository)

    @Test
    fun `loadUserByUsername returns user successfully`() {
        val userEntity = UserEntity(username = "admin", passwordHash = "hash", roles = mutableListOf("ADMIN"))
        `when`(userRepository.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(userEntity))

        val userDetails = userDetailsService.loadUserByUsername("admin")
        assertNotNull(userDetails)
        assertEquals("admin", userDetails.username)
        assertEquals("hash", userDetails.password)
    }

    @Test
    fun `loadUserByUsername throws when not found`() {
        `when`(userRepository.findByUsernameIgnoreCase("unknown")).thenReturn(Optional.empty())

        assertThrows(UsernameNotFoundException::class.java) {
            userDetailsService.loadUserByUsername("unknown")
        }
    }
}
