package com.capestone.hrms_backend.service;

import java.io.File;
import java.util.List;

public interface IEmailService {
    void send(String to, String subject, String body);
    void send(List<String> to, String subject, String body);
    void sendWithAttachment(String to, String subject, String body, File attachment);
    void sendWithAttachment(List<String> to, String subject, String body, File attachment);
}