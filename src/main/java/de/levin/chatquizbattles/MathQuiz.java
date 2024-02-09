package de.levin.chatquizbattles;

import de.levin.chatquizbattles.mathquiz.Calculation;
import de.levin.chatquizbattles.mathquiz.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MathQuiz implements Listener {

    private Calculation calculation = new Calculation();

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        Game game = new Game();
        if (game.isActive()) {
            try {
                Number message = calculation.parseString(e.getMessage());
                if (message != null && Double.valueOf(message.toString()).equals(Double.valueOf(game.getSolveValue()))) {
                    game.setWinner(e.getPlayer());
                } else {
                }
            } catch (NumberFormatException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
