package at.technikum.springrestbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PictureDto {
    private Long id;

    private Long userId;

    public PictureDto(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

}
