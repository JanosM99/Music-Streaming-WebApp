package at.technikum.springrestbackend;

import at.technikum.springrestbackend.dto.login.LoginRequest;
import at.technikum.springrestbackend.dto.login.TokenResponse;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.security.jwt.JwtIssuer;
import at.technikum.springrestbackend.security.principal.UserPrincipal;
import at.technikum.springrestbackend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JwtIssuer jwtIssuer;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void attemptLogin_WhenUserIsInactive_ShouldThrowAccessDeniedException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("inactiveUser", "password");

        User inactiveUser = new User();
        inactiveUser.setActive(false);
        when(userRepository.findByName(loginRequest.getUsername())).thenReturn(Optional.of(inactiveUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authService.attemptLogin(loginRequest));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void attemptLogin_WhenUserNotFound_ShouldAuthenticateAndGenerateToken() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("validUser", "password");

        User activeUser = new User();
        activeUser.setActive(true);

        when(userRepository.findByName(loginRequest.getUsername())).thenReturn(Optional.of(activeUser));

        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(1L);
        when(principal.getUsername()).thenReturn("validUser");

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(principal.getAuthorities()).thenAnswer((Answer<Collection<? extends GrantedAuthority>>) invocation -> authorities);


        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String expectedToken = "mockedJwtToken";
        when(jwtIssuer.issue(principal.getId(), principal.getUsername(), List.of("ROLE_USER"))).thenReturn(expectedToken);

        // Act
        TokenResponse tokenResponse = authService.attemptLogin(loginRequest);

        // Assert
        assertNotNull(tokenResponse);
        assertEquals(expectedToken, tokenResponse.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtIssuer).issue(principal.getId(), principal.getUsername(), List.of("ROLE_USER"));
    }

    @Test
    void attemptLogin_WhenValidUser_ShouldAuthenticateAndReturnToken() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("validUser", "password");

        User activeUser = new User();
        activeUser.setActive(true);
        when(userRepository.findByName(loginRequest.getUsername())).thenReturn(Optional.of(activeUser));

        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(1L);
        when(principal.getUsername()).thenReturn("validUser");
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(principal.getAuthorities()).thenAnswer((Answer<Collection<? extends GrantedAuthority>>) invocation -> authorities);


        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String token = "mockedJwtToken";
        when(jwtIssuer.issue(anyLong(), anyString(), anyList())).thenReturn(token);

        // Act
        TokenResponse response = authService.attemptLogin(loginRequest);

        // Assert
        assertEquals(token, response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtIssuer).issue(principal.getId(), principal.getUsername(), List.of("ROLE_USER"));
    }
}