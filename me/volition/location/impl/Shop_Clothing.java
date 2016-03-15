package me.volition.location.impl;

import me.volition.location.Location;
import me.volition.mapObject.entity.shopkeepers.Peppito;
import me.volition.mapObject.placeableObject.PlaceableObject;
import me.volition.location.tile.Tile;
import me.volition.location.tile.WoodFloor;
import me.volition.location.tile.WoodWall;
import me.volition.state.menu.ingamemenu.game.ShopMenu;
import me.volition.util.ImageManager;

import java.util.ArrayList;

/**
 * Created by mccloskeybr on 2/17/16.
 */
public class Shop_Clothing extends Location{

    public Shop_Clothing() {
        super("The GAP", true);
    }

    @Override
    public void loadMap() {

        ArrayList<Class<? extends Tile>> tiles = new ArrayList<>();
        tiles.add(WoodWall.class);
        tiles.add(WoodFloor.class);
        tiles.add(WoodFloor.class);

        ArrayList<Class<? extends PlaceableObject>> objects = new ArrayList<>();

        ImageManager.loadMapFromImage(this, ImageManager.getInstance().loadImage("/me/volition/assets/image/rooms/shop_clothing.png"), tiles, objects);

        addNpc(new Peppito(5 * Tile.TILE_SIZE, 2 * Tile.TILE_SIZE));

    }

    @Override
    public void loadExits(Tile[][] tilemap) {
    }

    @Override
    public void enterRoom(){
        super.enterRoom();

        ShopMenu.generateRandomItems(1);
    }

}
