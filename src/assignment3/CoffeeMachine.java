package assignment3;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Random;

class CoffeeMachine {
    private int reserve;
    private Random random = new Random();

    public CoffeeMachine() {
        this.reserve = 0;
    }

    public synchronized void replenish(int amount) {
        reserve += amount;
        System.out.println("Coffee machine replenished with " + amount + " cups at " + System.currentTimeMillis()/1000.0);
    }

    public boolean hasCoffee() {
        return reserve > 0;
    }

    public synchronized boolean dispenseCoffee() {
        if (reserve > 0) {
            reserve--;
            return true;
        }
        return false;
    }
}

class Worker {
    private int energyLevel;
    private double t; // Energy decrease interval in seconds
    private String location;
    private double lastEnergyDecrease;
    private final int id;
    private static int idCounter = 0;
    private Random random = new Random();

    public Worker() {
        this.energyLevel = random.nextInt(61) + 30; // 30-90
        this.t = 0.5 + (random.nextDouble() * 1.0); // 0.5-1.5 seconds
        this.location = "office";
        this.lastEnergyDecrease = System.currentTimeMillis() / 1000.0;
        this.id = idCounter++;
    }

    public boolean updateEnergy(double currentTime) {
        if ((currentTime - lastEnergyDecrease) >= t) {
            energyLevel--;
            lastEnergyDecrease = currentTime;
            return true;
        }
        return false;
    }

    public boolean needsCoffee() {
        return location.equals("office") && random.nextDouble() < 0.3;
    }

    public void goToCoffeeRoom(double currentTime) {
        location = "coffee_room";
        System.out.println("Worker " + id + " went to coffee room at " + currentTime);
    }

    public void drinkCoffee(double currentTime) {
        energyLevel += random.nextInt(21) + 20; // 20-40 energy boost
        location = "office";
        System.out.println("Worker " + id + " drank coffee and returned to office at " + currentTime);
    }

    public void goHome(double currentTime) {
        location = "home";
        System.out.println("Worker " + id + " went home due to 0 energy at " + currentTime);
    }

    public String getLocation() {
        return location;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }
}

class CoffeeSimulation {
    private ConcurrentLinkedQueue<Worker> workers;
    private CoffeeMachine coffeeMachine;
    private double simulationTime;
    private Random random = new Random();

    public CoffeeSimulation(int numWorkers, double duration) {
        this.workers = new ConcurrentLinkedQueue<>();
        this.coffeeMachine = new CoffeeMachine();
        this.simulationTime = duration;
        initialize(numWorkers);
    }

    private void initialize(int numWorkers) {
        for (int i = 0; i < numWorkers; i++) {
            workers.add(new Worker());
        }
    }

    public void run() {
        double startTime = System.currentTimeMillis() / 1000.0;
        double currentTime;

        while ((currentTime = System.currentTimeMillis() / 1000.0) - startTime < simulationTime) {
            for (Worker worker : workers) {
                if (worker.updateEnergy(currentTime)) {
                    if (worker.getEnergyLevel() <= 0 && worker.getLocation().equals("coffee_room")) {
                        worker.goHome(currentTime);
                        workers.remove(worker);
                    }
                }

                if (worker.getEnergyLevel() > 0) {
                    if (worker.needsCoffee()) {
                        worker.goToCoffeeRoom(currentTime);
                    }

                    if (worker.getLocation().equals("coffee_room") && coffeeMachine.hasCoffee()) {
                        if (coffeeMachine.dispenseCoffee()) {
                            worker.drinkCoffee(currentTime);
                        }
                    }
                }
            }

            // Random coffee machine replenishment (10% chance)
            if (random.nextDouble() < 0.1) {
                coffeeMachine.replenish(random.nextInt(11) + 5); // 5-15 cups
            }

            try {
                Thread.sleep(100); // 100ms steps
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Simulation completed at " + currentTime + " seconds");
    }

    public static void main(String[] args) {
        // Example usage
        int numWorkers = 5;    // Could be user input
        double duration = 30.0; // Could be user input
        CoffeeSimulation sim = new CoffeeSimulation(numWorkers, duration);
        sim.run();
    }
}
