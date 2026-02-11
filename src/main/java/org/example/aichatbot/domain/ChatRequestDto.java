package org.example.aichatbot.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatRequestDto implements Serializable {
    private String message;
}
