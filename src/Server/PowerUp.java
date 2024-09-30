package Server;

import static Server.GameLogic.getRandomFreePosition;

public class PowerUp {
    String name;
    pair location;


    public PowerUp(String name, pair location) {
        this.name = name;
        this.location = location;
    }

    public static PowerUp randomPowerUp (){
        double randomNumber = Math.floor((Math.random() * 10) + 1);
        pair pa = getRandomFreePosition();
        if (randomNumber == 1) {
            return new PowerUp("Plus 10 points", pa);
        } else if (randomNumber == 2){
            return new PowerUp("Teleport random", pa);
        } else if (randomNumber == 3) {
            return new PowerUp("Andrew går ned på alle 4", pa);
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public pair getLocation() {
        return location;
    }
    public int getXpos () {
        return location.getX();
    }
    public int getYpos () {
        return location.getY();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(pair location) {
        this.location = location;
    }
}
