package de.levin.chatquizbattles.utils;

import de.levin.chatquizbattles.ChatQuizBattles;
import de.levin.chatquizbattles.mathquiz.Calculation;
import de.levin.chatquizbattles.mathquiz.Game;
import de.levin.chatquizbattles.language.Translation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileManager {

    private FileConfiguration fileCfg_ConfigMath_German;
    private FileConfiguration fileCfg_ConfigMath_English;
    private FileConfiguration fileCfg_ConfigMath;
    private Game game = new Game();

    public FileConfiguration getConfigMath_German(){
        return fileCfg_ConfigMath_German;
    }

    public void setConfigMath_German(FileConfiguration configuration){
        this.fileCfg_ConfigMath_German = configuration;
    }

    public FileConfiguration getConfigMath_English(){
        return fileCfg_ConfigMath_English;
    }

    public void setConfigMath_English(FileConfiguration configuration){
        this.fileCfg_ConfigMath_English = configuration;
    }

    public void setConfigMath() {
        Translation translation = new Translation();
        translation.setLanguage(Translation.Language.valueOf(ChatQuizBattles.instance.getConfig().getString("Language")));
        System.err.println(translation.getLanguage());

        if (translation.getLanguage().equals(Translation.Language.german) && fileCfg_ConfigMath_German != null) {
            System.err.println("is german");
            this.fileCfg_ConfigMath = fileCfg_ConfigMath_German;
        } else if (translation.getLanguage().equals(Translation.Language.english) && fileCfg_ConfigMath_English != null) {
            this.fileCfg_ConfigMath = fileCfg_ConfigMath_English;
        } else {
            System.err.println("AHH");
        }
    }

    public FileConfiguration getConfigMath() {
        return fileCfg_ConfigMath;
    }

    public void loadMessages() {
        File file_ConfigMath_German = new File(ChatQuizBattles.instance.getDataFolder(), "configMath_german.yml");
        File file_ConfigMath_English = new File(ChatQuizBattles.instance.getDataFolder(), "configMath_english.yml");
        if (!file_ConfigMath_German.exists()) {
            createConfigMath_German();
        }
        if (!file_ConfigMath_English.exists()) {
            createConfigMath_English();
        }
        setConfigMath_German(YamlConfiguration.loadConfiguration(file_ConfigMath_German));
        setConfigMath_English(YamlConfiguration.loadConfiguration(file_ConfigMath_English));
    }

    public void createConfigMath_German() {
        String germanFileName = "configMath_german.yml";
        File germanLanguageFile = new File(ChatQuizBattles.instance.getDataFolder(), germanFileName);
        if (!germanLanguageFile.exists()) {
            try {
                germanLanguageFile.getParentFile().mkdirs();
                germanLanguageFile.createNewFile();
                InputStream defaultStream = ChatQuizBattles.instance.getResource(germanFileName);
                Files.copy(defaultStream, germanLanguageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                setConfigMath_German(YamlConfiguration.loadConfiguration(germanLanguageFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createConfigMath_English() {
        String englishFileName = "configMath_english.yml";
        File englishLanguageFile = new File(ChatQuizBattles.instance.getDataFolder(), englishFileName);
        if (!englishLanguageFile.exists()) {
            try {
                englishLanguageFile.getParentFile().mkdirs();
                englishLanguageFile.createNewFile();
                InputStream defaultStream = ChatQuizBattles.instance.getResource(englishFileName);
                Files.copy(defaultStream, englishLanguageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                setConfigMath_English(YamlConfiguration.loadConfiguration(englishLanguageFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadConfigFiles() {
        if (game.isActive()) {
            game.setActive(false);
            Translation translation = new Translation();
            Bukkit.broadcastMessage(translation.getPluginPrefix() + "§cDas Quiz wurde durch ein Reload gestoppt§8.");
//            MathQuiz.startQuiz(true);
        }
        File file_ConfigMath_German = new File(ChatQuizBattles.instance.getDataFolder(), "configMath_german.yml");
        File file_ConfigMath_English = new File(ChatQuizBattles.instance.getDataFolder(), "configMath_english.yml");
        File file_Config = new File(ChatQuizBattles.instance.getDataFolder(), "config.yml");

        if (file_ConfigMath_German.exists()) {
            setConfigMath_German(YamlConfiguration.loadConfiguration(file_ConfigMath_German));
        }

        if (file_ConfigMath_English.exists()) {
            setConfigMath_English(YamlConfiguration.loadConfiguration(file_ConfigMath_English));
        }

        if (file_Config.exists()) {
            ChatQuizBattles.instance.reloadConfig();
        }

        for (BukkitTask task : game.getTasks().values()) {
            if (task != null) {
                task.cancel();
            }
            Translation translation = new Translation();
            translation.setPluginPrefix();
        }

        setConfigMath();
        Calculation calculation = new Calculation();
        game.setDelayUntilStart(calculation.replaceSecondsMinutes("QuizTimer"));
        BukkitTask delayedStart = Bukkit.getScheduler().runTaskTimer(ChatQuizBattles.instance, () -> game.start(), game.getDelayUntilStart(), game.getDelayUntilStart());
        game.addTask(Game.TaskName.Start,delayedStart);
        Translation translation = new Translation();
        translation.setPluginPrefix();
    }

    public void setup(){
        Translation translation = new Translation();
        translation.setLanguage(Translation.Language.valueOf(ChatQuizBattles.instance.getConfig().getString("Language")));

        loadMessages();
        System.err.println("intalise");
        setConfigMath();

    }
}
