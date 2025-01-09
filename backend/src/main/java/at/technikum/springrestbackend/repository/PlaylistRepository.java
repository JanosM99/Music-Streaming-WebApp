package at.technikum.springrestbackend.repository;

import at.technikum.springrestbackend.dto.PlaylistDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import at.technikum.springrestbackend.model.Playlist;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query("SELECT DISTINCT new at.technikum.springrestbackend.dto.PlaylistDto "
            + "(p.id AS id, "
            + "p.name AS name, "
            + "p.user.id AS userId) "
            + "FROM Playlist p "
            + "JOIN p.songs s") // Only select playlists that have songs
    Page<PlaylistDto> findAllPageable(Pageable pageable);

    @Query("SELECT DISTINCT new at.technikum.springrestbackend.dto.PlaylistDto "
            + "(p.id AS id, "
            + "p.name AS name, "
            + "p.user.id AS userId) "
            + "FROM Playlist p "
            + "JOIN p.songs s " // Only select playlists that have songs
            + "WHERE (:filter IS NULL OR CONCAT(p.name, ' ') LIKE %:filter%)")
    Page<PlaylistDto> findAllWithFilterPageable(Pageable pageable, @Param("filter") String filter);

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId")
    List<Playlist> findAllByUserId(@Param("userId") Long userId);

}
