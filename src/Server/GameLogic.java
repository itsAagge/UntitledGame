package Server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.*;

public class GameLogic {
    public static HashMap<Integer, ServerPlayer> players = new HashMap<>();
    public static ArrayList<PowerUp> powerUpList = new ArrayList<>();

    public synchronized static ServerPlayer addPlayer(String name) {
        pair p = getRandomFreePosition();
        System.out.println("Position to add = x: " + p.x + ", y: " + p.y);
        ServerPlayer player = new ServerPlayer(name, p, "up");
        players.put(player.getId(), player);
        addPowerUp();
        return player;
    }

    public static void addPowerUp () {
        pair p = getRandomFreePosition();
        PowerUp powerUp = PowerUp.randomPowerUp();
        powerUp.setLocation(p);
        powerUpList.add(powerUp);
    }

    public static pair getRandomFreePosition()
    // finds a random new position which is not wall
    // and not occupied by other players
    {
        int x = 1;
        int y = 1;
        boolean foundfreepos = false;
        while (!foundfreepos) {
            Random r = new Random();
            x = Math.abs(r.nextInt() % 18) + 1;
            y = Math.abs(r.nextInt() % 18) + 1;
            if (Generel.board[y].charAt(x) == ' ') // er det gulv ?
            {
                foundfreepos = true;
                for (ServerPlayer p : players.values()) {
                    if (p.getXpos() == x && p.getYpos() == y) //pladsen optaget af en anden
                        foundfreepos = false;
                }
                for (PowerUp p : powerUpList) {
                    if (p.getXpos() == x && p.getYpos() == y) {
                        foundfreepos = false;
                    }
                }

            }
        }
        pair p = new pair(x, y);
        return p;
    }

    public synchronized static void updatePlayer(int id, int delta_x, int delta_y, String direction) {
        ServerPlayer player = players.get(id);
        if (player == null) throw new IllegalArgumentException("Player doesn't exist");

        player.direction = direction;
        if (player.getPowerUpTime().isAfter(LocalDateTime.now())) {
            if (player.getPowerUpType().equals("Reverse controls")) {
                player.direction = reverseDirection(direction);
                delta_x = reverseDelta(delta_x);
                delta_y = reverseDelta(delta_y);
            } else if (player.getPowerUpType().equals("Double speed")) {
             delta_x = delta_x * 2;
             delta_y = delta_y * 2;
            }
        }
        int x = player.getXpos(), y = player.getYpos();

        if (Generel.board[y + delta_y].charAt(x + delta_x) == 'w') {
            //player.addPoints(-1);
        } else {
            // collision detection
            ServerPlayer p = getPlayerAt(x + delta_x, y + delta_y);
            PowerUp pUp = getPowerUpAt(x + delta_x, y + delta_y);
            if (p != null) {
                player.addPoints(14);
                //update the other player
                p.addPoints(-7);
                pair pa = getRandomFreePosition();
                p.setLocation(pa);
                pair newpos = new pair(x + delta_x, y + delta_y);
                player.setLocation(newpos);
                Server.sendUpdateToClients(createGamestateJSON(-1, false));
            } else if (pUp != null) {
                if (pUp.getName().equals("Plus 10 points")) {
                    player.addPoints(10);
                } else if (pUp.getName().equals("Teleport random")) {
                    pair pa = getRandomFreePosition();
                    player.setLocation(pa);
                } else if (pUp.getName().equals("Reverse controls")) {
                    player.setPowerUpTime(LocalDateTime.now().plusSeconds(15));
                    player.setPowerUpType("Reverse controls");
                } else if (pUp.getName().equals("Double speed")) {
                    player.setPowerUpTime(LocalDateTime.now().plusSeconds(15));
                    player.setPowerUpType("Double speed");
                } else if (pUp.getName().equals("Star shooting")) {
                    player.setPowerUpTime(LocalDateTime.now().plusSeconds(15));
                    player.setPowerUpType("Star shooting");
                }
                pair newpos = new pair(x + delta_x, y + delta_y);
                player.setLocation(newpos);
                Server.sendUpdateToClients(createGamestateJSON(-1, false));
            } else {
                pair newpos = new pair(x + delta_x, y + delta_y);
                player.setLocation(newpos);
                Server.sendUpdateToClients(createGamestateJSON(-1, false));
            }
        }
    }

    public synchronized static void shoot(int id) {
        ServerPlayer player = players.get(id);
        if (player == null) throw new IllegalArgumentException("Player doesn't exist");

        int x = player.getXpos(), y = player.getYpos();
        String direction = player.getDirection();

        boolean starShootingActive = false;
        if (player.getPowerUpType().equals("Star shooting") && player.getPowerUpTime().isAfter(LocalDateTime.now())) {
            starShootingActive = true;
        }

        int checkAt = 0;
        boolean hitWallOrPlayer = false;
        ServerPlayer playerHit = null;

        if (direction.equals("up") || starShootingActive) {
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
        }
        if (direction.equals("down") || starShootingActive) {
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
        }
        if (direction.equals("right") || starShootingActive) {
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
        }
        if (direction.equals("left") || starShootingActive) {
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
            player.addPoints(10);
            playerHit.addPoints(-5);

            pair pa = getRandomFreePosition();
            playerHit.setLocation(pa);
        }
        Server.sendUpdateToClients(createGamestateJSON(id, starShootingActive));
        System.out.println("Shooting");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Server.sendUpdateToClients(createGamestateJSON(-1, false));
        System.out.println("Done");
    }

    public static int reverseDelta(int delta) {
        int newDelta = 0;
        if (delta == 1) {
            newDelta = -1;
        } else if (delta == -1) {
            newDelta = 1;
        }
        return delta;
    }

    public static String reverseDirection(String direction) {
        String newDirection = "";
        if (direction.equals("up")) {
            newDirection = "down";
        } else if (direction.equals("down")) {
            newDirection = "up";
        } else if (direction.equals("left")) {
            newDirection = ("right");
        } else if (direction.equals("right")) {
            newDirection = "left";
        }
        return newDirection;
    }

    public static ServerPlayer getPlayerAt(int x, int y) {
        for (ServerPlayer p : players.values()) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    public static PowerUp getPowerUpAt(int x, int y) {
        for (PowerUp p : powerUpList) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    public static JSONObject createGamestateJSON(int shooterId, boolean starShootingActive) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Update");
        jsonObject.put("UpdateType", "Gamestate");

        //JSON Array for the players
        JSONArray jsonPlayerArray = new JSONArray();
        for (ServerPlayer player : players.values()) {
            JSONObject jsonPlayer = new JSONObject();
            jsonPlayer.put("PlayerXPos", player.getXpos());
            jsonPlayer.put("PlayerYPos", player.getYpos());
            jsonPlayer.put("PlayerDirection", player.getDirection());
            boolean isShooting = player.getId() == shooterId;
            jsonPlayer.put("PlayerShooting", isShooting);
            if (isShooting) jsonPlayer.put("StarShootingActive", starShootingActive);
            jsonPlayerArray.put(jsonPlayer);
        }
        jsonObject.put("PlayerArray", jsonPlayerArray);

        //JSON Array for the points
        JSONArray jsonPointArray = new JSONArray();
        for (ServerPlayer player : players.values()) {
            JSONObject jsonPoint = new JSONObject();
            jsonPoint.put("PlayerName", player.getName());
            jsonPoint.put("PlayerPoints", player.getPoints());
            jsonPointArray.put(jsonPoint);
        }
        jsonObject.put("PointArray", jsonPointArray);

        JSONArray jsonPowerUpArray = new JSONArray();
        for (PowerUp powerUp : powerUpList) {
            JSONObject JsonPowerUp = new JSONObject();
            JsonPowerUp.put("X", powerUp.getXpos());
            JsonPowerUp.put("Y", powerUp.getYpos());
            jsonPowerUpArray.put(JsonPowerUp);
        }
        jsonObject.put("PowerUpArray", jsonPowerUpArray);

        return jsonObject;
    }


}