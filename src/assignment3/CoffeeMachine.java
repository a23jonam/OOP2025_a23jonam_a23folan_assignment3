package assignment3;

import java.util.Random;

class CoffeeMachine {
	private int reserve;

	private Random random = new Random();

	public CoffeeMachine() {
		this.reserve = 0;
	}

	void makeCoffee() {
		int type = random.nextInt(3);

		switch (type) {
		case 0:
			new BlackCoffee();
		case 1:
			new Cappuccino();
		case 2:
			new Latte();
		}

	}

	public synchronized void coffeeReserve(int amount) {
		while (reserve <= 20) {
			try {
				Thread.sleep(2000);
				reserve++;
				System.out.println("Added one cup to the reserve. " + reserve + "/20");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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