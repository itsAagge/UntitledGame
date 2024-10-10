package Server;

public class NPC {
    private pair location;
    private String direction;
    private boolean isAlive;

    public NPC(pair location) {
        this.location = location;
        this.direction = "down";
        this.isAlive = true;
    }

    public pair getLocation() {
        return location;
    }

    public void setLocation(pair location) {
        this.location = location;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void kill() {
        this.isAlive = false;
    }
}
