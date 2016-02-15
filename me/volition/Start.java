package me.volition;

import me.volition.state.StateManager;
import me.volition.state.menu.impl.MainMenu;
import me.volition.util.FontManager;
import me.volition.util.GameManager;
import me.volition.util.ImageManager;

/**
 * Created by Demerzel on 2/3/16.
 */
public class Start {
    public static void main(String[] args) {

        new FontManager().registerFont("/me/volition/assets/font/DTM-Sans.otf");
        new FontManager().registerFont("/me/volition/assets/font/DTM-Mono.otf");

        //StateManager.setCurrentState(new MainMenu());
        StateManager.setCurrentState(GameManager.getInstance().getGameState());

        new Main();

    }
}
