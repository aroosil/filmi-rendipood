package ee.andu.filmirendipood.repository;

import ee.andu.filmirendipood.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film,Long> {
//    @Query("select ...")
    List<Film> findByDays(int days); // translates to select f1_0.id,f1_0.days,f1_0.name,f1_0.type from film f1_0 where f1_0.days=? ????
}
