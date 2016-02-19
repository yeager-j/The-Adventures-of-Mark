package me.volition.mapObject.placeableObject.impl.furniture;

import me.volition.mapObject.ObjectEvent;
import me.volition.mapObject.placeableObject.PlaceableObject;
import me.volition.util.ImageManager;

/**
 * Created by mccloskeybr on 2/8/16.
 */
public class Couch extends PlaceableObject {
    public Couch(double x, double y) {
        super(ImageManager.getInstance().loadImage("/me/volition/assets/image/objects/furniture/couch.png"), ObjectEvent.NONE, "Couch", "I once gave birth on this beauty.", true, x, y);
    }
}
