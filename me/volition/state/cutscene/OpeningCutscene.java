package me.volition.state.cutscene;

import me.volition.state.StateManager;
import me.volition.util.ImageManager;

import java.awt.image.BufferedImage;

/**
 * Created by mccloskeybr on 2/6/16.
 */
public class OpeningCutscene extends CutsceneState {

    public OpeningCutscene() {
        super(new String[]{"Mark was quite the Ian!", "Frame!", "Cunt!", "Muffin!"});
    }

    @Override
    public void finish() {
        StateManager.setCurrentState(StateManager.GAME_INDEX);
    }

    @Override
    public BufferedImage[] loadDialogue_images() {
        ImageManager im = new ImageManager();
        BufferedImage[] images = new BufferedImage[4];

        images[0] = im.loadImage("/me/volition/assets/image/cutscene/opening1.png");
        images[1] = im.loadImage("/me/volition/assets/image/cutscene/opening2.png");
        images[2] = im.loadImage("/me/volition/assets/image/cutscene/opening1.png");
        images[3] = im.loadImage("/me/volition/assets/image/cutscene/opening2.png");

        return images;
    }
}