package com.example.OnlineMovieStreamingSystem.service;

import com.example.OnlineMovieStreamingSystem.domain.Country;
import com.example.OnlineMovieStreamingSystem.dto.response.country.CountryResponseDTO;

public interface CountryService {
    CountryResponseDTO convertToCountryResponseDTO(Country country);
}
