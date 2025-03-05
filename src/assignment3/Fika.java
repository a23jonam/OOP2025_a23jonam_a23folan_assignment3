package assignment3;
import java.util.concurrent.ConcurrentLinkedQueue;
public class Fika {

    public static void main(String[] args) {
        long duration = 3000;
        CoffeeSim sim = new CoffeeSim(duration);
        sim.run();
    }
}