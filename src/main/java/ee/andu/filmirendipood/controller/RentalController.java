package ee.andu.filmirendipood.controller;

import ee.andu.filmirendipood.entity.Film;
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

    public RentalController() {
        BASIC_PRICE = 3;
        PREMIUM_PRICE = 4;
    }

    @GetMapping("rentals")
    public List<Rental> getRentals(){
        return rentalRepository.findAll();
    }


    @PostMapping("start-rental")
    public Rental startRental(@RequestBody List<RentalFilm> rentalFilms){

        List<Film> toBeRented = new ArrayList<>();
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
            switch (film.getType()){
                case NEW -> {
                    sum += rentalFilm.getRentedDays() * PREMIUM_PRICE;
                }
                case REGULAR -> {
                    if (rentalFilm.getRentedDays() <= 3) {
                        sum += BASIC_PRICE;
                    } else {
                        sum += BASIC_PRICE + (rentalFilm.getRentedDays() - 3) * BASIC_PRICE;
                    }
                }
                case OLD -> {
                    if (rentalFilm.getRentedDays() <= 5) {
                        sum += BASIC_PRICE;
                    } else {
                        sum += BASIC_PRICE + (rentalFilm.getRentedDays() - 5) * BASIC_PRICE;
                    }
                }
            }
            toBeRented.add(film);
        }

        filmRepository.saveAll(toBeRented);

        Rental rental = new Rental();
        rental.setLateFee(0);
        rental.setInitialFee(sum);

        return rentalRepository.save(rental);
    }

    @PostMapping("end-rental")
    public Rental endRental(@RequestParam Long rentalId){
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Rental with id " + rentalId + " not found"));
        return rentalRepository.save(rental);
    }

}
