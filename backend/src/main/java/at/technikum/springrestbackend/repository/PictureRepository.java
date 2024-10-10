package at.technikum.springrestbackend.repository;

import at.technikum.springrestbackend.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

    @Query("SELECT p FROM Picture p WHERE p.user.id = ?1")
    Optional<Picture> findByUser(Long userId);

    @Modifying
    @Query("DELETE FROM Picture p WHERE p.user.id = ?1")
    void deleteByUserId(Long userId);

}
