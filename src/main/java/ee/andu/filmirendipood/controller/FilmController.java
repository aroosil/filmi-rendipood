package ee.andu.filmirendipood.controller;

import ee.andu.filmirendipood.entity.Film;
import ee.andu.filmirendipood.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FilmController {

    @Autowired
    private FilmRepository filmRepository;

    @GetMapping("films")
    public List<Film> getFilms()
    {
        return filmRepository.findAll();
    }

    @PostMapping("films")
    public List<Film> saveFilm(@RequestBody Film film) {
       if (film.getId() != null)
        {
            throw new RuntimeException("Cannot add with Id!");
        }

        filmRepository.save(film);
        return filmRepository.findAll();
    }

    @DeleteMapping("films/{id}")
    public List<Film> deleteFilmById(@PathVariable Long id)
    {
        filmRepository.deleteById(id);
        return filmRepository.findAll();
    }

    @DeleteMapping("films")
    public List<Film> deleteFilm(@RequestParam Long id)
    {
        filmRepository.deleteById(id);
        return filmRepository.findAll();
    }

    @PutMapping("films")
    public List<Film> updateFilm(@RequestBody Film film) {
        if (film.getId() == null)
        {
            throw new RuntimeException("Cannot update: Film id is null");
        }
        filmRepository.save(film);
        return filmRepository.findAll();
    }

    @GetMapping("films/available")
    public List<Film> getAvailableFilms()
    {
        Film probe = new Film();
        probe.setAvailable(true);

        return filmRepository.findAll(Example.of(probe));
    }
}
