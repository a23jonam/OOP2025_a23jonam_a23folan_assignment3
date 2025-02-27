package assignment3;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CoffeeSim {
    private ConcurrentLinkedQueue<Thread> workerThreads;
    private CoffeeMachine coffeeMachine;
    private double simulationTime;
    private Random random = new Random();

    public CoffeeSim(double duration) {
        this.workerThreads = new ConcurrentLinkedQueue<>();
        this.coffeeMachine = new CoffeeMachine();
        this.simulationTime = duration;
        initialize();
    }

    private void initialize() {
        String[] names = {"Adrian", "Simon", "Erik", "Nora"};
        for (String name : names) {
            Worker worker = new Worker(name, coffeeMachine);
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
        while (System.currentTimeMillis() / 1000.0 - startTime < simulationTime) {
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
        for (Thread thread : workerThreads) {
        	thread.interrupt();
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
}
