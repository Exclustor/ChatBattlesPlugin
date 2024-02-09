package de.levin.chatquizbattles;

import de.levin.chatquizbattles.language.Translation;
import de.levin.chatquizbattles.mathquiz.Game;
import de.levin.chatquizbattles.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CommandQuiz implements CommandExecutor, TabCompleter {

    private Translation translation = new Translation();
    private FileManager fileManager = new FileManager();
    private Game game = new Game();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        FileConfiguration cfg = fileManager.getConfigMath();
        if (sender.hasPermission("Quiz")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (!game.isActive()) {
                        sender.sendMessage(cfg.getString("quiz_start").replace("%prefix%", translation.getPluginPrefix()).replace("%player%", sender.getName()).replace("&", "§"));
                        game.start();
                    } else {
                        sender.sendMessage(cfg.getString("quiz_already_active").replace("%prefix%", translation.getPluginPrefix()).replace("%player%", sender.getName()).replace("&", "§"));
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    Bukkit.broadcastMessage(cfg.getString("quiz_stopped").replace("%prefix%", translation.getPluginPrefix()).replace("%player%", sender.getName()).replace("&", "§"));
                    game.stop();
                } else if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(cfg.getString("quiz_reloaded").replace("%prefix%", translation.getPluginPrefix()).replace("%player%", sender.getName()).replace("&", "§"));
                    fileManager.reloadConfigFiles();
                } else {
                    List<String> helpMessages = cfg.getStringList("help");
                    helpMessages.forEach(message -> {
                        message = message.replace("%prefix%", translation.getPluginPrefix()).replace("%player%", sender.getName()).replace("&", "§");
                        sender.sendMessage(message);
                    });

                }
            } else {
                List<String> helpMessages = cfg.getStringList("help");
                helpMessages.forEach(message -> {
                    message = message.replace("%prefix%", translation.getPluginPrefix()).replace("%player%", sender.getName()).replace("&", "§");
                    sender.sendMessage(message);
                });
            }
        } else {
            sender.sendMessage(cfg.getString("no_permission").replace("%prefix%", translation.getPluginPrefix()).replace("%player%", sender.getName()).replace("&", "§"));
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> complete = new ArrayList();
        if (args.length == 1 && sender.hasPermission("Quiz.complete")) {
            complete.add("start");
            complete.add("stop");
            complete.add("reload");
            return complete;
        }

        return null;
    }
}
