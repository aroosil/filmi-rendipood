package ee.andu.filmirendipood.service;

import ee.andu.filmirendipood.entity.Film;
import ee.andu.filmirendipood.entity.FilmType;
import ee.andu.filmirendipood.entity.Rental;
import ee.andu.filmirendipood.model.RentalFilm;
import ee.andu.filmirendipood.repository.FilmRepository;
import ee.andu.filmirendipood.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private FilmRepository filmRepository;

    private final double PREMIUM_PRICE;
    private final double BASIC_PRICE;

    private final double DEFAULT_RENTAL_DAYS;
    private final double OLD_TYPE_RENTAL_DAYS;

    public RentalService() {
        BASIC_PRICE = 3;
        PREMIUM_PRICE = 4;
        DEFAULT_RENTAL_DAYS = 3;
        OLD_TYPE_RENTAL_DAYS = 5;
    }

    public List<Rental> getRentalList() {
        return rentalRepository.findAll();
    }

    public String getRentalInitialFee(List<RentalFilm> rentalFilms) {
        Rental rental = new Rental();

        List<Film> filmsToBeRented = new ArrayList<>();
        double sum = 0.;

        // rentalfilm ->> saadab p2ringu tegija, film on andmebaasis
        for (RentalFilm rentalFilm : rentalFilms) {
            Film film = getFilmForRent(rentalFilm);

            film.setDays(rentalFilm.getRentedDays());
            film.setRental(rental);

            sum += calculateFilmFee(rentalFilm.getRentedDays(), film.getType());

            filmsToBeRented.add(film);
        }

        saveRental(rental, filmsToBeRented, sum);

        return "Total initial fee: " + sum;
    }

    public String getRentalLateFee(List<RentalFilm> rentalFilms) {
        double sum = 0;

        for (RentalFilm rentalFilm : rentalFilms) {
            Film film = getFilmToReturn(rentalFilm);

            Rental rental = film.getRental();

            double daysDifference = rentalFilm.getRentedDays() - film.getDays();

            if (daysDifference > 0) {
                sum += calculateLateFee(film.getType(), rental.getLateFee(), daysDifference);
                rental.setLateFee(sum);
            }

            film.setDays(0);
            film.setRental(null);
            rentalRepository.save(rental);
        }

        return "Late fee total: " + sum;
    }

    // PRIVATE API
    private Film getFilmToReturn(RentalFilm rentalFilm) {
        if (rentalFilm.getRentedDays() <= 0) {
            throw new RuntimeException("Cannot rent for 0 days or less");
        }

        Film film = filmRepository.findById(rentalFilm.getFilmId()).orElseThrow(()-> new RuntimeException("Film not found"));

        if (film.getDays() == 0 || film.getRental() == null) {
            throw new RuntimeException("Film is not renting");
        }
        return film;
    }

    private double calculateLateFee(FilmType filmType, double rentalLateFee, double daysDifference) {
        double price_base = BASIC_PRICE;

        if (filmType == FilmType.NEW) {
            price_base = PREMIUM_PRICE;
        }

        return rentalLateFee + daysDifference * price_base;
    }

    private void saveRental(Rental rental, List<Film> filmsToBeRented, double sum) {
        rental.setFilms(filmsToBeRented);
        rental.setLateFee(0);
        rental.setInitialFee(sum);
        rentalRepository.save(rental);
    }

    private Film getFilmForRent(RentalFilm rentalFilm) {
        if (rentalFilm.getRentedDays() <= 0) {
            throw new RuntimeException("Cannot rent for 0 days or less");
        }

        Film film = filmRepository.findById(rentalFilm.getFilmId()).orElseThrow(()-> new RuntimeException("Film not found"));
        if (film.getDays() > 0) {
            throw new RuntimeException("Film is already renting");
        }
        return film;
    }

    private double calculateFilmFee(int rentedDays, FilmType filmType) {
        double sum = 0.;
        switch (filmType){
            case NEW ->
                    sum = rentedDays * PREMIUM_PRICE;

            case REGULAR -> {
                if (rentedDays <= DEFAULT_RENTAL_DAYS) {
                    sum = BASIC_PRICE;
                } else {
                    sum = BASIC_PRICE + (rentedDays - DEFAULT_RENTAL_DAYS) * BASIC_PRICE;
                }
            }
            case OLD -> {
                if (rentedDays <= OLD_TYPE_RENTAL_DAYS) {
                    sum = BASIC_PRICE;
                } else {
                    sum = BASIC_PRICE + (rentedDays - OLD_TYPE_RENTAL_DAYS) * BASIC_PRICE;
                }
            }
        }
        return sum;
    }
}
