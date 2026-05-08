package com.github.electricista.intellijdocstringgenerator.settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class GeminiApiKeyManager {
    private static final String SUBSYSTEM = "GeminiDocstringGenerator";
    private static final String KEY = "GeminiApiKey";

    private static CredentialAttributes createCredentialAttributes() {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName(SUBSYSTEM, KEY));
    }

    public static String getApiKey() {
        Credentials credentials = PasswordSafe.getInstance().get(createCredentialAttributes());
        return credentials != null ? credentials.getPasswordAsString() : null;
    }

    public static void setApiKey(String apiKey) {
        Credentials credentials = new Credentials(null, apiKey);
        PasswordSafe.getInstance().set(createCredentialAttributes(), credentials);
    }
}