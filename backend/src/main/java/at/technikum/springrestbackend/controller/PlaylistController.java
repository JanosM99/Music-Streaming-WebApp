package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.PagedResponseDto;
import at.technikum.springrestbackend.dto.PlaylistDto;
import at.technikum.springrestbackend.exception.EntityNotFoundException;
import at.technikum.springrestbackend.security.principal.UserPrincipal;
import at.technikum.springrestbackend.service.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import at.technikum.springrestbackend.model.Playlist;
import at.technikum.springrestbackend.service.PlaylistService;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin
public class PlaylistController {

    @Autowired
    PlaylistService playlistService;

    @Autowired
    SongService songService;


    /**
     * Fetches all playlists associated with a specific user ID.
     *
     * @param userId the ID of the user for whom to retrieve playlists
     * @return a list of playlists associated with the given user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaylistDto>> getPlaylistsByUserId(@PathVariable Long userId) {
        List<PlaylistDto> playlists = playlistService.getPlaylistsByUserId(userId);
        return ResponseEntity.ok(playlists);
    }

    /**
     * Retrieves a playlist by their ID.
     * @param id the ID of the playlist to retrieve
     * @return a ResponseEntity containing the playlist details
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDto> getPlaylistById(@PathVariable Long id) {
        try {
            PlaylistDto playlistDto = playlistService.getPlaylistById(id);
            return ResponseEntity.ok(playlistDto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves a paginated list of playlists with optional filtering.
     *
     * @param pageSize the number of users per page
     * @param page the page number to retrieve
     * @param sort the sorting criteria (default is "name,asc")
     * @param filter an optional filter string to filter playlists
     * @return a ResponseEntity containing a paged response with playlist details
     */
    @GetMapping("/get-all")
    public ResponseEntity<PagedResponseDto<PlaylistDto>> getAllPlaylists(
            @RequestParam(name = "pageSize", defaultValue = "50", required = false) int pageSize,
            @RequestParam(name = "page", required = true) int page,
            @RequestParam(defaultValue = "name,asc") String sort,
            @RequestParam(required = false) String filter) {


        Page<PlaylistDto> playlistPage;
        if (filter == null) {
            playlistPage = playlistService.findAllPlaylists(page, pageSize, sort);
        } else {
            playlistPage = playlistService
                    .findAllPlaylistsWithFilter(page, pageSize, filter, sort);
        }

        PagedResponseDto<PlaylistDto> response = new PagedResponseDto<>(
                playlistPage.getContent(),
                playlistPage.getNumber(),
                playlistPage.getTotalElements(),
                playlistPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Create a new playlist
     *
     * @param playlist Playlist object to be created
     * @return PlaylistDto object containing playlist details
     */
    @PostMapping("/create")
    public ResponseEntity<PlaylistDto> createPlaylist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody Playlist playlist) {
        PlaylistDto createdPlaylist = playlistService.createPlaylist(playlist, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlaylist);
    }

    /**
     * Deletes a playlist by its ID.
     *
     * @param id the ID of the playlist to delete
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePlaylistById(@PathVariable Long id) {
        if (!playlistService.playlistExists(id)) {
            return ResponseEntity.notFound().build();
        }
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a song to a playlist.
     *
     * @param playlistId the ID of the playlist
     * @param songId ID of the song to add
     * @return a ResponseEntity with the updated playlist or an error message
     */
    @PostMapping("/{playlistId}/song/{songId}")
    public ResponseEntity<PlaylistDto> addSongToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        PlaylistDto updatedPlaylist = playlistService.addSongToPlaylist(playlistId, songId);
        return ResponseEntity.ok(updatedPlaylist);
    }

    /**
     * Removes a song from a playlist.
     *
     * @param playlistId the ID of the playlist
     * @param songId ID of the song to remove
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/{playlistId}/song/{songId}")
    public ResponseEntity<Void> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.noContent().build();
    }

}
