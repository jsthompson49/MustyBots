package org.usfirst.frc.team3407.ems.simulator;

import java.util.Random;

public class SimulatedNumber {
	
	private Random random;
	private double average;
	private double delta;
	
	public SimulatedNumber(double average, double delta) {
		random = new Random();
		this.average = average;
		this.delta = delta;
	}
	
	public double get() {
		return average - (delta / 2) + (random.nextDouble() * delta);
	}

}
