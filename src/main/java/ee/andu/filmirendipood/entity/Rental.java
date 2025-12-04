package ee.andu.filmirendipood.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double initialFee;
    private double lateFee;

    @JsonBackReference // seda välja ei näidata, muidu tekib lõputu tsükkel, kuna filmi küljes on ka rental https://www.baeldung.com/jackson-annotations
    @ManyToMany(cascade = CascadeType.PERSIST) // https://www.baeldung.com/jpa-cascade-types
    private List<Film> films;

    // cascade all -> kui rental lisatakse siis ka film lisatakse/muudetakse, aga ka kustutatakse kui rental kustutatakse
    // cascade persist -> kui lisatakse, cascade remove kui kustutatakse

}
