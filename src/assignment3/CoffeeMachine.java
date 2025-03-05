package assignment3;


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