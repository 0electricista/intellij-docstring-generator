package com.github.electricista.intellijdocstringgenerator.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class GeminiSettingsComponent {
    private final JPanel myMainPanel;
    private final JBPasswordField apiKeyField = new JBPasswordField();

    public GeminiSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Gemini API Key: "), apiKeyField, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return apiKeyField;
    }

    public String getApiKey() {
        return String.valueOf(apiKeyField.getPassword());
    }

    public void setApiKey(String newText) {
        apiKeyField.setText(newText);
    }
}