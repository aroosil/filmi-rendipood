package ee.andu.filmirendipood.repository;

import ee.andu.filmirendipood.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film,Long> {
}
