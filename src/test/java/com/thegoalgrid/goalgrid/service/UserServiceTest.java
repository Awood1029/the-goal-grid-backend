// File: test/java/com/thegoalgrid/goalgrid/service/UserServiceTest.java
package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        assertThat(userService.getAllUsers()).hasSize(2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_UserFound() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);
        assertThat(result.getUsername()).isEqualTo("user1");
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");
    }
}
