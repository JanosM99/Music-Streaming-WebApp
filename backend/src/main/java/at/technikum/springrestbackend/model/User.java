package at.technikum.springrestbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country", referencedColumnName = "name")
    private Country country;

    @Column(name = "follower_count")
    private int followerCount;

    @Column(name = "active")
    private boolean active;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Picture picture;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Song> songs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Playlist> playlists = new ArrayList<>();

}
