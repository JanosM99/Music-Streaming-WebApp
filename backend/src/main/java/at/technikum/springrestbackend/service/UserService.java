package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.UserDetailsDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.exception.EntityNotFoundException;
import at.technikum.springrestbackend.model.Country;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.repository.PictureRepository;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.utils.PageableFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    private CountryService countryService;

    @Lazy
    @Autowired
    PasswordEncoder passwordEncoder;


    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users.
     */
    public List<User> findAll(){
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to be retrieved.
     * @return the user with the specified ID.
     * @throws EntityNotFoundException if no user with the given ID is found.
     */
    public User findById(Long id) throws EntityNotFoundException{
        var result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("User with id: " + id + " not found.");
        }
        return result.get();
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to be retrieved.
     * @return the user with the specified username.
     * @throws EntityNotFoundException if no user with the given username is found.
     */
    public User findByName(String username) throws EntityNotFoundException {
        var result = userRepository.findByName(username);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("User with id: " + username + " not found.");
        }
        return result.get();
    }

    /**
     * Saves a new user to the repository.
     *
     * @param userDto the data transfer object containing user details.
     * @return the saved user.
     * @throws Exception if an error occurs while saving the user.
     */
    public User save(UserDto userDto) throws Exception {
        try {
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setRole(userDto.getRole());
            user.setBirthDate(userDto.getBirthDate());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setFollowerCount(userDto.getFollowerCount());
            user.setActive(userDto.isActive());

            Country country = countryService.findByName(userDto.getCountry());
            if (country == null) {
                throw new EntityNotFoundException("Country not found");
            }
            user.setCountry(country);

            return userRepository.save(user);
        } catch (Exception e) {
            throw new Exception("Error saving user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user in the repository.
     *
     * @param userDto the data transfer object containing updated user details.
     * @return the updated user.
     * @throws Exception if an error occurs while updating the user.
     */
    public User updateUser(UserDto userDto) throws Exception {
        try {
            User existingUser = findById(userDto.getId());

            existingUser.setUsername(userDto.getUsername());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setRole(userDto.getRole());
            existingUser.setBirthDate(userDto.getBirthDate());
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            existingUser.setFollowerCount(userDto.getFollowerCount());
            existingUser.setActive(userDto.isActive());

            Country country = countryService.findByName(userDto.getCountry());
            if (country == null) {
                throw new EntityNotFoundException("Country not found");
            }
            existingUser.setCountry(country);

            return userRepository.save(existingUser);
        } catch (Exception e) {
            throw new Exception("Error updating user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user in the repository.
     *
     * @param userDto the data transfer object containing updated user details.
     * @return the updated user.
     * @throws Exception if an error occurs while updating the user.
     */
    public User updateUserDetails(UserDetailsDto userDto) throws Exception {
        try {
            User existingUser = findById(userDto.getId());

            existingUser.setUsername(userDto.getUsername());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setRole(userDto.getRole());
            existingUser.setBirthDate(userDto.getBirthDate());
            existingUser.setFollowerCount(userDto.getFollowerCount());
            existingUser.setActive(userDto.isActive());

            Country country = countryService.findByName(userDto.getCountry());
            if (country == null) {
                throw new EntityNotFoundException("Country not found");
            }
            existingUser.setCountry(country);

            return userRepository.save(existingUser);
        } catch (Exception e) {
            throw new Exception("Error updating user: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the Picture associated with the specified user ID.
     * @param userId the ID of the user whose Picture should be deleted
     */
    @Transactional
    public void deletePictureByUserId(Long userId) throws Exception {
        User user = findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }
        pictureRepository.deleteByUserId(userId);
    }

    /**
     * Deletes a user from the repository by their ID.
     *
     * @param id the ID of the user to be deleted.
     * @throws Exception if an error occurs while deleting the user.
     */
    public void deleteUser(Long id) throws Exception {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception("Error deleting user: " + e.getMessage(), e);
        }
    }

    /**
     * Changes the password for a given user
     * @param userId of the user
     * @param oldPassword the current password of the user that needs to be verified.
     * @param newPassword the new password to be set for the user.
     * @return true if the password was successfully changed,
     * false if the old password does not match the current password.
     * @throws EntityNotFoundException if the user cannot be found.
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword)
        throws EntityNotFoundException {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        var currentPassword = user.getPassword();
        if (!passwordEncoder.matches(oldPassword, currentPassword)) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /**
     * Returns all Users.
     *
     * @param page page to return
     * @param size size of page
     * @param active active
     * @param sort sort parameter
     *
     * @return all users.
     */
    public Page<UserDetailsDto> findAllUsers(int page, int size, boolean active, String sort) {
        Pageable pageable = PageableFactory.create(page, size, sort);
        if (active) {
            return userRepository.findAllByActivePageable(pageable);
        } else {
            return userRepository.findAllPageable(pageable);
        }
    }

    /**
     * Find all users with a filter.
     *
     * @param page A Page.
     * @param size The Page size.
     * @param active Whether user is active.
     * @param filter The attribute to filter for.
     * @param sort The attribute to sort for.
     * @return Page of PeopleUserTableEntryDto
     **/
    public Page<UserDetailsDto> findAllUsersWithFilter(int page, int size, boolean active, String filter,
                                                       String sort) {
        Pageable pageable = PageableFactory.create(page, size, sort);
        if (active) {
            return userRepository.findAllByActiveWithFilterPageable(pageable, filter);
        } else {
            return userRepository.findAllWithFilterPageable(pageable, filter);
        }
    }

    /**
     * Converts a User entity to a UserDto.
     *
     * @param user the user entity to be converted.
     * @return the converted UserDto.
     */
    public UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBirthDate(),
                user.getPassword(),
                user.getCountry().getName(),
                user.getFollowerCount(),
                user.isActive()
        );
    }

    /**
     * Converts a User object to a UserDetails
     * @param user User object
     * @return UserDetails
     */
    public UserDetailsDto convertToUserDetailsDto(User user) {
        return new UserDetailsDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBirthDate(),
                user.getCountry().getName(),
                user.getFollowerCount(),
                user.isActive()
        );
    }

}
