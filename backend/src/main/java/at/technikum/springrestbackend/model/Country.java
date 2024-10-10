package at.technikum.springrestbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "country")
public class Country implements Serializable {

    @Id
    @Column(name = "name")
    private String name;

}
