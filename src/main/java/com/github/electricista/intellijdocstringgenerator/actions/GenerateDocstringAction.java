package com.github.electricista.intellijdocstringgenerator.actions;

import com.github.electricista.intellijdocstringgenerator.services.DocstringGeneratorService;
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
import org.jetbrains.annotations.NotNull;

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
                        break;
                    }
                }
            }
            PsiElement element = file.findElementAt(codeOffset);
            PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class, false);
            if (selectedText != null && method != null) {
                startGenerationTask(event.getProject(), selectedText, document, offset);
            } else {
                Messages.showWarningDialog(event.getProject(), "Please select the body of a Java method to generate a docstring.", "Invalid Selection");
            }
        }
    }

    private void startGenerationTask(Project project, String methodBody, Document document, int offset) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Generating method docstring with Gemini") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    DocstringGeneratorService generatorService = new DocstringGeneratorService();
                    String result = generatorService.generateDocstring(methodBody);
                    
                    ApplicationManager.getApplication().invokeLater(() -> {
                        WriteCommandAction.runWriteCommandAction(project, () ->
                                document.insertString(offset, result + "\n")
                        );
                    });

                } catch (IllegalStateException ex) {
                    ApplicationManager.getApplication().invokeLater(() ->
                            Messages.showErrorDialog(project, "Gemini API key is not set. Please configure it in the settings.", "API Key Missing"));
                } catch (Exception ex) {
                    Notifications.Bus.notify(new Notification("AI_Docstring_Notifications", "AI error",
                            "Couldn't connect with Gemini: " + ex.getMessage(), NotificationType.ERROR));
                }
            }
        });
    }
}
