package me.volition.location;

import me.volition.Main;
import me.volition.Window;
import me.volition.location.tile.Tile;
import me.volition.mapObject.MapObject;
import me.volition.mapObject.ObjectEvent;
import me.volition.mapObject.entity.Entity;
import me.volition.mapObject.entity.Player;
import me.volition.mapObject.placeableObject.PlaceableObject;
import me.volition.state.menu.ingamemenu.game.LoadMenu;
import me.volition.util.BattleManager;
import me.volition.util.GameManager;
import me.volition.util.ImageManager;
import me.volition.util.ObjectManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Demerzel on 2/3/16.
 */

public abstract class Location {

    private String name;
    private ArrayList<Exit> exits;
    private ArrayList<PlaceableObject> placeableObjects;
    private ArrayList<MapObject> perspectiveList;
    private Tile[][] tilemap;
    private BufferedImage bgImage;
    private double bg_x, bg_y, bg_horizOffset, bg_vertOffset;
    private boolean freeCamera, safeRoom;
    private ArrayList<Entity> npcs;


    public Location(String name, boolean safeRoom, boolean freeCamera) {
        exits = new ArrayList<>();
        placeableObjects = new ArrayList<>();
        npcs = new ArrayList<>();

        perspectiveList = new ArrayList<>();

        this.name = name;
        this.freeCamera = freeCamera;
        this.safeRoom = safeRoom; //if false, random tiles can cause battles

        loadMap();
        this.bgImage = ImageManager.makeImageFromMap(this);

        perspectiveList.addAll(placeableObjects);
        perspectiveList.addAll(npcs);

    }

    public double getBg_horizOffset() {
        return bg_horizOffset;
    }

    public double getBg_vertOffset() {
        return bg_vertOffset;
    }

    public boolean hasFreeCamera(){
        return freeCamera;
    }

    public Tile[][] getTilemap(){
        return tilemap;
    }

    public void addExit(Exit exit){
        exits.add(exit);

        int x = (int) (exit.getX() / Tile.TILE_SIZE);
        int y = (int) (exit.getY() / Tile.TILE_SIZE);

        tilemap[y][x].setExit(exit);
    }

    public void addPlaceableObject(PlaceableObject placeableObject){

        placeableObjects.add(placeableObject);

        double x = placeableObject.getX() / Tile.TILE_SIZE;
        double y = placeableObject.getY() / Tile.TILE_SIZE;

        BufferedImage image = placeableObject.getImage();

        if (image != null) {
            int width = image.getWidth() / (Tile.TILE_SIZE / 2) - 1;

            for (int i = width - 1; i >= 0; i--) {
                if (image.getRGB(
                        (i * Tile.TILE_SIZE / 2) + Tile.TILE_SIZE / 2,
                        (i * Tile.TILE_SIZE / 4) + Tile.TILE_SIZE / 2) != 16777215) { //makes sure transp. tiles arent solid

                    tilemap[(int) y][(int) x + (i - 1)].setSolid(placeableObject.isSolid());
                    tilemap[(int) y][(int) x + (i - 1)].setObject(placeableObject);
                }
            }
        }
    }

    public void addNpc(Entity npc){

        npcs.add(npc);

        double x = npc.getX() / Tile.TILE_SIZE;
        double y = npc.getY() / Tile.TILE_SIZE;

        tilemap[(int) y][(int) x].setSolid(true);
        tilemap[(int) y][(int) x].setNpc(npc);
    }

    public void setTilemap(Tile[][] tilemap){
        this.tilemap = tilemap;
    }

    public void update(double delta){

        Player player = GameManager.getInstance().getGameState().getPlayer();

        //collision detection
        int distConst = 10;

        if (tilemap[(int) (player.getY() + distConst) / Tile.TILE_SIZE][((int) player.getX() + player.getWidth()) / Tile.TILE_SIZE].isSolid() ||
                tilemap[(int) (player.getY() + player.getHeight() - distConst) / Tile.TILE_SIZE][((int) player.getX() + player.getWidth()) / Tile.TILE_SIZE].isSolid())
            player.setGoingRight(false);

        else if (tilemap[(int) (player.getY() + distConst) / Tile.TILE_SIZE][(int) player.getX() / Tile.TILE_SIZE].isSolid() ||
                tilemap[((int) player.getY() + player.getHeight() - distConst) / Tile.TILE_SIZE][(int) player.getX() / Tile.TILE_SIZE].isSolid())
            player.setGoingLeft(false);

        if (tilemap[((int) player.getY() + player.getHeight()) / Tile.TILE_SIZE][(int) (player.getX() + distConst) / Tile.TILE_SIZE].isSolid() ||
                tilemap[((int) player.getY() + player.getHeight()) / Tile.TILE_SIZE][((int) player.getX() + player.getWidth() - distConst) / Tile.TILE_SIZE].isSolid())
            player.setGoingDown(false);

        else if (tilemap[(int) player.getY() / Tile.TILE_SIZE][(int) (player.getX() + distConst) / Tile.TILE_SIZE].isSolid() ||
                tilemap[(int) player.getY() / Tile.TILE_SIZE][((int) player.getX() + player.getWidth() - distConst) / Tile.TILE_SIZE].isSolid())
            player.setGoingUp(false);


        Tile playerTile = tilemap[((int) player.getY() + player.getHeight() / 2) / Tile.TILE_SIZE][((int) player.getX() + player.getWidth() / 2) / Tile.TILE_SIZE];

        //random battles
        if (!safeRoom && player.isMoving()){
            if (Math.random() < Math.pow(delta, 2.5)) {

                player.stopMoving();

                player.setAnimator(player.getBattleAnimator());
                BattleManager.startBattle(player, playerTile.getImage());

            }
        }

        //battle tiles
        ArrayList<Entity> entities = playerTile.getBattleEntities();
        if (entities != null) {

            player.stopMoving();

            player.setAnimator(player.getBattleAnimator());
            BattleManager.startBattle(player, entities, playerTile.getImage());

            playerTile.setBattleEntities(null);

        }
        //check if at exit
        Exit exit = playerTile.getExit();
        if (exit != null && exit.isActive()) {

            player.stopMoving();

            GameManager.getInstance().getGameState().setInGameMenu(new LoadMenu());
            Main.getInstance().repaint();

            exit.enter(player);
        }

        //objects closer to camera need to be displayed on top
        determinePerspective();

        //update entity animations
        for (Entity npc: npcs)
            npc.update(delta);

    }

    public void determinePerspective(){

        if (!perspectiveList.contains(GameManager.getInstance().getGameState().getPlayer()))
            perspectiveList.add(GameManager.getInstance().getGameState().getPlayer());

        //bubble sort
        for (int i = 1; i < perspectiveList.size(); i++){
            int j = i;
            while (j > 0 && perspectiveList.get(j).getX() < perspectiveList.get(j - 1).getX()) {

                MapObject temp = perspectiveList.get(j);

                perspectiveList.set(j, perspectiveList.get(j - 1));
                perspectiveList.set(j - 1, temp);

                j--;
            }
        }
    }

    public void enterRoom(){

        Player player = GameManager.getInstance().getGameState().getPlayer();

        loadExits(tilemap);

        //readjusts camera if its a free camera room
        if (freeCamera) {

            bg_x = Window.WINDOW_WIDTH / 2 - (player.getX() + player.getWidth() / 2);
            bg_y = Window.WINDOW_HEIGHT / 2 - (player.getY() + player.getHeight() / 2);

        } else {

            bg_horizOffset = (Window.WINDOW_WIDTH - bgImage.getWidth()) / 2;
            bg_vertOffset = ((Window.WINDOW_HEIGHT - bgImage.getHeight()) / 2) - Tile.TILE_SIZE / 4;
            bg_x += bg_horizOffset;
            bg_y += bg_vertOffset;

        }

        //exit loading screen
        GameManager.getInstance().getGameState().setInGameMenu(null);

    }

    public void adjustCamera(double delta){

        if (freeCamera) {

            Player player = GameManager.getInstance().getGameState().getPlayer();
            double dist = delta * player.getBaseSpeed();

            //move objects if the player is moving
            if (player.isGoingDown()) {

                bg_y -= dist;

                for (Entity npc: npcs)
                    npc.setY(npc.getY() - dist);

            } else if (player.isGoingUp()) {

                bg_y += dist;

                for (Entity npc: npcs)
                    npc.setY(npc.getY() + dist);
            }

            if (player.isGoingLeft()) {

                bg_x += dist;

                for (Entity npc: npcs)
                    npc.setX(npc.getX() + dist);

            } else if (player.isGoingRight()) {

                bg_x -= dist;

                for (Entity npc: npcs)
                    npc.setX(npc.getX() - dist);

            }
        }

    }

    public void inspect(){

        Player player = GameManager.getInstance().getGameState().getPlayer();

        int playerx = (int) (player.getX() + player.getWidth() / 2) / Tile.TILE_SIZE;
        int playery = (int) (player.getY() + player.getHeight() / 2) / Tile.TILE_SIZE;

        Tile inspectTile;

        if (player.isFacingUp())
            inspectTile = tilemap[playery - 1][playerx];

        else if (player.isFacingDown())
            inspectTile = tilemap[playery + 1][playerx];

        else if (player.isFacingRight())
            inspectTile = tilemap[playery][playerx + 1];

        else
            inspectTile = tilemap[playery][playerx - 1];

        Entity npc = inspectTile.getNpc();

        if (npc != null) {

            ObjectManager.onObjectEvent(npc);

        } else {

            PlaceableObject object = inspectTile.getObject();

            if (object != null) {
                inspectTile.getObject().onInspect();
                inspectTile.getObject().setOnInspect(ObjectEvent.NONE);
            }

        }

    }

    public abstract void loadMap();

    public abstract void loadExits(Tile[][] tilemap);

    public void render(Graphics2D g) {

        g.drawImage(bgImage, (int) bg_x, (int) bg_y, null);

        for (int i = 0; i < perspectiveList.size(); i++)
            perspectiveList.get(i).render(g);

    }
}