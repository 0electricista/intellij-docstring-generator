package com.github.electricista.intellijdocstringgenerator.actions;

import com.github.electricista.intellijdocstringgenerator.settings.GeminiApiKeyManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class GenerateDocstringAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        PsiFile file = event.getData(CommonDataKeys.PSI_FILE);
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor != null && file != null) {
            Document document = editor.getDocument();
            String selectedText = editor.getSelectionModel().getSelectedText();
            int offset = editor.getSelectionModel().getSelectionStart();
            int codeOffset = offset;
            if (selectedText != null) {
                for (int i = 0; i < selectedText.length(); i++) {
                    if (!Character.isWhitespace(selectedText.charAt(i))) {
                        codeOffset = offset + i;
                        break; // Stop at first real character.
                    }
                }
            }
            
            PsiElement element = file.findElementAt(codeOffset);
            PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class, false);
            if (selectedText != null && method != null) {
                startGenerationTask(event.getProject(), selectedText, document, offset);
            }else {
                Messages.showWarningDialog(event.getProject(), "Please select the body of a Java method to generate a docstring.", "Invalid Selection");
            }
        }
    }
    private void startGenerationTask(Project project, String methodBody, Document document, int offset) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Generating method docstring with Gemini") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String apiKey = GeminiApiKeyManager.getApiKey();
                if (apiKey == null || apiKey.trim().isEmpty()) {
                    ApplicationManager.getApplication().invokeLater(() ->
                            Messages.showErrorDialog(project, "Gemini API key is not set. Please configure it in the settings.", "API Key Missing"));
                    return;
                }
                try {
                    ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                            .apiKey(apiKey)
                            .modelName("gemini-2.5-flash-lite")
                            .timeout(Duration.ofSeconds(120))
                            .build();
                    String result = model.generate("Generate only a professional Javadoc docstring for the provided Java method.\n" +
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
                            methodBody);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        WriteCommandAction.runWriteCommandAction(project, () ->
                                document.insertString(offset, result+"\n")
                        );
                    });

                } catch (Exception ex) {
                    Notifications.Bus.notify(new Notification("AI_Docstring_Notifications", "AI error",
                            "Couldn't connect with Gemini: " + ex.getMessage(), NotificationType.ERROR));
                }
            }
        });
    }
}
