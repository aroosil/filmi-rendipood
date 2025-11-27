package ee.andu.filmirendipood.controller;

import ee.andu.filmirendipood.entity.Film;
import ee.andu.filmirendipood.entity.FilmType;
import ee.andu.filmirendipood.repository.FilmRepository;

import org.springframework.beans.factory.annotation.Autowired;
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

       film.setDays(0);
        filmRepository.save(film);
        return filmRepository.findAll();
    }

    @DeleteMapping("films/{id}")
    public List<Film> deleteFilmById(@PathVariable Long id)
    {
        filmRepository.deleteById(id);
        return filmRepository.findAll();
    }
    //
    //    @DeleteMapping("films")
    //    public List<Film> deleteFilm(@RequestParam Long id)
    //    {
    //        filmRepository.deleteById(id);
    //        return filmRepository.findAll();
    //    }

    // @PutMapping - selles saab kõiki välju muuta
    // @PatchMapping - saab ainult ühte
    @PatchMapping("films")
    public List<Film> updateFilm(@RequestParam Long filmId, @RequestParam FilmType newType) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new RuntimeException("Cannot update type: Film with id " + filmId + " not found"));
        // Alternatiivne
        // Film film = filmRepository.findById(filmId).orElse(null);
        // if (film != null) {
//              throw new RuntimeException("Cannot update type: Film with id " + filmId + " already exists");
        // }
        film.setType(newType);
        filmRepository.save(film);
        return filmRepository.findAll();
    }

    @GetMapping("films/available")
    public List<Film> getAvailableFilms()
    {
        // Alternatiivne lähenemine
        //  Film probe = new Film();
        //  probe.setAvailable(true);
        //
        //  return filmRepository.findAll(Example.of(probe));

        return filmRepository.findByDays(0);
    }
}
