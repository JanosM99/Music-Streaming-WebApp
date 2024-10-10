package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.minio.MinioService;
import at.technikum.springrestbackend.model.Picture;
import at.technikum.springrestbackend.model.User;
import at.technikum.springrestbackend.repository.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

@Service
public class PictureService {

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MinioService minioService;


    /**
     * Retrieves the associated Picture with the specified User.
     * @return an {@link Optional} containing the {@link Picture} if found,
     * or an empty {@link Optional} if no Picture is associated with the user.
     */
    public Optional<Picture> findPictureByUserId(Long userId) {
        return pictureRepository.findByUser(userId);
    }

    /**
     * Returns the avatar file associated with the specified Picture.
     *
     * @param picture the Picture containing the file details
     * @param pictureBucketName the name of the Minio bucket where the picture is stored
     * @return an InputStream of the downloaded file
     * @throws Exception if there is an error during the file download process
     */
    public InputStream downloadAvatarFile(Picture picture, String pictureBucketName) throws Exception {
        return minioService.downloadFile(pictureBucketName, picture.getFileName());
    }

    /**
     * Uploads an avatar file for a specified user and saves it to the database.
     *
     * @param file the MultipartFile representing the avatar to be uploaded
     * @param songBucketName the name of the Minio bucket where the file will be uploaded
     * @param user the User associated with the avatar
     * @return the saved Picture object containing file details
     * @throws Exception if there is an error during the upload or saving process
     */
    public Picture uploadAvatar(MultipartFile file, String songBucketName, User user) throws Exception {
        // Generate a unique file name
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = user.getId() + "_" + originalFileName;

        // Upload to Minio
        minioService.uploadFile(songBucketName, uniqueFileName, file);

        // Save to DB
        Picture picture = new Picture();
        picture.setFileName(uniqueFileName);
        picture.setUser(user);
        pictureRepository.save(picture);

        return picture;
    }

    /**
     * Deletes the Picture associated with the specified Picture object from both Minio and the database.
     *
     * @param picture the Picture to be deleted
     * @param pictureBucketName the name of the Minio bucket where the picture is stored
     * @throws Exception if there is an error during the deletion process
     */
    public void deleteAvatar(Long userId, Picture picture, String pictureBucketName) throws Exception {
        // Delete the picture from Minio
        minioService.deleteFile(pictureBucketName, picture.getFileName());

        // Remove the picture from the database
        userService.deletePictureByUserId(userId);
    }

    /**
     * Checks if the uploaded file is a valid image type (JPEG or PNG).
     *
     * @param file the MultipartFile to be validated
     * @return true if the file type is valid, false otherwise
     */
    public boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return "image/jpeg".equals(contentType) || "image/png".equals(contentType);
    }
}
