package at.technikum.springrestbackend.repository;

import at.technikum.springrestbackend.dto.SongDto;
import at.technikum.springrestbackend.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    @Query(" SELECT DISTINCT new at.technikum.springrestbackend.dto.SongDto "
            + " ( s.id AS id, "
            + "s.name AS name, "
            + "s.artist AS artist, "
            + "s.genre AS genre, "
            + "s.likeCount AS likeCount,"
            + "s.user.id As userId) "
            + " FROM Song s " )
    Page<SongDto> findAllPageable(Pageable pageable);

    @Query(" SELECT DISTINCT new at.technikum.springrestbackend.dto.SongDto "
            + " ( s.id AS id, "
            + "s.name AS name, "
            + "s.artist AS artist, "
            + "s.genre AS genre, "
            + "s.likeCount AS likeCount,"
            + "s.user.id As userId) "
            + " FROM Song s "
            + " WHERE (:filter IS NULL OR CONCAT(s.name, ' ', s.artist , ' ', s.genre, ' ', s.likeCount) LIKE %:filter%)  " )
    Page<SongDto> findAllWithFilterPageable(Pageable pageable, @Param("filter") String filter);

    @Query("SELECT s FROM Song s WHERE s.user.id = :userId")
    List<Song> findAllByUserId(@Param("userId") Long userId);

}
