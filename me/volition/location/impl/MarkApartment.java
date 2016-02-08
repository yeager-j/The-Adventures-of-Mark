package me.volition.location.impl;

import me.volition.Window;
import me.volition.location.Exit;
import me.volition.location.Location;
import me.volition.location.placeableObject.*;
import me.volition.location.tile.BrickWall;
import me.volition.location.tile.Tile;
import me.volition.location.tile.WoodTile;

import java.util.ArrayList;

/**
 * Created by Demerzel on 2/3/16.
 */
public class MarkApartment extends Location {

    public MarkApartment() {
        super("Mark's Apartment", false);
    }

    @Override
    public Tile[][] loadMap() {
        Tile[][] tileMap = new Tile[Window.WINDOW_HEIGHT / Tile.TILE_SIZE][Window.WINDOW_WIDTH / Tile.TILE_SIZE];

        //set background
        for (int i = 0; i < tileMap.length; i++){
            for (int j = 0; j < tileMap[i].length; j++){
                if (i == 0 || j == 0 || i == tileMap.length - 1 || j == tileMap[i].length - 1)
                    tileMap[i][j] = new BrickWall(j * Tile.TILE_SIZE, i * Tile.TILE_SIZE);
                else
                    tileMap[i][j] = new WoodTile(j * Tile.TILE_SIZE, i * Tile.TILE_SIZE);
            }
        }

        //add solid objects
        addPlaceableObject(new TexasCarpet(tileMap, 5 * Tile.TILE_SIZE, 3 * Tile.TILE_SIZE));
        addPlaceableObject(new PizzaBox(tileMap, Tile.TILE_SIZE, 2 * Tile.TILE_SIZE));
        addPlaceableObject(new PizzaBox(tileMap,  2 * Tile.TILE_SIZE, 6 * Tile.TILE_SIZE));
        addPlaceableObject(new PizzaBox(tileMap, 5 * Tile.TILE_SIZE, 7 * Tile.TILE_SIZE));
        addPlaceableObject(new Bed(tileMap, 8 * Tile.TILE_SIZE, 2 * Tile.TILE_SIZE));
        addPlaceableObject(new Desk(tileMap, 2 * Tile.TILE_SIZE, Tile.TILE_SIZE));
        addPlaceableObject(new Couch(tileMap, 3 * Tile.TILE_SIZE, 7 * Tile.TILE_SIZE));


        return tileMap;
    }

    @Override
    public void loadExits(Tile[][] tileMap){
        addExit(new Exit(tileMap[0].length * Tile.TILE_SIZE - Tile.TILE_SIZE * 2, tileMap.length / 2 * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, new Room(), Tile.TILE_SIZE * 2, 2 * Tile.TILE_SIZE, true));
    }
}
