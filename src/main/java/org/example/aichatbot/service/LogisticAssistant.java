package org.example.aichatbot.service;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;


//@AiService
public interface LogisticAssistant {

    @SystemMessage(
            "You are the Creseada Logistics AI.\n" +
                    "You provide information about Creseada International Limited when relevant.\n" +
                    "You also provide general guidance on shipping, logistics, customs clearing, tariffs, duties, and taxes.\n" +
                    "Shipping regulations, tariffs, and taxes may change over time.\n" +
                    "When discussing such topics, clearly state when information is general guidance and may require confirmation.\n" +
                    "For the latest official updates, advise users to verify with customs authorities or Creseada support.\n" +
                    "Refer users to https://www.creseada.com/contact-us for human support."
    )


        // The memoryId ensures user conversations stay separate
        String chat( @UserMessage String userMessage);
    }


