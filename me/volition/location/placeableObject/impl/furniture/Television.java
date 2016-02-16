package me.volition.location.placeableObject.impl.furniture;

import me.volition.location.placeableObject.ObjectEvent;
import me.volition.location.placeableObject.PlaceableObject;
import me.volition.location.tile.Tile;
import me.volition.util.ImageManager;


/**
 * Created by mccloskeybr on 2/10/16.
 */
public class Television extends PlaceableObject {
    public Television(Tile[][] tileMap, double x, double y) {
        super(ImageManager.getInstance().loadImage("/me/volition/assets/image/objects/furniture/tv.png"), tileMap, ObjectEvent.NONE, "A good ol' Television", "Your least favorite show is on.", true, x, y);
    }
}