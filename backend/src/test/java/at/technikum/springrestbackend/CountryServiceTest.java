package at.technikum.springrestbackend;

import at.technikum.springrestbackend.model.Country;
import at.technikum.springrestbackend.repository.CountryRepository;
import at.technikum.springrestbackend.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllCountries_ShouldReturnListOfCountries() {
        // Arrange
        List<String> mockCountries = List.of("Austria", "Germany", "Switzerland");
        when(countryRepository.findAllCountries()).thenReturn(mockCountries);

        // Act
        List<String> countries = countryService.findAllCountries();

        // Assert
        assertNotNull(countries);
        assertEquals(3, countries.size());
        assertEquals("Austria", countries.get(0));
        verify(countryRepository, times(1)).findAllCountries();
    }

    @Test
    void findByName_ShouldReturnCountry_WhenCountryExists() {
        // Arrange
        Country mockCountry = new Country("Austria");
        when(countryRepository.findById("Austria")).thenReturn(Optional.of(mockCountry));

        // Act
        Country country = countryService.findByName("Austria");

        // Assert
        assertNotNull(country);
        assertEquals("Austria", country.getName());
        verify(countryRepository, times(1)).findById("Austria");
    }

    @Test
    void findByName_ShouldReturnNull_WhenCountryDoesNotExist() {
        // Arrange
        when(countryRepository.findById("Unknown")).thenReturn(Optional.empty());

        // Act
        Country country = countryService.findByName("Unknown");

        // Assert
        assertNull(country);
        verify(countryRepository, times(1)).findById("Unknown");
    }

    @Test
    void findByName_ShouldReturnNull_WhenCountryIsNull() {
        // Act
        Country country = countryService.findByName(null);

        // Assert
        assertNull(country);
        verify(countryRepository, never()).findById(anyString());
    }

    @Test
    void save_ShouldReturnSavedCountry() {
        // Arrange
        Country mockCountry = new Country("Austria");
        when(countryRepository.save(mockCountry)).thenReturn(mockCountry);

        // Act
        Country savedCountry = countryService.save(mockCountry);

        // Assert
        assertNotNull(savedCountry);
        assertEquals("Austria", savedCountry.getName());
        verify(countryRepository, times(1)).save(mockCountry);
    }
}
