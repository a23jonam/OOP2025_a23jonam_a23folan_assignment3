package assignment3;

import java.util.Random;
public class Worker implements Runnable {
    private int energyLevel;
    private int t; // Energy decrease interval in seconds
    private String location;
    private long lastEnergyDecrease;
    private final String name;
    private Random random = new Random();
    private final CoffeeMachine coffeeMachine;
    private volatile boolean running = true;

    public Worker(String name, CoffeeMachine coffeeMachine) {
        this.energyLevel = random.nextInt(61) + 30;
        this.t = 500 + (random.nextInt(1000) +1);
        this.location = "office";
        this.lastEnergyDecrease = System.currentTimeMillis();
        this.name = name;
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public void run() {
        while (running && energyLevel > 0) {
            double currentTime = System.currentTimeMillis();
            
            if (updateEnergy(currentTime)) {
                if (energyLevel <= 0 && location.equals("coffee_room")) {
                    goHome(currentTime);
                    running = false;
                }
            }

            if (energyLevel > 0) {
                if (needsCoffee()) {
                    goToCoffeeRoom(currentTime);
                }
                if (location.equals("coffee_room") && coffeeMachine.hasCoffee()) {
                    if (coffeeMachine.dispenseCoffee()) {
                        drinkCoffee(currentTime);
                    }
                }
            }

            try {
                Thread.sleep(100); // 100ms steps
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private boolean updateEnergy(double currentTime) {
        if ((currentTime - lastEnergyDecrease) >= t) {
            energyLevel--;
            lastEnergyDecrease = (long)currentTime;
            return true;
        }
        return false;
    }
   // private void timeSwap(long currentTime) {
   // 	double TimeSwap = currentTime * 1000;
    //}

    private boolean needsCoffee() {
        return location.equals("office") && random.nextDouble() < 0.3;
    }

    private void goToCoffeeRoom(double currentTime) {
        location = "coffee_room";
        System.out.println(name + " went to coffee room at " + (currentTime));
    }

    private void drinkCoffee(double currentTime) {
        energyLevel += random.nextInt(21) + 20;
        location = "office";
        System.out.println(name + " drank coffee and returned to office at " + (currentTime));
    }

    private void goHome(double currentTime) {
        location = "home";
        System.out.println(name + " went home due to 0 energy at " + currentTime);
    }

    public void stop() {
        running = false;
    }
}
