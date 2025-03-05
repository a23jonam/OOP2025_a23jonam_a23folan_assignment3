package assignment3;

import java.util.Random;

public class Coffee {
	private static Random random = new Random();

	private int energy;

	private String type;

	public Coffee(String type, int base, int range) {
		this.type = type;
		this.energy = random.nextInt(range) + base + 1;
	}

	public int getEnergy() {
		return this.energy;
	}

	public String getType() {
		return this.type;
	}
}
