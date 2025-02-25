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

class Worker implements Runnable {
    private int energyLevel;
    private double t; // Energy decrease interval in seconds
    private String location;
    private double lastEnergyDecrease;
    private final String name;
    private Random random = new Random();
    private final CoffeeMachine coffeeMachine;
    private volatile boolean running = true;

    public Worker(String name, CoffeeMachine coffeeMachine) {
        this.energyLevel = random.nextInt(61) + 30;
        this.t = 0.5 + (random.nextDouble() * 1.0);
        this.location = "office";
        this.lastEnergyDecrease = System.currentTimeMillis() / 1000.0;
        this.name = name;
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public void run() {
        while (running && energyLevel > 0) {
            double currentTime = System.currentTimeMillis() / 1000.0;
            
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
            lastEnergyDecrease = currentTime;
            return true;
        }
        return false;
    }

    private boolean needsCoffee() {
        return location.equals("office") && random.nextDouble() < 0.3;
    }

    private void goToCoffeeRoom(double currentTime) {
        location = "coffee_room";
        System.out.println(name + " went to coffee room at " + currentTime);
    }

    private void drinkCoffee(double currentTime) {
        energyLevel += random.nextInt(21) + 20;
        location = "office";
        System.out.println(name + " drank coffee and returned to office at " + currentTime);
    }

    private void goHome(double currentTime) {
        location = "home";
        System.out.println(name + " went home due to 0 energy at " + currentTime);
    }

    public void stop() {
        running = false;
    }
}

class CoffeeSimulation {
	private ConcurrentLinkedQueue<Worker> workers; // Changed to store Worker instances
    private ConcurrentLinkedQueue<Thread> workerThreads; // Still track threads separately
    private CoffeeMachine coffeeMachine;
    private double simulationTime;
    private Random random = new Random();

    public CoffeeSimulation(double duration) {
        this.workerThreads = new ConcurrentLinkedQueue<>();
        this.coffeeMachine = new CoffeeMachine();
        this.simulationTime = duration;
        initialize();
    }

    private void initialize() {
        String[] names = {"Adrian", "Simon", "Erik", "Nora"};
        for (String name : names) {
            Worker worker = new Worker(name, coffeeMachine);
            workers.add(worker);
            Thread thread = new Thread(worker);
            workerThreads.add(thread);
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        
        
        // Start all worker threads
        for (Thread thread : workerThreads) {
            thread.start();
        }

        // Run simulation for specified duration
        while ((System.currentTimeMillis()) - startTime < simulationTime * 1000) {
            if (random.nextDouble() < 0.1) {
                coffeeMachine.replenish(random.nextInt(11) + 5);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Stop all workers
        for (Worker worker : workers) {
            worker.stop();
        }


        // Wait for all threads to complete
        for (Thread thread : workerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Simulation completed at " + (System.currentTimeMillis() / 1000.0));
    }

    public static void main(String[] args) {
        double duration = 30.0;
        CoffeeSimulation sim = new CoffeeSimulation(duration);
        sim.run();
    }
}