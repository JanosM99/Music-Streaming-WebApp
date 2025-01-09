package at.technikum.springrestbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SongDto {
    private Long id;

    private String name;

    private String artist;

    private String genre;

    private int likeCount;

    private Long userId;


    public SongDto() {}

    public SongDto(Long id, String name, String artist, String genre, long userId) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.userId = userId;
    }
}
