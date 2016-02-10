package me.volition.state.menu.ingamemenu.game;

import me.volition.Window;
import me.volition.state.menu.ingamemenu.InGameMenu;
import me.volition.util.GameManager;
import me.volition.util.RenderUtils;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by mccloskeybr on 2/9/2016.
 */
public class PauseMenu implements InGameMenu {

    private String[] options = {
            "Continue",
            "Inventory",
            "Status",
            "Quit"
    };
    private int currentIndex;
    private boolean hasSelected;

    public PauseMenu() {
    }

    @Override
    public void update() {
        if (currentIndex < 0)
            currentIndex = options.length - 1;
        else if (currentIndex >= options.length)
            currentIndex = 0;

        if (hasSelected) {
            hasSelected = false;
            select(currentIndex);
        }
    }

    @Override
    public void select(int currentIndex) {
        if (currentIndex == 0)
            GameManager.getGameState().setPauseMenu(null);
        else if (currentIndex == 1) {

        } else if (currentIndex == 2) {

        } else
            System.exit(0);
    }

    @Override
    public void keyPressed(int k) {
        if (k == KeyEvent.VK_D)
            currentIndex++;
        else if (k == KeyEvent.VK_A)
            currentIndex--;
        else if (k == KeyEvent.VK_ENTER)
            hasSelected = true;
        else if (k == KeyEvent.VK_ESCAPE)
            select(0);
    }

    @Override
    public void render(Graphics g) {
        RenderUtils.drawOutlinedBox(g, 0,  4 * Window.WINDOW_HEIGHT / 5, Window.WINDOW_WIDTH - 5, Window.WINDOW_HEIGHT / 5 - 5);
        g.setFont(new Font("Determination Sans", Font.PLAIN, 14));

        for (int i = 0; i < options.length; i++) {
            if (i == currentIndex)
                g.setColor(Color.RED);
            else
                g.setColor(Color.WHITE);

            g.drawString(options[i], 100 + 100 * i, me.volition.Window.WINDOW_HEIGHT - 75);
        }
    }
}