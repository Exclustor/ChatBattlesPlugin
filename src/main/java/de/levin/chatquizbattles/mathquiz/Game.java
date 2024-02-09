package de.levin.chatquizbattles.mathquiz;

import de.levin.chatquizbattles.ChatQuizBattles;
import de.levin.chatquizbattles.language.Translation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.List;

public class Game {

    private boolean active;
    private HashMap<TaskName, BukkitTask> tasks = new HashMap<>();
    private Integer solveValue;
    private Player winner;
    private Translation translation = new Translation();
    private HashMap<Character, Integer> numbers = new HashMap<>();
    private int delayUntilStart;
    public enum TaskName {
        Expire,
        Start
    }

    public void start() {
        active = true;
        QuizManager quizManager = new QuizManager();
        quizManager.startQuiz();
        BukkitTask expireTask = getTask(TaskName.Expire);
        if (expireTask != null) {
            expireTask.cancel();
        }
    }

    public void stop() {
        active = false;
        BukkitTask expireTask = getTask(TaskName.Expire);
        if (expireTask != null) {
            expireTask.cancel();
        }
    }

    public void addTask(TaskName taskName, BukkitTask task) {
        if (task != null) {
            tasks.put(taskName, task);
        } else {
            throw new IllegalArgumentException(taskName + " konnte nicht zu den Tasks hinzugefügt werden, da die BukkitTask null ist!");
        }
    }

    public BukkitTask getTask(TaskName taskName) {
        return tasks.get(taskName);
    }

    public HashMap<TaskName, BukkitTask> getTasks() {
        return tasks;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getSolveValue() {
        return solveValue;
    }

    public void setSolveValue(Integer solveValue) {
        this.solveValue = solveValue;
    }

    public void sendGame(FileConfiguration cfg, String pathMathKey, char[] charSolutionList) {
        List<String> list = (List<String>) cfg.getList(pathMathKey + ".message");
        for (String s : list) {
            s = s.replace("%prefix%", translation.getPluginPrefix()).replace("&", "§");
            for (char c : charSolutionList) {
                s = s.replaceAll("%" + c + "%", numbers.get(c).toString());
            }
            Bukkit.broadcastMessage(s);
        }
    }

    public Integer getDelayUntilStart(){
        return delayUntilStart;
    }

    public void setDelayUntilStart(Integer newInt){
        this.delayUntilStart = newInt;
    }

    public void setup(){
        Calculation calculation = new Calculation();
        setDelayUntilStart(calculation.replaceSecondsMinutes("QuizTimer"));
//        setDelayUntilStart(Integer.valueOf(ChatQuizBattles.instance.getConfig().getString("QuizTimer").replace("%prefix%", translation.getPluginPrefix()).replace("&", "§")));

        System.out.println(delayUntilStart);

        BukkitTask delayedStart = Bukkit.getScheduler().runTaskTimer(ChatQuizBattles.instance, this::start, getDelayUntilStart(), getDelayUntilStart());
        addTask(Game.TaskName.Start, delayedStart);
    }

    public void setWinner(Player player){
        QuizManager quizManager = new QuizManager();
        quizManager.setWinner(player);
        this.winner = player.getPlayer();
    }

    public Player getWinner(){
        return winner;
    }

}
