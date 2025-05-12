package com.example.OnlineMovieStreamingSystem.service.impl;

import com.example.OnlineMovieStreamingSystem.domain.Country;
import com.example.OnlineMovieStreamingSystem.dto.response.country.CountryResponseDTO;
import com.example.OnlineMovieStreamingSystem.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    @Override
    public CountryResponseDTO convertToCountryResponseDTO(Country country) {
        return CountryResponseDTO.builder()
                .id(country.getId())
                .name(country.getName())
                .build();
    }
}
