package de.levin.chatquizbattles;

import de.levin.chatquizbattles.language.Translation;
import de.levin.chatquizbattles.mathquiz.Game;
import de.levin.chatquizbattles.utils.FileManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatQuizBattles extends JavaPlugin {

    static Economy econ;
    public static JavaPlugin instance;
    Game game = new Game();
    FileManager fileManager = new FileManager();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupEconomy();
        registerCommands();
        fileManager.setup();
        game.setup();

        getServer().getPluginManager().registerEvents(new MathQuiz(), instance);
    }


    @Override
    public void onDisable() {
    }

    private void registerCommands(){
        this.getCommand("quiz").setExecutor(new CommandQuiz());
        this.getCommand("quiz").setTabCompleter(new CommandQuiz());
    }
    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Install Vault plugin, for full support.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("Unable to get Economy service provider. Disabling plugin.");
            return;
        }
        econ = rsp.getProvider();
        if (econ == null) {
            getLogger().severe("Economy provider is null. Disabling plugin.");
        }
    }

    public static Economy getEcon(){
        return ChatQuizBattles.econ;
    }

}
