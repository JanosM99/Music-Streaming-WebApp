package at.technikum.springrestbackend.service;

import at.technikum.springrestbackend.model.Country;
import at.technikum.springrestbackend.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    /**
     * Returns all countries
     *
     * @return returns list of countries
     */
    public List<String> findAllCountries() {
        return countryRepository.findAllCountries();
    }

    /**
     * Finds a given country by a unique name, if no country is found the method returns null.
     *
     * @param country a given country we try to find in the db
     * @return a country if it exists, otherwise null
     */
    public Country findByName(String country) {

        if (country != null) {
            var optionalCountry = this.countryRepository.findById(country);

            if (optionalCountry.isPresent()) {
                return optionalCountry.get();
            }
        }

        return null;
    }

    /**
     * Saves a new country identified by a given string.
     *
     * @param country the name of the country we want to save
     * @return returns the Country object created by saving a new country
     */
    public Country save(Country country) {
        return this.countryRepository.save(country);
    }

}
