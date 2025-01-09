package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.dto.PlaylistDto;
import at.technikum.springrestbackend.dto.SongDto;
import at.technikum.springrestbackend.exception.EntityNotFoundException;
import at.technikum.springrestbackend.model.Song;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.repository.SongRepository;
import at.technikum.springrestbackend.repository.UserRepository;
import at.technikum.springrestbackend.utils.PageableFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import at.technikum.springrestbackend.model.Playlist;
import at.technikum.springrestbackend.repository.PlaylistRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongService songService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;


    /**
     * Retrieves a list of playlists by userId.
     *
     * @param userId the ID of the User
     * @return List of playlists
     */
    public List<PlaylistDto> getPlaylistsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));

        List<Playlist> playlists = playlistRepository.findAllByUserId(userId);
        return playlists.stream()
                .map(this::convertToPlaylistDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a Playlist by their ID.
     * @param playlistId the ID of the playlist to be retrieved.
     * @return if no playlist with the given ID is found.
     */
    public PlaylistDto getPlaylistById(Long playlistId) {
        var result = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist with ID " + playlistId + " not found."));

        return convertToPlaylistDto(result);
    }

    /**
     * Returns all Playlists.
     *
     * @param page page to return
     * @param size size of page
     * @param sort sort parameter
     *
     * @return all playlists.
     */
    public Page<PlaylistDto> findAllPlaylists(int page, int size, String sort) {
        Pageable pageable = PageableFactory.create(page, size, sort);
        return playlistRepository.findAllPageable(pageable);
    }

    /**
     * Find all playlists with a filter.
     *
     * @param page A Page.
     * @param size The Page size.
     * @param filter The attribute to filter for.
     * @param sort The attribute to sort for.
     * @return Page of playlists
     **/
    public Page<PlaylistDto> findAllPlaylistsWithFilter(int page, int size, String filter, String sort) {
        Pageable pageable = PageableFactory.create(page, size, sort);
        return playlistRepository.findAllWithFilterPageable(pageable, filter);
    }

    /**
     * Creates a new playlist and returns its DTO.
     *
     * @param playlist the playlist to create
     * @return the created playlist as a PlaylistDto
     */
    public PlaylistDto createPlaylist(Playlist playlist, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));
        playlist.setUser(user);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return convertToPlaylistDto(savedPlaylist);
    }

    /**
     * Deletes a playlist by its ID.
     *
     * @param id the ID of the playlist to delete
     * @throws EntityNotFoundException if an error occurs during deletion
     */
    public void deletePlaylist(Long id) throws EntityNotFoundException {
        if (!playlistRepository.existsById(id)) {
            throw new EntityNotFoundException("Playlist with ID " + id + " not found");
        }
        playlistRepository.deleteById(id);
    }

    /**
     * Checks if a playlist exists by its ID.
     *
     * @param id the ID of the playlist
     * @return true if the playlist exists, false otherwise
     */
    public boolean playlistExists(Long id) {
        return playlistRepository.existsById(id);
    }

    /**
     * Adds a song to a playlist.
     *
     * @param playlistId the ID of the playlist
     * @param songId ID of the song to add
     * @return the updated PlaylistDto containing the playlist details
     */
    public PlaylistDto addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist with ID " + playlistId + " not found."));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Song with ID " + songId + " not found."));

        playlist.getSongs().add(song);
        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return convertToPlaylistDto(updatedPlaylist);
    }

    /**
     * Removes a song from a playlist.
     *
     * @param playlistId the ID of the playlist
     * @param songId ID of the song to remove
     * @throws EntityNotFoundException if the playlist does not exist or the song is not in the playlist
     */
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist with ID " + playlistId + " not found."));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Song with ID " + songId + " not found."));

        if (!playlist.getSongs().remove(song)) {
            throw new IllegalArgumentException("Song with ID " + songId + " is not in the playlist.");
        }

        playlistRepository.save(playlist);
    }

    /**
     * Converts a Playlist object to a PlaylistDto
     * @param playlist playlist object
     * @return PlaylistDto
     */
    public PlaylistDto convertToPlaylistDto(Playlist playlist) {
        List<SongDto> songDtos = playlist.getSongs().stream()
                .map(song -> songService.convertToSongDto(song))
                .collect(Collectors.toList());

        return new PlaylistDto(
                playlist.getId(),
                playlist.getName(),
                playlist.getUser().getId(),
                songDtos
        );
    }

}
