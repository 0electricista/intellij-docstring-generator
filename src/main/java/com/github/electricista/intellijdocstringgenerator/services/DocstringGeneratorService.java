package com.github.electricista.intellijdocstringgenerator.services;

import com.github.electricista.intellijdocstringgenerator.prompts.PromptConstants;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class DocstringGeneratorService {

    public String generateDocstring(String methodBody) {
        ChatLanguageModel model = GeminiClientService.getModel();
        String prompt = String.format(PromptConstants.GENERATE_DOCSTRING_PROMPT, methodBody);
        return model.generate(prompt);
    }
}
