package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.PagedResponseDto;
import at.technikum.springrestbackend.dto.PasswordChangeDto;
import at.technikum.springrestbackend.dto.UserDetailsDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.exception.EntityNotFoundException;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.security.principal.UserPrincipal;
import at.technikum.springrestbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;


    /**
     * Retrieves a paginated list of users with optional filtering.
     *
     * @param pageSize the number of users per page
     * @param page the page number to retrieve
     * @param sort the sorting criteria (default is "username,asc")
     * @param filter an optional filter string to filter users
     * @param active a flag to filter by active status (default is false)
     * @return a ResponseEntity containing a paged response with user details
     */
    @GetMapping("/get-all")
    public ResponseEntity<PagedResponseDto<UserDetailsDto>> getAllUsers(
            @RequestParam(name = "pageSize", defaultValue = "50", required = false) int pageSize,
            @RequestParam(name = "page", required = true) int page,
            @RequestParam(defaultValue = "username,asc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(name = "active", defaultValue = "false", required = false) boolean active) {


        Page<UserDetailsDto> userPage;
        if (filter == null) {
            // get all users - pageable
            userPage = userService.findAllUsers(page, pageSize, active, sort);
        } else {
            userPage = userService
                    .findAllUsersWithFilter(page, pageSize, active, filter, sort);
        }

        PagedResponseDto<UserDetailsDto> response = new PagedResponseDto<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return a ResponseEntity containing the user details
     * @throws EntityNotFoundException if the user is not found
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) throws EntityNotFoundException {
        User user = userService.findById(id);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    /**
     * Retrieves the currently authenticated user's details.
     *
     * @param userPrincipal the currently authenticated user's principal
     * @return a ResponseEntity containing the user's details
     * @throws EntityNotFoundException if the user is not found
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailsDto> getMyUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) throws EntityNotFoundException {

        User user = userService.findById(userPrincipal.getId());
        return ResponseEntity.ok(userService.convertToUserDetailsDto(user));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return a ResponseEntity containing the user details
     * @throws EntityNotFoundException if the user is not found
     */
    @GetMapping("/get-user-by-name/{username}")
    public ResponseEntity<UserDto> getUserByName(@PathVariable String username) throws  EntityNotFoundException {
        User user = userService.findByName(username);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    /**
     * Creates a new user.
     *
     * @param userDto the user details to create
     * @return a ResponseEntity containing the created user details
     * @throws Exception if an error occurs during user creation
     */
    @PostMapping("/create")
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) throws Exception {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.convertToDto(userService.save(userDto)));
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing user's information.
     *
     * @param userDto the user details to update
     * @return a ResponseEntity containing the updated user details
     */
    @PutMapping("/update")
    public ResponseEntity<UserDto> updateById(@RequestBody UserDto userDto) {
        try {
            User updatedUser = userService.updateUser(userDto);
            return ResponseEntity.ok(userService.convertToDto(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates the details of an existing user.
     *
     * @param userDto the user details to update
     * @return a ResponseEntity containing the updated user details
     */
    @PutMapping("/update-details")
    public ResponseEntity<UserDetailsDto> updateDetailsById(@RequestBody UserDetailsDto userDto) {
        try {
            User updatedUser = userService.updateUserDetails(userDto);
            return ResponseEntity.ok(userService.convertToUserDetailsDto(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Changes the password of the currently authenticated user.
     *
     * @param userPrincipal the currently authenticated user's principal
     * @param passwordChangeDto contains the old and new passwords
     * @return a ResponseEntity with success or error message
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PasswordChangeDto passwordChangeDto) {

        try {
            boolean success = userService.changePassword(userPrincipal.getId(),
                    passwordChangeDto.getOldPassword(),
                    passwordChangeDto.getNewPassword());
            if (success) {
                return ResponseEntity.ok(passwordChangeDto);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Old password is incorrect");
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

}
