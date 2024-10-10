package at.technikum.springrestbackend.controller;

import at.technikum.springrestbackend.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/country")
public class CountryController {

    @Autowired
    CountryService countryService;


    /**
     * Get all countries.
     */
    @GetMapping("/all")
    public ResponseEntity<List<String>> getAll() {

        List<String> countryList = countryService.findAllCountries();
        return ResponseEntity.ok(countryList);
    }

}