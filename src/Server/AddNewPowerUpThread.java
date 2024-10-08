package Server;

public class AddNewPowerUpThread extends Thread {
    public void run() {
        double r = Math.random() * 10000;
        try {
            Thread.sleep((long) r);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        GameLogic.newPowerUp();
        Server.sendUpdateToClients(GameLogic.createGamestateJSON(-1, false));
    }
}
