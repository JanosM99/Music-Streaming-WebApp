package at.technikum.springrestbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDetailsDto {

    private Long id;

    private String username;

    private String email;

    private String role;

    private LocalDate birthDate;

    private String country;

    private int followerCount;

    private boolean active;

    /**
     * Constructor
     */
    public UserDetailsDto(
            Long id,
            String username,
            String email,
            String role,
            LocalDate birthDate,
            String country,
            int followerCount,
            boolean active
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.birthDate = birthDate;
        this.country = country;
        this.followerCount = followerCount;
        this.active = active;
    }

}
