package com.openlogic.activemq;

public class ProducerRunner {
	public static void main(String[] args) {
		Producer producer = new Producer();
		Thread thread = new Thread(producer);
		thread.start();

		while (producer.isExit() == false) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		producer.close();
		System.out.println("Producer exiting");
	}
}
