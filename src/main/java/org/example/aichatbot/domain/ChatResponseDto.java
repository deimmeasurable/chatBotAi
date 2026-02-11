package org.example.aichatbot.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatResponseDto implements Serializable {
    private String reply;
    public ChatResponseDto(String reply) {
        this.reply = reply;
    }
}
