package ee.andu.filmirendipood.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private FilmType type;
    private Integer days; // kui days on 0, siis on available , int ei ole nullable, Integer on
    
    @ManyToOne
    private Rental rental;

}
