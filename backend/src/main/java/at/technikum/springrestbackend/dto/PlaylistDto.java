package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.model.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaylistDto {
    private Long id;

    private String name;

    private Long userId;

    private List<Song> songs;


    public PlaylistDto(Long id, String name, Long userId, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.songs = songs;
    }

}
