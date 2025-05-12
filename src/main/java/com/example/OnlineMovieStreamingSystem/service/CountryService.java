package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.Country;
import com.example.OnlineMovieStreamingSystem.dto.response.country.CountryResponseDTO;

import java.util.List;

public interface CountryService {
    CountryResponseDTO convertToCountryResponseDTO(Country country);
    List<CountryResponseDTO> getAllCountries();
}
