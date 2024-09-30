package Server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GameLogic {
    public static HashMap<Integer, ServerPlayer> players = new HashMap<>();

    public synchronized static ServerPlayer addPlayer(String name) {
        pair p = getRandomFreePosition();
        System.out.println("Position to add = x: " + p.x + ", y: " + p.y);
        ServerPlayer player = new ServerPlayer(name,p,"up");
        players.put(player.getId(), player);
        return player;
    }

    public static pair getRandomFreePosition()
    // finds a random new position which is not wall
    // and not occupied by other players
    {
        int x = 1;
        int y = 1;
        boolean foundfreepos = false;
        while  (!foundfreepos) {
            Random r = new Random();
            x = Math.abs(r.nextInt()%18) +1;
            y = Math.abs(r.nextInt()%18) +1;
            if (Generel.board[y].charAt(x)==' ') // er det gulv ?
            {
                foundfreepos = true;
                for (ServerPlayer p: players.values()) {
                    if (p.getXpos()==x && p.getYpos()==y) //pladsen optaget af en anden
                        foundfreepos = false;
                }

            }
        }
        pair p = new pair(x,y);
        return p;
    }

    public synchronized static void updatePlayer(int id, int delta_x, int delta_y, String direction) {
        ServerPlayer player = players.get(id);
        if (player == null) throw new IllegalArgumentException("Player doesn't exist");

        player.direction = direction;
        int x = player.getXpos(),y = player.getYpos();

        if (Generel.board[y+delta_y].charAt(x+delta_x)=='w') {
            player.addPoints(-1);
        }
        else {
            // collision detection
            ServerPlayer p = getPlayerAt(x+delta_x,y+delta_y);
            if (p!=null) {
                player.addPoints(10);
                //update the other player
                p.addPoints(-10);
                pair pa = getRandomFreePosition();
                p.setLocation(pa);
                pair oldpos = new pair(x+delta_x,y+delta_y);

                //Server.sendUpdateToClients(createPlayerMoveJSON(oldpos, pa, direction)); // Send update
                Server.sendUpdateToClients(createGamestateJSON(-1)); // Send gamestate
            } else {
                player.addPoints(1);
                pair oldpos = player.getLocation();
                pair newpos = new pair(x + delta_x, y + delta_y);
                player.setLocation(newpos);

                //Server.sendUpdateToClients(createPlayerMoveJSON(oldpos, newpos, direction)); // Send update
                Server.sendUpdateToClients(createGamestateJSON(-1)); // Send gamestate
            }
        }
    }

    public synchronized static void shoot(int id) {
        ServerPlayer player = players.get(id);
        if (player == null) throw new IllegalArgumentException("Player doesn't exist");

        int x = player.getXpos(), y = player.getYpos();
        String direction = player.getDirection();

        int checkAt = 0;
        boolean hitWallOrPlayer = false;
        ServerPlayer playerHit = null;

        if (direction.equals("up")) {
            checkAt = y - 1;
            while (!hitWallOrPlayer && checkAt >= 1) {
                if (getPlayerAt(x, checkAt) != null) {
                    hitWallOrPlayer = true;
                    playerHit = getPlayerAt(x, checkAt);
                } else if (Generel.board[checkAt].charAt(x) == 'w') {
                    hitWallOrPlayer = true;
                } else {
                    checkAt--;
                }
            }
        } else if (direction.equals("down")) {
            checkAt = y + 1;
            while (!hitWallOrPlayer && checkAt <= 19) {
                if (getPlayerAt(x, checkAt) != null) {
                    hitWallOrPlayer = true;
                    playerHit = getPlayerAt(x, checkAt);
                } else if (Generel.board[checkAt].charAt(x) == 'w') {
                    hitWallOrPlayer = true;
                } else {
                    checkAt++;
                }
            }
        } else if (direction.equals("right")) {
            checkAt = x + 1;
            while (!hitWallOrPlayer && checkAt <= 19) {
                if (getPlayerAt(checkAt, y) != null) {
                    hitWallOrPlayer = true;
                    playerHit = getPlayerAt(checkAt, y);
                } else if (Generel.board[x].charAt(checkAt) == 'w') {
                    hitWallOrPlayer = true;
                } else {
                    checkAt++;
                }
            }
        } else if (direction.equals("left")) {
            checkAt = x - 1;
            while (!hitWallOrPlayer && checkAt >= 1) {
                if (getPlayerAt(checkAt, y) != null) {
                    hitWallOrPlayer = true;
                    playerHit = getPlayerAt(checkAt, y);
                } else if (Generel.board[x].charAt(checkAt) == 'w') {
                    hitWallOrPlayer = true;
                } else {
                    checkAt--;
                }
            }
        }
        if (playerHit != null) {
            player.addPoints(50);
            playerHit.addPoints(-50);

            pair pa = getRandomFreePosition();
            playerHit.setLocation(pa);
        }
        Server.sendUpdateToClients(createGamestateJSON(id));
        System.out.println("Shooting");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Server.sendUpdateToClients(createGamestateJSON(-1));
        System.out.println("Done");
    }

    public static ServerPlayer getPlayerAt(int x, int y) {
        for (ServerPlayer p : players.values()) {
            if (p.getXpos()==x && p.getYpos()==y) {
                return p;
            }
        }
        return null;
    }

    /*
    public static JSONObject createPlayerMoveJSON(pair oldpos, pair newpos, String direction) { // Send update
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Update");
        jsonObject.put("UpdateType", "PlayerMoved");
        jsonObject.put("PlayerOldXPos", oldpos.x);
        jsonObject.put("PlayerOldYPos", oldpos.y);
        jsonObject.put("PlayerNewXPos", newpos.x);
        jsonObject.put("PlayerNewYPos", newpos.y);
        jsonObject.put("PlayerDirection", direction);

        return jsonObject;
    }

    public static JSONObject createPlayerAddedJSON(pair pos, String direction) { // Send update
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Update");
        jsonObject.put("UpdateType", "PlayerAdded");
        jsonObject.put("PlayerXPos", pos.x);
        jsonObject.put("PlayerYPos", pos.y);
        jsonObject.put("PlayerDirection", direction);

        return jsonObject;
    }
     */

    public static JSONObject createGamestateJSON(int shooterId) { // Send gamestate
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Update");
        jsonObject.put("UpdateType", "Gamestate");

        //JSON Array for the players
        JSONArray jsonArray = new JSONArray();
        for (ServerPlayer player : players.values()) {
            JSONObject jsonPlayer = new JSONObject();
            jsonPlayer.put("PlayerXPos", player.getXpos());
            jsonPlayer.put("PlayerYPos", player.getYpos());
            jsonPlayer.put("PlayerDirection", player.getDirection());
            boolean isShooting = player.getId() == shooterId;
            jsonPlayer.put("PlayerShooting", isShooting);
            jsonArray.put(jsonPlayer);
        }
        jsonObject.put("PlayerArray", jsonArray);

        return jsonObject;
    }


}