package de.levin.chatquizbattles.mathquiz;

import de.levin.chatquizbattles.ChatQuizBattles;
import de.levin.chatquizbattles.language.Translation;
import de.levin.chatquizbattles.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuizManager {

    private Game game = new Game();
    private Translation translation = new Translation();
    private Calculation calculation = new Calculation();
    private FileManager fileManager = new FileManager();
    HashMap<Character, Integer> numbers = new HashMap();

    public void startQuiz() {
        startQuiz(false);
    }

    public void startQuiz(Boolean checkForOnlinePlayers) {
        if (Bukkit.getOnlinePlayers().size() >= ChatQuizBattles.instance.getConfig().getInt("PlayerOnlineToStart") | checkForOnlinePlayers) {
            if (game.isActive()) {
                game.stop();

                System.out.println("stopped start quiz");
            }
            game.setActive(true);

            Integer delayInt = calculation.replaceSecondsMinutes("MathQuestionExpireTime");
            System.out.println(delayInt);
            BukkitTask delayedExpire = Bukkit.getScheduler().runTaskTimer(ChatQuizBattles.instance, game::stop, delayInt, 0L);
            game.addTask(Game.TaskName.Expire, delayedExpire);
            FileConfiguration cfg = fileManager.getConfigMath();

            String calculationCalculation = null;
            ArrayList<String> list = new ArrayList<>();
            list.addAll(cfg.getConfigurationSection("Math").getKeys(false));
            String pathMathKey = list.get(calculation.getRandomNumber(0, cfg.getConfigurationSection("Math").getKeys(false).size() - 1));
            pathMathKey = "Math." + pathMathKey;
            String solution = cfg.getString(pathMathKey + ".solution");
            calculationCalculation = solution;
            solution = solution.replaceAll("[^a-zA-Z]", "");

            for (Character charSolutionList : solution.toCharArray()) {
                String randXY = cfg.getString(pathMathKey + ".range" + charSolutionList);
                if (randXY.contains(translation.getTranslationKey())) {
                    String[] args = randXY.split(translation.getTranslationKey());
                    int zahl1 = Integer.parseInt(args[0].trim());
                    int zahl2 = Integer.parseInt(args[1].trim());
                    if (zahl2 > zahl1) {
                        numbers.put(charSolutionList, calculation.getRandomNumber(zahl1, zahl2));
                    } else {
                        numbers.put(charSolutionList, calculation.getRandomNumber(zahl2, zahl1));
                    }

                } else {
                    if (randXY.startsWith("[") && randXY.endsWith("]")) {
                        String[] numbersArray = randXY.replaceAll("\\[|\\]", "").split(",");
                        int index = calculation.getRandomNumber(0, numbersArray.length - 1);
                        Integer integerValue = Integer.valueOf(numbersArray[index].trim());
                        numbers.put(charSolutionList, integerValue);
                    }

                }

                calculationCalculation = calculationCalculation.replace(charSolutionList.toString(), numbers.get(charSolutionList).toString());

            }

            game.setSolveValue(Integer.valueOf(calculation.calcString(calculationCalculation).toString()));
            game.sendGame(cfg, pathMathKey, solution.toCharArray());
        }
    }

    public void setWinner(Player player) {
        game.stop();
        Bukkit.broadcastMessage(fileManager.getConfigMath().getString("playerWonGame").replace("%prefix%", translation.getPluginPrefix()).replace("%player%", player.getName()).replace("&", "§"));
        FileConfiguration cfg = fileManager.getConfigMath();

        ArrayList<String> list = new ArrayList<>();
        list.addAll(cfg.getConfigurationSection("Rewards").getKeys(false));
        String pathMathKey = list.get(calculation.getRandomNumber(0, cfg.getConfigurationSection("Rewards").getKeys(false).size() - 1));
        pathMathKey = "Rewards." + pathMathKey;

        String message = cfg.getString(pathMathKey + ".message")
                .replace("%prefix%", translation.getPluginPrefix());
        String giveMoneyValue = cfg.getString(pathMathKey + ".giveMoney");

        if (giveMoneyValue.contains(translation.getTranslationKey())) {
            String[] args = giveMoneyValue.split(translation.getTranslationKey());
            Integer zahl1 = Integer.valueOf(args[0].trim());
            Integer zahl2 = Integer.valueOf(args[1].trim());
            Number money;
            if (zahl2 > zahl1) {
                money = calculation.getRandomNumber(zahl1, zahl2);
            } else {
                money = calculation.getRandomNumber(zahl2, zahl1);
            }
            ChatQuizBattles.getEcon().withdrawPlayer(player, money.doubleValue());
            message = message.replace("%money%", money.toString());
        } else {
            Number n = Integer.valueOf(giveMoneyValue);
            message = message.replace("%money%", String.valueOf(formatMoney(n)));
            ChatQuizBattles.getEcon().withdrawPlayer(player, n.doubleValue());
        }
//        message = message;

        Object commands = cfg.get(pathMathKey + ".commands");
        ArrayList<String> cmds = new ArrayList<>();
        if (commands instanceof String) {
            cmds.add((String) commands);
        } else if (commands instanceof List<?>) {
            for (Object cmd : (List<?>) commands) {
                if (cmd instanceof String) {
                    cmds.add((String) cmd);
                }
            }
        }
        Bukkit.getScheduler().runTask(ChatQuizBattles.instance, () -> {
            for (String cmd : cmds) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getPlayer().getName()));
            }
        });

        int rawEXP = cfg.getInt(pathMathKey + ".giveExp");
        String giveExpValue = cfg.getString(pathMathKey + ".giveExp");
        if (giveExpValue.contains(translation.getTranslationKey())) {
            String[] args = giveExpValue.split(translation.getTranslationKey());
            Integer zahl1 = Integer.valueOf(args[0].trim());
            Integer zahl2 = Integer.valueOf(args[1].trim());
            int exp;
            if (zahl2 > zahl1) {
                exp = calculation.getRandomNumber(zahl1, zahl2);
            } else {
                exp = calculation.getRandomNumber(zahl2, zahl1);
            }
            player.giveExp(exp);
            message = message.replace("%exp%", String.valueOf(exp));
        } else {
            message = message.replace("%exp%", String.valueOf(rawEXP));
            player.giveExp(rawEXP);
        }

        message = message.replace("&", "§");
        player.sendMessage(message);
    }

    public Number formatMoney(Number money) {
        if (money instanceof Integer) {
            return (Integer) money;
        }
        if (money instanceof Double) {
            return (Double) money;
        }

        return money;
    }
}
