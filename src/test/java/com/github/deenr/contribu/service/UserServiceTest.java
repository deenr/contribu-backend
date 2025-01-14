package com.github.deenr.contribu.service;

import com.github.deenr.contribu.exception.EmailAlreadyInUseException;
import com.github.deenr.contribu.model.User;
import com.github.deenr.contribu.repository.UserRepository;
import com.github.deenr.contribu.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void register_Success() {
        String firstName = "John";
        String lastName = "Doe";
        String email = "email@google.com";
        String password = "password123";
        String hashedPassword = "mock_encryption";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(encoder.encode(password)).thenReturn(hashedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = userService.register(firstName, lastName, email, password);

        Assertions.assertNotNull(token);

        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(encoder).encode(password);
        // TODO VERIFY TOKEN GENERATION
        Mockito.verify(encoder).encode(password);
    }

    @Test
    public void register_EmailAlreadyExists_ThrowsException() {
        String firstName = "John";
        String lastName = "Doe";
        String email = "email@google.com";
        String password = "password123";

        User existingUser = new User();
        existingUser.setEmail(email);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        RuntimeException exception = Assertions.assertThrows(
                EmailAlreadyInUseException.class,
                () -> userService.register(firstName, lastName, email, password)
        );
        Assertions.assertEquals("Email is already in use.", exception.getMessage());

        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(encoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void login_Success() {
        String email = "email@google.com";
        String password = "password123";
        String hashedPassword = "mock_encryption";

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(encoder.matches(password, hashedPassword)).thenReturn(true);

        String token = userService.login(email, password);

        Assertions.assertNotNull(token);

        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(encoder).matches(password, hashedPassword);
        // TODO VERIFY TOKEN GENERATION
    }

    @Test
    public void authenticate_Failure_InvalidPassword() {
        String email = "email@google.com";
        String password = "password123";
        String hashedPassword = "mock_encryption";

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(encoder.matches(password, hashedPassword)).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.login(email, password)
        );
        Assertions.assertEquals("Invalid credentials", exception.getMessage());

        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(encoder).matches(password, hashedPassword);
    }
}