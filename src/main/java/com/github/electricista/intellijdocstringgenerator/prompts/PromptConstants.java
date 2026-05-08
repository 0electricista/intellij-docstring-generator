package com.github.electricista.intellijdocstringgenerator.prompts;

public class PromptConstants {
    public static final String GENERATE_DOCSTRING_PROMPT = "Generate only a professional Javadoc docstring for the provided Java method.\n" +
            "\n" +
            "Mandatory requirements:\n" +
            "\n" +
            "Return exclusively the Javadoc block (/** ... */).\n" +
            "Do not include the method body.\n" +
            "Do not include the method signature.\n" +
            "Do not use Markdown blocks or ```java.\n" +
            "Do not add explanations, notes, considerations, or any additional text before or after.\n" +
            "Document in a technical and precise manner:\n" +
            "the purpose of the method,\n" +
            "the general execution flow,\n" +
            "parameters (@param),\n" +
            "return value (@return),\n" +
            "relevant exceptions (@throws).\n" +
            "Use a professional tone and standard enterprise Java documentation style.\n" +
            "Describe implicit restrictions and validations when they are evident.\n" +
            "Maintain good readability and clean formatting.\n" +
            "Do not invent behavior that does not exist in the code.\n" +
            "\n" +
            "Method:\n" +
            "%s";
}
