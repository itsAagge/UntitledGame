package Server;

import java.util.*;

public class GameLogic {
    public static HashMap<Integer, ServerPlayer> players = new HashMap<>();

    public static int addPlayer(String name) {
        pair p=getRandomFreePosition();
        ServerPlayer player = new ServerPlayer(name,p,"up");
        players.put(player.getId(), player);
        return player.getId();
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

    public static void updatePlayer(int id, int delta_x, int delta_y, String direction) {
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
                //Gui.movePlayerOnScreen(oldpos,pa,p.direction);
            } else
                player.addPoints(1);
            pair oldpos = player.getLocation();
            pair newpos = new pair(x+delta_x,y+delta_y);
            //Gui.movePlayerOnScreen(oldpos,newpos,direction); // Skal muligvis returnere en ny position, som tråden kan samle op og sende tilbage
            player.setLocation(newpos);
        }
    }

    public static ServerPlayer getPlayerAt(int x, int y) {
        for (ServerPlayer p : players.values()) {
            if (p.getXpos()==x && p.getYpos()==y) {
                return p;
            }
        }
        return null;
    }




}