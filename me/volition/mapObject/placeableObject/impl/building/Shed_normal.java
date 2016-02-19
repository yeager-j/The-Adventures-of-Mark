package me.volition.mapObject.placeableObject.impl.building;

import me.volition.mapObject.ObjectEvent;
import me.volition.mapObject.placeableObject.PlaceableObject;
import me.volition.util.ImageManager;

/**
 * Created by mccloskeybr on 2/15/16.
 */
public class Shed_normal extends PlaceableObject {
    public Shed_normal(double x, double y) {
        super(ImageManager.getInstance().loadImage("/me/volition/assets/image/objects/buildings/shed_1.png"), ObjectEvent.NONE, "Shed", "A some-what large wooden person container.", true, x, y);
    }
}