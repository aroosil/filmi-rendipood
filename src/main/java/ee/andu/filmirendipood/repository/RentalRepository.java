package ee.andu.filmirendipood.repository;

import ee.andu.filmirendipood.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepositry --> kõik funktsioonid
// PagingAndSortingRepository --> minimaalsed funkts + kõik page ja sort funkts
// CrudRepository --> minimaalsed funktsioonid

public interface RentalRepository extends JpaRepository<Rental,Long> {
}
