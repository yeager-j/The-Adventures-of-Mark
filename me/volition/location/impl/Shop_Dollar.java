package me.volition.location.impl;

import me.volition.location.Exit;
import me.volition.location.Location;
import me.volition.location.placeableObject.PlaceableObject;
import me.volition.location.tile.Tile;
import me.volition.location.tile.WoodFloor;
import me.volition.location.tile.WoodWall;
import me.volition.util.ImageManager;

import java.util.ArrayList;

/**
 * Created by mccloskeybr on 2/17/16.
 */
public class Shop_Dollar extends Location {

    public Shop_Dollar() {
        super("The Dollar Store", true, false);
    }

    @Override
    public void loadMap() {

        ArrayList<Class<? extends Tile>> tiles = new ArrayList<>();
        tiles.add(WoodWall.class);
        tiles.add(WoodFloor.class);
        tiles.add(WoodFloor.class);

        ArrayList<Class<? extends PlaceableObject>> objects = new ArrayList<>();

        ImageManager.loadMapFromImage(this, ImageManager.getInstance().loadImage("/me/volition/assets/image/rooms/shop_dollar.png"), tiles, objects);
    }

    @Override
    public void loadExits(Tile[][] tilemap) {
        addExit(new Exit(
                tilemap,
                6 * Tile.TILE_SIZE, 8 * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE,
                Midtown.class, 16 * Tile.TILE_SIZE, 29 * Tile.TILE_SIZE, true
        ));
    }
}
