package ee.andu.filmirendipood;

import ee.andu.filmirendipood.controller.RentalController;
import ee.andu.filmirendipood.entity.Film;
import ee.andu.filmirendipood.entity.FilmType;
import ee.andu.filmirendipood.entity.Rental;
import ee.andu.filmirendipood.model.RentalFilm;
import ee.andu.filmirendipood.repository.FilmRepository;

import ee.andu.filmirendipood.repository.RentalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RentalControllerTest {

    @Mock
    RentalRepository rentalRepository;

    @Autowired
    @Mock
    FilmRepository filmRepository;

    @InjectMocks // autowired aga mockib
    RentalController rentalController;

    @Test
    void rentFilmSuccess() {
        List<RentalFilm> rentalFilms = new ArrayList<>();

        RentalFilm rentalFilm = new RentalFilm();
        rentalFilm.setFilmId(1L);
        rentalFilm.setRentedDays(10);

        rentalFilms.add(rentalFilm);

        Film film = new Film();
        film.setName("test new film");
        film.setType(FilmType.NEW);
        film.setDays(0);

        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
//        when(filmRepository.save())
        Rental rental = rentalController.startRental(rentalFilms);

        System.out.println(rental);
        //assertEquals(Rental.class, rental.getClass());
    }

    @Test
    void givenFilmIsReadyRented_whenStartingRental_thenExceptionIsThrown() {
        List<RentalFilm> rentalFilms = new ArrayList<>();

        RentalFilm rentalFilm = new RentalFilm();
        rentalFilm.setFilmId(1L);
        rentalFilm.setRentedDays(10);

        rentalFilms.add(rentalFilm);

        Film film = new Film();
        film.setName("test new film");
        film.setType(FilmType.NEW);
        film.setDays(1);

        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));

        String msg = assertThrows(RuntimeException.class, () -> rentalController.startRental(rentalFilms)).getMessage();
        assertEquals("Film is already renting", msg);
    }

    @Test
    void givenFilmIsNotInDatabase_whenStartingRental_thenExceptionIsThrown() {
        List<RentalFilm> rentalFilms = new ArrayList<>();

        RentalFilm rentalFilm = new RentalFilm();
        rentalFilm.setFilmId(1L);
        rentalFilm.setRentedDays(10);

        rentalFilms.add(rentalFilm);

        String msg = assertThrows(RuntimeException.class, () -> rentalController.startRental(rentalFilms)).getMessage();
        assertEquals("Film not found", msg);
    }

}
