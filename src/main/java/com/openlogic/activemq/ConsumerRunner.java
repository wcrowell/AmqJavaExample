package com.openlogic.activemq;

public class ConsumerRunner {
	public static void main(String[] args) {
		Consumer consumer = new Consumer();
		Thread thread = new Thread(consumer);
		thread.start();

		while (consumer.isExit() == false) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		consumer.close();
		System.out.println("Consumer exiting now");
	}
}
