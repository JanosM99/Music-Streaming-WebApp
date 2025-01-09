package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.dto.SongDto;
import at.technikum.springrestbackend.model.Picture;
import at.technikum.springrestbackend.model.Song;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.security.principal.UserPrincipal;
import at.technikum.springrestbackend.service.PictureService;
import at.technikum.springrestbackend.service.SongService;
import at.technikum.springrestbackend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

@RestController
@RequestMapping("api/files")
@CrossOrigin
public class FileController {

    @Autowired
    UserService userService;

    @Autowired
    SongService songService;

    @Autowired
    PictureService pictureService;

    @Value("${minio.bucket-name}")
    private String pictureBucketName;

    @Value("${minio.bucket-name-songs}")
    private String songBucketName;


    /**
     * Uploads a song file to the server.
     *
     * @param userPrincipal the authenticated user uploading the song
     * @param file the MultipartFile representing the song to upload
     * @return a ResponseEntity indicating the result of the upload operation
     */
    @PostMapping("/songs")
    public ResponseEntity<String> uploadSong(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("artist") String artist,
            @RequestParam("genre") String genre) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        try {
            // File type validation
            if (!songService.isValidFileType(file)) {
                return ResponseEntity.badRequest().body("Invalid file type.");
            }

            // Retrieve the user
            User user = userService.findByName(userPrincipal.getUsername());

            // Create SongDto object
            SongDto songDto = new SongDto();
            songDto.setName(name);
            songDto.setArtist(artist);
            songDto.setGenre(genre);

            // Upload the song using the service
            songService.uploadSong(file, songBucketName, user, songDto);

            return ResponseEntity.status(HttpStatus.CREATED).body("Song uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while uploading the song: " + e.getMessage());
        }
    }

    /**
     * Uploads an avatar image for the authenticated user.
     *
     * @param userPrincipal the authenticated user uploading the avatar
     * @param file the MultipartFile representing the avatar to upload
     * @return a ResponseEntity indicating the result of the upload operation
     */
    @PostMapping("/avatars")
    public ResponseEntity<String> uploadAvatar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        try {
            // File type validation
            if (!pictureService.isValidImageType(file)) {
                return ResponseEntity.badRequest().body("Invalid file type.");
            }

            // Retrieve the user
            User user = userService.findByName(userPrincipal.getUsername());

            // Upload the picture using the service
            pictureService.uploadAvatar(file, pictureBucketName, user);

            return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    /**
     * Downloads a song by its ID.
     *
     * @param id the ID of the song to download
     * @return a ResponseEntity containing the InputStreamResource for the song
     */
    @GetMapping("/songs/{id}")
    public ResponseEntity<InputStreamResource> downloadSong(
            @PathVariable Long id) {
        try {
            Optional<Song> songOpt = songService.findSongById(id);
            if (songOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            Song song = songOpt.get();
            InputStream inputStream = songService.downloadSongFile(song, songBucketName);

            MediaType mediaType = songService.getMediaTypeForFileName(song.getFileName());
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + song.getFileName() + "\"")
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Downloads the avatar image for the specified user.
     *
     * @param userId the ID of the user whose avatar is to be downloaded
     * @return a ResponseEntity containing the InputStreamResource for the avatar
     */
    @GetMapping("/avatars/{userId}")
    public ResponseEntity<InputStreamResource> downloadAvatar(
            @PathVariable Long userId) {
        try {
            Optional<Picture> pictureOpt = pictureService.findPictureByUserId(userId);
            if (pictureOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            Picture picture = pictureOpt.get();
            InputStream inputStream = pictureService.downloadAvatarFile(picture, pictureBucketName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + picture.getFileName() + "\"")
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Deletes a song by its ID if the user is authorized.
     *
     * @param userPrincipal the authenticated user requesting the deletion
     * @param id the ID of the song to delete
     * @return a ResponseEntity indicating the result of the deletion operation
     */
    @DeleteMapping("/songs/{id}")
    @Transactional
    public ResponseEntity<String> deleteSong(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {

        try {
            // Retrieve the song by its ID
            Optional<Song> songOpt = songService.findSongById(id);
            if (songOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No song found with this ID.");
            }

            Song song = songOpt.get();
            // The user who uploaded the song
            User user = song.getUser();

            // Check if the user is either the uploader or has ROLE_ADMIN
            if (!userPrincipal.getId().equals(user.getId()) && userPrincipal.getAuthorities().stream()
                    .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete this song.");
            }

            // Delete the song from Minio and from the db
            songService.deleteSong(song.getId());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Song deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while deleting the song: " + e.getMessage());
        }
    }

    /**
     * Deletes the avatar image of the specified user if the user is authorized.
     *
     * @param userPrincipal the authenticated user requesting the deletion
     * @param userId the ID of the user whose avatar is to be deleted
     * @return a ResponseEntity indicating the result of the deletion operation
     */
    @DeleteMapping("/avatars/{userId}")
    @Transactional
    public ResponseEntity<String> deleteAvatar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long userId) {

        try {
            User user = userService.findById(userId);
            if (user == null || !userPrincipal.getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete this photo.");
            }

            // Retrieve the existing photo
            Optional<Picture> existingPhotoOpt = pictureService.findPictureByUserId(userId);
            if (existingPhotoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No photo found for this user.");
            }

            // Delete the file from Minio
            Picture existingPhoto = existingPhotoOpt.get();

            // Delete the picture from Minio and from the db
            pictureService.deleteAvatar(userId, existingPhoto, pictureBucketName);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Photo deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

}
