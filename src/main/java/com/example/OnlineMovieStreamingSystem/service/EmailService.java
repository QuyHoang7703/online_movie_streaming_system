package com.example.OnlineMovieStreamingSystem.service;

import org.thymeleaf.context.Context;

public interface EmailService {
    void sendEmail(String to, String subject, String templateName, Context context);
}
