package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.SongDto;
import at.technikum.springrestbackend.exception.EntityNotFoundException;
import at.technikum.springrestbackend.model.Song;
import at.technikum.springrestbackend.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/songs")
@CrossOrigin
public class SongController {

    @Autowired
    SongService songService;


    /**
     * Retrieves a song by their ID.
     *
     * @param id the ID of the song to retrieve
     * @return a ResponseEntity containing the song details
     * @throws EntityNotFoundException if the song is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable Long id) throws EntityNotFoundException {
        Song song = songService.findById(id);
        return ResponseEntity.ok(songService.convertToSongDto(song));
    }

}
