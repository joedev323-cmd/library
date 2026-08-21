package com.example.libback.repository;

import com.example.libback.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest // Configures an in-memory database and transaction rollback
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager; // Safely manages test data persist operations

    @Test
    @DisplayName("Should successfully find an existing user by username")
    void shouldFindUserByUsername() {
        // Arrange - Setup a user using your exact model fields
        User user = new User();
        user.setName("Alice Admin");
        user.setUsername("alice_admin");
        user.setPassword("hashed_password_123");
        user.setRole("ADMIN");
        
        entityManager.persistAndFlush(user);

        // Act - Call the repository method
        Optional<User> foundUser = userRepository.findByUsername("alice_admin");

        // Assert - Verify the result matches our expectations
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Alice Admin");
        assertThat(foundUser.get().getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should return empty Optional when username does not exist")
    void shouldReturnEmptyWhenUsernameNotFound() {
        // Act
        Optional<User> foundUser = userRepository.findByUsername("non_existent_user");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should throw constraint violation when username is a duplicate")
    void shouldThrowExceptionOnDuplicateUsername() {
        // Arrange - Save the first user
        User user1 = new User();
        user1.setName("Alice Admin");
        user1.setUsername("alice_admin");
        user1.setPassword("pass1");
        user1.setRole("ADMIN");
        entityManager.persistAndFlush(user1);

        // Arrange - Create a second user with the same username
        User user2 = new User();
        user2.setName("Bob Admin");
        user2.setUsername("alice_admin"); // Duplicate username!
        user2.setPassword("pass2");
        user2.setRole("SUPER_ADMIN");

        // Act & Assert - Expect a DataIntegrityViolationException due to the unique constraint
        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user2);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should throw constraint violation when a nullable field is null")
    void shouldThrowExceptionWhenPasswordIsNull() {
        // Arrange - Create a user without a password (violating nullable = false)
        User user = new User();
        user.setName("Alice Admin");
        user.setUsername("alice_admin");
        user.setPassword(null); // Null password!
        user.setRole("ADMIN");

        // Act & Assert
        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}