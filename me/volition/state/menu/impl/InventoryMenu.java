package me.volition.state.menu.impl;

import me.volition.*;
import me.volition.Window;
import me.volition.entity.Player;
import me.volition.state.StateManager;
import me.volition.state.menu.LeftTextMenu;
import me.volition.util.GameManager;
import me.volition.util.RenderUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.awt.event.KeyEvent;


/**
 * Created by mccloskeybr on 2/12/16.
 */
public class InventoryMenu extends LeftTextMenu {
    private Player player;

    public InventoryMenu(Player player) {
        super(null, ArrayUtils.addAll(new String[]{"Go back"}, player.getInventory_strarr()), Color.WHITE, Color.RED);
        this.player = player;
    }

    @Override
    public void select(int index) {
        if (index == 0)
            StateManager.setCurrentState(GameManager.getGameState());
        else {
            player.useItem(index - 1);
            StateManager.setCurrentState(new InventoryMenu(player));
        }

    }

    @Override
    public void render(Graphics g){
        super.render(g);

        RenderUtils.drawOutlinedBox(g, 200, 30, Window.WINDOW_WIDTH - 250, Window.WINDOW_HEIGHT - 60);
        if (getCurrentIndex() != 0)
            player.getInventory().get(getCurrentIndex() - 1).renderDescription(g);
    }
}
