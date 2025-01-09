package at.technikum.springrestbackend;

import at.technikum.springrestbackend.dto.UserDetailsDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.exception.EntityNotFoundException;
import at.technikum.springrestbackend.model.Country;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.repository.PictureRepository;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.service.CountryService;
import at.technikum.springrestbackend.service.UserService;
import at.technikum.springrestbackend.utils.PageableFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PictureRepository pictureRepository;

    @Mock
    private CountryService countryService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        // Arrange
        List<User> userList = List.of(new User());
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertEquals(userList.size(), result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_UserExists() throws EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findById(userId);

        // Assert
        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void findByName_UserExists() throws EntityNotFoundException {
        // Arrange
        String username = "user1";
        User user = new User();
        when(userRepository.findByName(username)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByName(username);

        // Assert
        assertEquals(user, result);
        verify(userRepository, times(1)).findByName(username);
    }

    @Test
    void findByName_UserNotFound() {
        // Arrange
        String username = "user1";
        when(userRepository.findByName(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findByName(username));
    }

    @Test
    void save_UserSuccessfullySaved() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("username");
        userDto.setCountry("Austria");

        User user = new User();
        when(countryService.findByName("Austria")).thenReturn(new Country());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        User result = userService.save(userDto);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void save_CountryNotFound() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setCountry("UnknownCountry");

        when(countryService.findByName("UnknownCountry")).thenReturn(null);

        // Act & Assert
        assertThrows(Exception.class, () -> userService.save(userDto));
    }

    @Test
    void updateUser_UserSuccessfullyUpdated() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setCountry("Austria");

        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(countryService.findByName("Austria")).thenReturn(new Country());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        User result = userService.updateUser(userDto);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserDetails_UserSuccessfullyUpdated() throws Exception {
        // Arrange
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setId(1L);
        userDetailsDto.setCountry("Austria");

        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(countryService.findByName("Austria")).thenReturn(new Country());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.updateUserDetails(userDetailsDto);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deletePictureByUserId() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deletePictureByUserId(userId);

        // Assert
        verify(pictureRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void deleteUser() throws Exception {
        // Arrange
        Long userId = 1L;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void changePassword_SuccessfulPasswordChange() throws EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setPassword("encodedOldPassword");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        boolean result = userService.changePassword(userId, "oldPassword", "newPassword");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePassword_InvalidOldPassword() throws EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setPassword("encodedOldPassword");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

        // Act
        boolean result = userService.changePassword(userId, "wrongOldPassword", "newPassword");

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(user);
    }

    @Test
    void findAllUsers() {
        Pageable pageable = PageableFactory.create(0, 10, "sort"); // Create a real Pageable object
        UserDetailsDto userDetailsDto = new UserDetailsDto(
                1L,
                "username",
                "email@example.com",
                "ROLE_USER",
                LocalDate.of(1990, 1, 1),
                "Austria",
                100,
                true
        );

        List<UserDetailsDto> userDetailsList = List.of(userDetailsDto); // Wrap it in a list
        Page<UserDetailsDto> userDetailsPage = new PageImpl<>(userDetailsList, pageable, userDetailsList.size()); // Create a real Page

        when(userRepository.findAllPageable(pageable)).thenReturn(userDetailsPage);

        Page<UserDetailsDto> result = userService.findAllUsers(0, 10, false, "sort");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userDetailsDto.getUsername(), result.getContent().get(0).getUsername());
        verify(userRepository, times(1)).findAllPageable(pageable);
    }

    @Test
    void findAllUsersWithFilter() {
        Pageable pageable = PageableFactory.create(0, 10, "sort"); // Create a real Pageable object
        UserDetailsDto userDetailsDto = new UserDetailsDto(
                1L,
                "username",
                "email@example.com",
                "ROLE_USER",
                LocalDate.of(1990, 1, 1),
                "Austria",
                100,
                true
        );

        List<UserDetailsDto> userDetailsList = List.of(userDetailsDto);
        Page<UserDetailsDto> userDetailsPage = new PageImpl<>(userDetailsList, pageable, userDetailsList.size());

        when(userRepository.findAllWithFilterPageable(pageable, "filter")).thenReturn(userDetailsPage);

        Page<UserDetailsDto> result = userService.findAllUsersWithFilter(0, 10, false, "filter", "sort");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userDetailsDto.getUsername(), result.getContent().get(0).getUsername());
        verify(userRepository, times(1)).findAllWithFilterPageable(pageable, "filter");
    }
}
