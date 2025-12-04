package ee.andu.filmirendipood.controller;

import ee.andu.filmirendipood.entity.Rental;
import ee.andu.filmirendipood.model.RentalFilm;
import ee.andu.filmirendipood.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @GetMapping("rentals")
    public List<Rental> getRentals(){
        return rentalService.getRentalList();
    }

    @PostMapping("start-rental")
    public String startRental(@RequestBody List<RentalFilm> rentalFilms){

        return rentalService.getRentalInitialFee(rentalFilms);
    }

    @PostMapping("end-rental")
    // TODO: days -> timestamp
    public String endRental(@RequestBody List<RentalFilm> rentalFilms){
        return rentalService.getRentalLateFee(rentalFilms);
    }

//    @PostMapping("end-rental")
//    public Rental endRental(@RequestParam Long rentalId){
//        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Rental with id " + rentalId + " not found"));
//
//        return rentalRepository.save(rental);
//    }

}
