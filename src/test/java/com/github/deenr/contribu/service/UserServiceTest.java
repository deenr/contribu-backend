package com.github.deenr.contribu.service;

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
        String email = "email@google.com";
        String password = "password123";
        String hashedPassword = "mock_encryption";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(encoder.encode(password)).thenReturn(hashedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.register(email, password);

        Assertions.assertEquals(email, user.getEmail());
        Assertions.assertEquals(hashedPassword, user.getPassword());

        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(encoder).encode(password);
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    public void register_EmailAlreadyExists_ThrowsException() {
        String email = "email@google.com";
        String password = "password123";

        User existingUser = new User();
        existingUser.setEmail(email);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(email, password)
        );
        Assertions.assertEquals("Email is already in use.", exception.getMessage());

        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(encoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void authenticate_Success() {
        String email = "email@google.com";
        String password = "password123";
        String hashedPassword = "mock_encryption";

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(encoder.matches(password, hashedPassword)).thenReturn(true);

        boolean authenticated = userService.authenticate(email, password);
        Assertions.assertTrue(authenticated);

        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(encoder).matches(password, hashedPassword);
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

        boolean authenticated = userService.authenticate(email, password);

        Assertions.assertFalse(authenticated);
        Mockito.verify(userRepository).findByEmail(email);
        Mockito.verify(encoder).matches(password, hashedPassword);
    }
}