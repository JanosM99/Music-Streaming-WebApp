package at.technikum.springrestbackend;

import at.technikum.springrestbackend.minio.MinioService;
import at.technikum.springrestbackend.model.Picture;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.repository.PictureRepository;
import at.technikum.springrestbackend.service.PictureService;
import at.technikum.springrestbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PictureServiceTest {

    @Mock
    private PictureRepository pictureRepository;

    @Mock
    private UserService userService;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private PictureService pictureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindPictureByUserId() {
        Long userId = 1L;
        Picture picture = new Picture();
        when(pictureRepository.findByUser(userId)).thenReturn(Optional.of(picture));

        Optional<Picture> result = pictureService.findPictureByUserId(userId);

        assertTrue(result.isPresent());
        assertEquals(picture, result.get());
        verify(pictureRepository, times(1)).findByUser(userId);
    }

    @Test
    void testDownloadAvatarFile() throws Exception {
        Picture picture = new Picture();
        picture.setFileName("avatar.png");
        String pictureBucketName = "bucket";
        InputStream inputStream = mock(InputStream.class);
        when(minioService.downloadFile(pictureBucketName, "avatar.png")).thenReturn(inputStream);

        InputStream result = pictureService.downloadAvatarFile(picture, pictureBucketName);

        assertNotNull(result);
        assertEquals(inputStream, result);
        verify(minioService, times(1)).downloadFile(pictureBucketName, "avatar.png");
    }

    @Test
    void testUploadAvatar() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("avatar.png");

        User user = new User();
        user.setId(1L);

        String songBucketName = "bucket";

        doNothing().when(minioService).uploadFile(anyString(), anyString(), any(MultipartFile.class));

        ArgumentCaptor<Picture> pictureCaptor = ArgumentCaptor.forClass(Picture.class);

        when(pictureRepository.save(pictureCaptor.capture())).thenAnswer(invocation -> pictureCaptor.getValue());

        Picture result = pictureService.uploadAvatar(file, songBucketName, user);

        verify(minioService, times(1)).uploadFile(eq(songBucketName), eq("1_avatar.png"), eq(file));
        verify(pictureRepository, times(1)).save(any(Picture.class));

        assertNotNull(result);
        assertEquals("1_avatar.png", result.getFileName());
        assertEquals(user, result.getUser());

        // Also, check the object passed to save (captured by the ArgumentCaptor)
        Picture savedPicture = pictureCaptor.getValue();
        assertEquals("1_avatar.png", savedPicture.getFileName());
        assertEquals(user, savedPicture.getUser());
    }

    @Test
    void testDeleteAvatar() throws Exception {
        Picture picture = new Picture();
        picture.setFileName("avatar.png");
        Long userId = 1L;
        String pictureBucketName = "bucket";

        doNothing().when(minioService).deleteFile(pictureBucketName, "avatar.png");
        doNothing().when(userService).deletePictureByUserId(userId);

        pictureService.deleteAvatar(userId, picture, pictureBucketName);

        verify(minioService, times(1)).deleteFile(pictureBucketName, "avatar.png");
        verify(userService, times(1)).deletePictureByUserId(userId);
    }

    @Test
    void testIsValidImageType_ValidJPEG() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");

        boolean result = pictureService.isValidImageType(file);

        assertTrue(result);
    }

    @Test
    void testIsValidImageType_ValidPNG() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");

        boolean result = pictureService.isValidImageType(file);

        assertTrue(result);
    }

    @Test
    void testIsValidImageType_InvalidType() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");

        boolean result = pictureService.isValidImageType(file);

        assertFalse(result);
    }
}
