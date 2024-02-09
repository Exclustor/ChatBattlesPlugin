package de.levin.chatquizbattles.language;

import de.levin.chatquizbattles.ChatQuizBattles;
import de.levin.chatquizbattles.utils.FileManager;

public class Translation {

    private Language language;
    private String pluginPrefix;

    public enum Language {
        german,
        english
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getPluginPrefix() {
        return pluginPrefix;
    }

    public void setPluginPrefix() {
        FileManager fileManager = new FileManager();
        this.pluginPrefix = fileManager.getConfigMath().getString("Prefix").replace("&", "§");
    }

    public String getTranslationKey() {
        if (getLanguage() == null) {
            this.language = Language.valueOf(ChatQuizBattles.instance.getConfig().getString("Language"));
        }
        if (getLanguage().equals(Language.german)) {
            return "bis";
        } else if (getLanguage().equals(Language.english)) {
            return "to";
        }

        return null;
    }
}
