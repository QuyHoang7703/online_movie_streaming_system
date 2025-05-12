package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Country;
import com.example.OnlineMovieStreamingSystem.dto.response.country.CountryResponseDTO;
import com.example.OnlineMovieStreamingSystem.repository.CountryRepository;
import com.example.OnlineMovieStreamingSystem.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    @Override
    public CountryResponseDTO convertToCountryResponseDTO(Country country) {
        return CountryResponseDTO.builder()
                .id(country.getId())
                .name(country.getName())
                .build();
    }

    @Override
    public List<CountryResponseDTO> getAllCountries() {
        List<Country> countries = this.countryRepository.findAll();

        List<CountryResponseDTO> countriesResponseDTO = countries.stream()
                .map(this::convertToCountryResponseDTO)
                .toList();

        return countriesResponseDTO;
    }
}
