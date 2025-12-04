package ee.andu.filmirendipood.controller;

import ee.andu.filmirendipood.entity.Film;
import ee.andu.filmirendipood.entity.FilmType;
import ee.andu.filmirendipood.entity.Rental;
import ee.andu.filmirendipood.model.RentalFilm;
import ee.andu.filmirendipood.repository.FilmRepository;
import ee.andu.filmirendipood.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RentalController {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private FilmRepository filmRepository;

    private final double PREMIUM_PRICE;
    private final double BASIC_PRICE;

    private final double DEFAULT_RENTAL_DAYS;
    private final double OLD_TYPE_RENTAL_DAYS;

    public RentalController() {
        BASIC_PRICE = 3;
        PREMIUM_PRICE = 4;
        DEFAULT_RENTAL_DAYS = 3;
        OLD_TYPE_RENTAL_DAYS = 5;
    }

    @GetMapping("rentals")
    public List<Rental> getRentals(){
        return rentalRepository.findAll();
    }


    @PostMapping("start-rental")
    public Rental startRental(@RequestBody List<RentalFilm> rentalFilms){

        Rental rental = new Rental();

        List<Film> filmsToBeRented = new ArrayList<>();
        double sum = 0.;

        // rentalfilm ->> saadab p2ringu tegija, film on andmebaasis
        for (RentalFilm rentalFilm : rentalFilms) {
            if (rentalFilm.getRentedDays() <= 0) {
                throw new RuntimeException("Cannot rent for 0 days or less");
            }

            Film film = filmRepository.findById(rentalFilm.getFilmId()).orElseThrow(()-> new RuntimeException("Film not found"));
            if (film.getDays() > 0) {
                throw new RuntimeException("Film is already renting");
            }

            film.setDays(rentalFilm.getRentedDays());
            film.setRental(rental);

            switch (film.getType()){
                case NEW ->
                    sum += rentalFilm.getRentedDays() * PREMIUM_PRICE;

                case REGULAR -> {
                    if (rentalFilm.getRentedDays() <= DEFAULT_RENTAL_DAYS) {
                        sum += BASIC_PRICE;
                    } else {
                        sum += BASIC_PRICE + (rentalFilm.getRentedDays() - DEFAULT_RENTAL_DAYS) * BASIC_PRICE;
                    }
                }
                case OLD -> {
                    if (rentalFilm.getRentedDays() <= OLD_TYPE_RENTAL_DAYS) {
                        sum += BASIC_PRICE;
                    } else {
                        sum += BASIC_PRICE + (rentalFilm.getRentedDays() - OLD_TYPE_RENTAL_DAYS) * BASIC_PRICE;
                    }
                }
            }
            filmsToBeRented.add(film);
        }

        rental.setFilms(filmsToBeRented);
        rental.setLateFee(0);
        rental.setInitialFee(sum);

        return rentalRepository.save(rental);
    }

//    @PostMapping("end-rental")
//    public Rental endRental(@RequestParam Long rentalId){
//        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Rental with id " + rentalId + " not found"));
//
//        return rentalRepository.save(rental);
//    }

    @PostMapping("end-rental")
    // TODO: days -> timestamp
    public String endRental(@RequestBody List<RentalFilm> rentalFilms){
        double sum = 0;

        for (RentalFilm rentalFilm : rentalFilms) {
            if (rentalFilm.getRentedDays() <= 0) {
                throw new RuntimeException("Cannot rent for 0 days or less");
            }

            Film film = filmRepository.findById(rentalFilm.getFilmId()).orElseThrow(()-> new RuntimeException("Film not found"));

            if (film.getDays() == 0 || film.getRental() == null) {
                throw new RuntimeException("Film is not renting");
            }

            Rental rental = film.getRental();

            double daysDifference = rentalFilm.getRentedDays() - film.getDays();

            if (daysDifference > 0) {
                double price_base = BASIC_PRICE;

                if (film.getType() == FilmType.NEW) {
                    price_base = PREMIUM_PRICE;
                }

                double filmFee = rental.getLateFee() + daysDifference * price_base;
                rental.setLateFee(filmFee);
                sum += filmFee;
            }

            film.setDays(0);
            film.setRental(null);
            rentalRepository.save(rental);
        }

        return "Late fee total: " + sum;
    }

}
