package com.github.electricista.intellijdocstringgenerator.services;

import com.github.electricista.intellijdocstringgenerator.settings.GeminiApiKeyManager;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

import java.time.Duration;

public class GeminiClientService {

    public static ChatLanguageModel getModel() {
        String apiKey = GeminiApiKeyManager.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Gemini API Key is missing.");
        }

        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash-lite")
                .timeout(Duration.ofSeconds(120))
                .build();
    }
}
