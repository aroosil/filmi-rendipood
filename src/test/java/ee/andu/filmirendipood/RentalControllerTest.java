package ee.andu.filmirendipood;

import ee.andu.filmirendipood.controller.RentalController;
import ee.andu.filmirendipood.entity.Film;
import ee.andu.filmirendipood.entity.FilmType;
import ee.andu.filmirendipood.entity.Rental;
import ee.andu.filmirendipood.model.RentalFilm;

import ee.andu.filmirendipood.repository.FilmRepository;
import ee.andu.filmirendipood.repository.RentalRepository;
import ee.andu.filmirendipood.service.RentalService;
import org.hibernate.service.spi.InjectService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RentalControllerTest {

    @Mock
    RentalRepository rentalRepository;

    @Mock
    FilmRepository filmRepository;

    @InjectMocks // autowired aga mockib
    RentalService rentalService;


    Film film = new Film();
    List<RentalFilm> rentalFilms =  new ArrayList<>();
    Rental rental = new Rental();
    RentalFilm rentalFilm = new RentalFilm();

    @BeforeEach
    void setUp() {

        film.setDays(0);
        film.setType(FilmType.NEW);
        rentalFilm.setFilmId(1L);
    }

    @Test
    void givenFilmIsNewAndRentedFor10Days_whenStartingRental_thenSumIs40() {
        rentFilm(10, FilmType.NEW, 40.0);
    }

    private void rentFilm(int days, FilmType filmType, double sum) {
        when(rentalRepository.save(any())).thenReturn(rental);

        film.setType(filmType);
        rentalFilm.setRentedDays(days);
        rentalFilms.add(rentalFilm);
        String response = rentalService.getRentalInitialFee(rentalFilms);

        assertEquals("Total initial fee: " + sum, response);
    }

    @Test
    void givenFilmIsAlreadyRented_whenStartingRental_thenExceptionIsThrown() {
        film.setDays(1);
        rentalFilm.setRentedDays(1);
        rentalFilms.add(rentalFilm);
        when(rentalRepository.save(any())).thenReturn(rental);

        String msg = assertThrows(RuntimeException.class, () -> rentalService.getRentalInitialFee(rentalFilms)).getMessage();
        assertEquals("Film is already renting", msg);
    }

    @Test
    void givenFilmIsNotInDatabase_whenStartingRental_thenExceptionIsThrown() {
        rentalFilm.setFilmId(2L);
        rentalFilm.setRentedDays(1);
        rentalFilms.add(rentalFilm);

        String msg = assertThrows(RuntimeException.class, () -> rentalService.getRentalInitialFee(rentalFilms)).getMessage();

        assertEquals("Film not found", msg);
    }

    @Test
    void endNewFilmRentalOnTime() {
        endRental(10, 10, FilmType.NEW, 0.0);
    }

    @Test
    void endNewFilmRentalOverTimeFor1Day() {
        endRental(10, 11, FilmType.NEW, 4.0);
    }

    private void endRental(int filmDays, int rentedDays, FilmType filmType, double expectedSum) {
        film.setDays(filmDays);
        film.setRental(rental);
        film.setType(filmType);

        rentalFilm.setRentedDays(rentedDays);

        rentalFilm.setFilmId(1L);
        rentalFilms.add(rentalFilm);

        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));

        String response = rentalService.getRentalLateFee(rentalFilms);
        assertEquals("Late fee total: " + expectedSum, response);
    }

}

// Õpetaja fail
//package ee.mihkel.filmirent;
//
//import ee.mihkel.filmirent.controller.RentalController;
//import ee.mihkel.filmirent.entity.Film;
//import ee.mihkel.filmirent.entity.FilmType;
//import ee.mihkel.filmirent.entity.Rental;
//import ee.mihkel.filmirent.model.RentalFilm;
//import ee.mihkel.filmirent.repository.FilmRepository;
//import ee.mihkel.filmirent.repository.RentalRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//class RentalControllerTest {
//
//    @Mock
//    RentalRepository rentalRepository;
//
//    @Mock
//    FilmRepository filmRepository;
//
//    @InjectMocks // tagataustal ongi @Autowired, aga ka mockib (jäljendab) andmebaase
//    RentalController rentalController;
//
//    List<RentalFilm> rentalFilms = new ArrayList<>();
//    RentalFilm rentalFilm = new RentalFilm();
//    Film film = new Film();
//    Rental rental = new Rental();
//
//    @BeforeEach
//    void setUp() {
//        rentalFilm.setFilmId(1L);
//        rentalFilms.add(rentalFilm);
//
//        film.setDays(0);
//        film.setRental(rental);
//    }
//
//    @Test
//    void given0DaysForRent_whenStartingRental_thenExceptionIsThrown() {
//
//        String message = assertThrows(RuntimeException.class, () -> rentalController.startRental(rentalFilms)).getMessage();
//
//        assertEquals("Cannot rent for 0 days or less", message);
//    }
//
//    @Test
//    void givenFilmIsNotInDB_whenStartingRental_thenExceptionIsThrown() {
//        rentalFilm.setFilmId(2L);
//        rentalFilm.setRentedDays(2);
//
//        String message = assertThrows(RuntimeException.class, () -> rentalController.startRental(rentalFilms)).getMessage();
//
//        assertEquals("No value present", message);
//    }
//
//    @Test
//    void givenFilmIsReadyRented_whenStartingRental_thenExceptionIsThrown() {
//        film.setDays(5);
//        rentalFilm.setRentedDays(1);
//        when(filmRepository.findById(1L)).thenReturn(Optional.of(film)); // jäljendame andmebaasi
//
//        String message = assertThrows(RuntimeException.class, () -> rentalController.startRental(rentalFilms)).getMessage();
//
//        assertEquals("Film already rented", message);
//    }
//
//    @Test
//    void givenFilmIsNewAndRentedFor10Days_whenStartingRental_thenSumIs40() {
//        rentFilm(10, FilmType.NEW, 40.0);
//    }
//
//    @Test
//    void givenFilmIsRegularAndRentedFor5Days_whenStartingRental_thenSumIs9() {
//        rentFilm(5, FilmType.REGULAR, 9.0);
//    }
//
//    private void rentFilm(int days, FilmType filmType, double sum) {
//        rentalFilm.setRentedDays(days);
//        film.setType(filmType);
//        when(filmRepository.findById(1L)).thenReturn(Optional.of(film)); // jäljendame andmebaasi
//        when(rentalRepository.save(any())).thenReturn(rental);
//
//        String response = rentalController.startRental(rentalFilms);
//
//        assertEquals("Total initial fee: " + sum, response);
//    }
//}