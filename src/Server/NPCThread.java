package Server;

public class NPCThread extends Thread {
    private NPC npc;

    public NPCThread(NPC npc) {
        this.npc = npc;
    }

    public void run() {
        while (npc.isAlive()) {
            double r = Math.random() * 5000;
            try {
                Thread.sleep((long) r);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean[] surroundings = GameLogic.getNPCSurroundings();
            boolean choosefreepos = false;
            int nextDirection = 0;
            while (!choosefreepos) {
                double way = Math.random() * 4;
                if (surroundings[(int) way]) {
                    choosefreepos = true;
                    nextDirection = (int) way;
                }
            }
            switch (nextDirection) {
                case 0 -> GameLogic.moveNPC(0,-1,"up");
                case 1 -> GameLogic.moveNPC(1,0,"right");
                case 2 -> GameLogic.moveNPC(0,1,"down");
                case 3 -> GameLogic.moveNPC(-1,0,"left");
            }
        }





        double r = Math.random() * 10000;
        try {
            Thread.sleep((long) r);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        GameLogic.addNPC();
    }
}
