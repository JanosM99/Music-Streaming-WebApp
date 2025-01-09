package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.PagedResponseDto;
import at.technikum.springrestbackend.dto.SongDto;
import at.technikum.springrestbackend.exception.EntityNotFoundException;
import at.technikum.springrestbackend.model.Song;
import at.technikum.springrestbackend.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * Fetches all songs associated with a specific user ID.
     *
     * @param userId the ID of the user for whom to retrieve songs
     * @return a list of songs associated with the given user ID
     */
    @GetMapping("/user/{userId}")
    public List<SongDto> getSongsByUserId(@PathVariable Long userId) {
        return songService.getSongsByUserId(userId);
    }

    /**
     * Retrieves a paginated list of songs with optional filtering.
     *
     * @param pageSize the number of songs per page
     * @param page the page number to retrieve
     * @param sort the sorting criteria (default is "name,asc")
     * @param filter an optional filter string to filter songs
     * @return a ResponseEntity containing a paged response with song details
     */
    @GetMapping("/get-all")
    public ResponseEntity<PagedResponseDto<SongDto>> getAllSongs(
            @RequestParam(name = "pageSize", defaultValue = "50", required = false) int pageSize,
            @RequestParam(name = "page", required = true) int page,
            @RequestParam(defaultValue = "name,asc") String sort,
            @RequestParam(required = false) String filter) {


        Page<SongDto> songPage;
        if (filter == null) {
            songPage = songService.findAllSongs(page, pageSize, sort);
        } else {
            songPage = songService
                    .findAllSongsWithFilter(page, pageSize, filter, sort);
        }

        PagedResponseDto<SongDto> response = new PagedResponseDto<>(
                songPage.getContent(),
                songPage.getNumber(),
                songPage.getTotalElements(),
                songPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a song by their ID.
     *
     * @param id the ID of the song to delete
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        try {
            songService.deleteSong(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Likes a song by their ID.
     *
     * @param id the ID of the user to delete
     * @return a ResponseEntity with no content
     */
    @PostMapping("/like/{id}")
    public ResponseEntity<Void> likeById(@PathVariable Long id,
                                         @RequestBody(required = true) int likeIncrement){
        try {
            songService.likeSong(id, likeIncrement);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
