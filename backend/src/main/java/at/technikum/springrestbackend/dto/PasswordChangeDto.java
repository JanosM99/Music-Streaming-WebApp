package at.technikum.springrestbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordChangeDto {
    private String oldPassword;
    private String newPassword;
}
