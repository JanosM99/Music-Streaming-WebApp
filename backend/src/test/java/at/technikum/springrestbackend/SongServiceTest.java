package at.technikum.springrestbackend;

import at.technikum.springrestbackend.dto.SongDto;
import at.technikum.springrestbackend.exception.EntityNotFoundException;
import at.technikum.springrestbackend.minio.MinioService;
import at.technikum.springrestbackend.model.Song;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.repository.SongRepository;
import at.technikum.springrestbackend.service.SongService;
import at.technikum.springrestbackend.utils.PageableFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SongServiceTest {

    @InjectMocks
    private SongService songService;

    @Mock
    private SongRepository songRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Pageable pageable;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_Success() throws EntityNotFoundException {
        Song song = new Song();
        song.setId(1L);
        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        Song foundSong = songService.findById(1L);

        assertNotNull(foundSong);
        assertEquals(1L, foundSong.getId());
    }

    @Test
    public void testFindById_NotFound() {
        when(songRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> songService.findById(1L));
    }

    @Test
    public void testFindSongById() {
        Song song = new Song();
        song.setId(1L);
        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        Optional<Song> foundSong = songService.findSongById(1L);

        assertTrue(foundSong.isPresent());
        assertEquals(1L, foundSong.get().getId());
    }

    @Test
    public void testDownloadSongFile() throws Exception {
        Song song = new Song();
        song.setFileName("test.mp3");
        InputStream mockInputStream = mock(InputStream.class);
        when(minioService.downloadFile(anyString(), eq("test.mp3"))).thenReturn(mockInputStream);

        InputStream result = songService.downloadSongFile(song, "songs");

        assertNotNull(result);
    }

    @Test
    public void testUploadSong_Success() throws Exception {
        SongDto songDto = new SongDto();
        songDto.setName("Test Song");
        songDto.setArtist("Test Artist");
        songDto.setGenre("Pop");

        User user = new User();
        user.setId(1L);

        when(multipartFile.getOriginalFilename()).thenReturn("test.mp3");
        doNothing().when(minioService).uploadFile(anyString(), anyString(), eq(multipartFile));
        when(songRepository.save(any(Song.class))).thenReturn(new Song());

        Song result = songService.uploadSong(multipartFile, "songs", user, songDto);

        assertNotNull(result);
    }

    @Test
    public void testDeleteSong_Success() throws Exception {
        Song song = new Song();
        song.setId(1L);
        song.setFileName("test.mp3");

        when(songRepository.findById(1L)).thenReturn(Optional.of(song));
        doNothing().when(minioService).deleteFile(anyString(), eq("test.mp3"));
        doNothing().when(songRepository).deleteById(1L);

        songService.deleteSong(1L);

        verify(minioService, times(1)).deleteFile(anyString(), eq("test.mp3"));
        verify(songRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testLikeSong_Success() throws Exception {
        Song song = new Song();
        song.setId(1L);
        song.setLikeCount(5);

        when(songRepository.findById(1L)).thenReturn(Optional.of(song));
        when(songRepository.save(any(Song.class))).thenReturn(song);

        songService.likeSong(1L, 3);

        assertEquals(8, song.getLikeCount());
        verify(songRepository, times(1)).save(any(Song.class));
    }

    @Test
    public void testIsValidFileType_Valid() {
        when(multipartFile.getContentType()).thenReturn("audio/mpeg");
        boolean isValid = songService.isValidFileType(multipartFile);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidFileType_Invalid() {
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        boolean isValid = songService.isValidFileType(multipartFile);

        assertFalse(isValid);
    }

    @Test
    public void testGetMediaTypeForFileName() {
        MediaType mediaType = songService.getMediaTypeForFileName("song.mp3");

        assertEquals(MediaType.valueOf("audio/mpeg"), mediaType);
    }

    @Test
    public void testConvertToSongDto() {
        User user = new User();
        user.setId(99L);

        Song song = new Song();
        song.setId(1L);
        song.setName("Test Song");
        song.setArtist("Test Artist");
        song.setGenre("Pop");
        song.setUser(user);

        SongDto songDto = songService.convertToSongDto(song);

        assertEquals(1L, songDto.getId());
        assertEquals("Test Song", songDto.getName());
        assertEquals("Test Artist", songDto.getArtist());
        assertEquals("Pop", songDto.getGenre());
    }

    @Test
    public void testFindAllSongs() {
        SongDto songDto = new SongDto();
        songDto.setId(1L);
        songDto.setName("Test Song");
        songDto.setArtist("Test Artist");
        songDto.setGenre("Pop");

        Page<SongDto> page = new PageImpl<>(Collections.singletonList(songDto));

        when(songRepository.findAllPageable(any(Pageable.class))).thenReturn(page);

        Page<SongDto> result = songService.findAllSongs(1, 10, "name,asc");

        assertNotNull(result, "The result should not be null");
        assertEquals(1, result.getContent().size(), "The size of the result content should be 1");
        assertEquals("Test Song", result.getContent().get(0).getName(), "Song name should match");
    }

    @Test
    public void testFindAllSongsWithFilter() {
        SongDto songDto = new SongDto();
        songDto.setId(1L);
        songDto.setName("Test Song");
        songDto.setArtist("Test Artist");
        songDto.setGenre("Pop");

        Page<SongDto> page = new PageImpl<>(Collections.singletonList(songDto));

        when(songRepository.findAllWithFilterPageable(any(Pageable.class), eq("pop"))).thenReturn(page);

        Page<SongDto> result = songService.findAllSongsWithFilter(1, 10, "pop", "name,asc");

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(1, result.getContent().size(), "The size of the result content should be 1");
        assertEquals("Test Song", result.getContent().get(0).getName(), "The song name should match");
    }
}