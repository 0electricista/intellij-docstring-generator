package com.github.electricista.intellijdocstringgenerator.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GeminiSettingsConfigurable implements Configurable {

    private GeminiSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Gemini Docstring Generator";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new GeminiSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        // Stops Run Plugin from showing generic Throwable error
        if (mySettingsComponent == null) {
            return false;
        }
        String storedKey = GeminiApiKeyManager.getApiKey();
        String currentKeyInUi = mySettingsComponent.getApiKey();
        return !currentKeyInUi.equals(storedKey == null ? "" : storedKey);
    }

    @Override
    public void apply() {
        if (mySettingsComponent != null) {
            GeminiApiKeyManager.setApiKey(mySettingsComponent.getApiKey());
        }
    }

    @Override
    public void reset() {
        if (mySettingsComponent != null) {
            String storedKey = GeminiApiKeyManager.getApiKey();
            mySettingsComponent.setApiKey(storedKey == null ? "" : storedKey);
        }
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}