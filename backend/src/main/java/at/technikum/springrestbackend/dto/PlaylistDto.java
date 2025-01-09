package at.technikum.springrestbackend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PlaylistDto {

    private Long id;

    private String name;

    private Long userId;

    private List<SongDto> songs;

    public PlaylistDto() {}

    public PlaylistDto(Long id, String name, Long userId, List<SongDto> songs) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.songs = songs;
    }

}
