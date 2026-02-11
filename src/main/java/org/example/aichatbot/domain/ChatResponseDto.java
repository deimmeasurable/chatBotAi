package org.example.aichatbot.domain;

import lombok.Data;

@Data
public class ChatResponseDto {
    private String reply;
    public ChatResponseDto(String reply) {
        this.reply = reply;
    }
}
