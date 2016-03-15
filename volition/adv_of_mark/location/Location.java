package volition.adv_of_mark.location;

import volition.adv_of_mark.Main;
import volition.adv_of_mark.Window;
import volition.adv_of_mark.location.tile.Tile;
import volition.adv_of_mark.mapObject.MapObject;
import volition.adv_of_mark.mapObject.ObjectEvent;
import volition.adv_of_mark.mapObject.entity.Entity;
import volition.adv_of_mark.mapObject.entity.Player;
import volition.adv_of_mark.mapObject.placeableObject.PlaceableObject;
import volition.adv_of_mark.state.menu.ingamemenu.game.LoadMenu;
import volition.adv_of_mark.util.BattleManager;
import volition.adv_of_mark.util.GameManager;
import volition.adv_of_mark.util.ImageManager;
import volition.adv_of_mark.util.ObjectManager;

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
    private boolean safeRoom;
    private ArrayList<Entity> npcs;

    private static final int DIST_CONST = 15; //for collision detection


    public Location(String name, boolean safeRoom) {
        exits = new ArrayList<>();
        placeableObjects = new ArrayList<>();
        npcs = new ArrayList<>();

        perspectiveList = new ArrayList<>();

        this.name = name;
        this.safeRoom = safeRoom; //if false, random tiles can cause battles

        loadMap();
        this.bgImage = ImageManager.makeImageFromMap(getTilemap());

        perspectiveList.addAll(placeableObjects);
        perspectiveList.addAll(npcs);

    }

    public double getBg_horizOffset() {
        return bg_horizOffset;
    }

    public double getBg_vertOffset() {
        return bg_vertOffset;
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
            int width = placeableObject.getWidth() / Tile.TILE_SIZE;
            int length = placeableObject.getLength() / Tile.TILE_SIZE;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < length; j++){
                    tilemap[(int) y + j][(int) x + i].setSolid(placeableObject.isSolid());
                    tilemap[(int) y + j][(int) x + i].setObject(placeableObject);
                }
            }
        }
    }

    public void addNpc(Entity npc){

        npcs.add(npc);

        double x = npc.getX();
        double y = npc.getY();

        tilemap[(int) y][(int) x].setSolid(true);
        tilemap[(int) y][(int) x].setNpc(npc);
    }

    public void setTilemap(Tile[][] tilemap){
        this.tilemap = tilemap;
    }

    public void update(double delta){

        Player player = GameManager.getInstance().getGameState().getPlayer();

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

        bg_horizOffset = (Window.WINDOW_WIDTH - bgImage.getWidth()) / 2;
        bg_vertOffset = ((Window.WINDOW_HEIGHT - bgImage.getHeight()) / 2) - Tile.TILE_SIZE / 4;
        bg_x += bg_horizOffset;
        bg_y += bg_vertOffset;

        //exit loading screen
        GameManager.getInstance().getGameState().setInGameMenu(null);

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

    public boolean ableMoveUp(){
        Player player = GameManager.getInstance().getGameState().getPlayer();

        if (tilemap[(int) (player.getY() / Tile.TILE_SIZE)][(int) (player.getX() + DIST_CONST) / Tile.TILE_SIZE].isSolid() ||
                tilemap[(int) (player.getY() / Tile.TILE_SIZE)][(int) ((player.getX() + player.getWidth() - DIST_CONST) / Tile.TILE_SIZE)].isSolid())
            return false;

        return true;
    }

    public boolean ableMoveDown(){
        Player player = GameManager.getInstance().getGameState().getPlayer();

        if (tilemap[(int) ((player.getY() + player.getHeight()) / Tile.TILE_SIZE)][(int) ((player.getX() + DIST_CONST) / Tile.TILE_SIZE)].isSolid() ||
                tilemap[(int) ((player.getY() + player.getHeight()) / Tile.TILE_SIZE)][(int) ((player.getX() + player.getWidth() - DIST_CONST) / Tile.TILE_SIZE)].isSolid())
            return false;

        return true;
    }

    public boolean ableMoveLeft(){
        Player player = GameManager.getInstance().getGameState().getPlayer();

        if (tilemap[(int) ((player.getY() + DIST_CONST) / Tile.TILE_SIZE)][(int) (player.getX() / Tile.TILE_SIZE)].isSolid() ||
                tilemap[(int) ((player.getY() + player.getHeight() - DIST_CONST) / Tile.TILE_SIZE)][(int) (player.getX() / Tile.TILE_SIZE)].isSolid())
            return false;

        return true;
    }

    public boolean ableMoveRight(){
        Player player = GameManager.getInstance().getGameState().getPlayer();

        if (tilemap[(int) (player.getY() / Tile.TILE_SIZE)][(int) ((player.getX() + player.getWidth()) / Tile.TILE_SIZE)].isSolid() ||
                tilemap[(int) ((player.getY() + player.getHeight() - DIST_CONST) / Tile.TILE_SIZE)][(int) ((player.getX() + player.getWidth()) / Tile.TILE_SIZE)].isSolid())
            return false;

        return true;
    }

    public abstract void loadMap();

    public abstract void loadExits(Tile[][] tilemap);

    public void render(Graphics2D g) {

        g.drawImage(bgImage, (int) bg_x, (int) bg_y, null);

        for (int i = 0; i < perspectiveList.size(); i++)
            perspectiveList.get(i).render(g);

    }
}