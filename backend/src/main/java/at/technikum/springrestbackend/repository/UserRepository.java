package at.technikum.springrestbackend.repository;

import at.technikum.springrestbackend.dto.UserDetailsDto;
import at.technikum.springrestbackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByName(String username);

    @Query(" SELECT DISTINCT new at.technikum.springrestbackend.dto.UserDetailsDto "
            + " ( u.id AS id, "
            + "u.username AS username, "
            + "u.email AS email, "
            + "u.role AS role, "
            + "u.birthDate AS birthDate, "
            + "u.country.name AS country, "
            + "u.followerCount AS followerCount, "
            + "u.active AS active ) "
            + " FROM User u "
            + " WHERE "
            + " u.active = TRUE ")
    Page<UserDetailsDto> findAllByActivePageable(Pageable pageable);

    @Query(" SELECT DISTINCT new at.technikum.springrestbackend.dto.UserDetailsDto "
            + " ( u.id AS id, "
            + "u.username AS username, "
            + "u.email AS email, "
            + "u.role AS role, "
            + "u.birthDate AS birthDate, "
            + "u.country.name AS country, "
            + "u.followerCount AS followerCount, "
            + "u.active AS active ) "
            + " FROM User u " )
    Page<UserDetailsDto> findAllPageable(Pageable pageable);


    @Query(" SELECT DISTINCT new at.technikum.springrestbackend.dto.UserDetailsDto "
            + " ( u.id AS id, "
            + "u.username AS username, "
            + "u.email AS email, "
            + "u.role AS role, "
            + "u.birthDate AS birthDate, "
            + "u.country.name AS country, "
            + "u.followerCount AS followerCount, "
            + "u.active AS active ) "
            + " FROM User u "
            + " WHERE "
            + " u.active = TRUE "
            + " AND (:filter IS NULL OR CONCAT(u.username, ' ', u.role , ' ', u.birthDate, ' ', u.email, ' ', u.country.name, ' ', u.followerCount) LIKE %:filter%) ")
    Page<UserDetailsDto> findAllByActiveWithFilterPageable(Pageable pageable, @Param("filter") String filter);

    @Query(" SELECT DISTINCT new at.technikum.springrestbackend.dto.UserDetailsDto "
            + " ( u.id AS id, "
            + "u.username AS username, "
            + "u.email AS email, "
            + "u.role AS role, "
            + "u.birthDate AS birthDate, "
            + "u.country.name AS country, "
            + "u.followerCount AS followerCount, "
            + "u.active AS active ) "
            + " FROM User u "
            + " WHERE (:filter IS NULL OR CONCAT(u.username, ' ', u.role , ' ', u.birthDate, ' ', u.email, ' ', u.country.name, ' ', u.followerCount, ' ', u.active) LIKE %:filter%) ")
    Page<UserDetailsDto> findAllWithFilterPageable(Pageable pageable, @Param("filter") String filter);

}

