package com.openlogic.activemq;

import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer implements Runnable, MessageListener {
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;

	private static final String CONNECTION_STRING = "tcp://localhost:61616";
	private static final String QUEUE_NAME = "queue1";

	private static final boolean transacted = false;
	private static final int ackMode = Session.AUTO_ACKNOWLEDGE;

	private boolean exit = false;

	private Scanner input;
	private PrintStream ps = System.out;
	
	public void sendData(String data) {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTION_STRING);
		Destination adminQueue = null;
		Destination destination = null;

		if (connection == null) {
			try {
				connection = connectionFactory.createConnection();
				connection.start();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

		try {
			if (session == null) {
				session = connection.createSession(transacted, ackMode);
				adminQueue = session.createQueue(QUEUE_NAME);
				producer = session.createProducer(adminQueue);
			}

			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			destination = session.createTemporaryQueue();
			consumer = session.createConsumer(destination);

			consumer.setMessageListener(this);

			TextMessage message = session.createTextMessage();
			message.setText(data);
			message.setJMSReplyTo(destination);

			String correlationId = createRandomString();
			message.setJMSCorrelationID(correlationId);

			ps.println(String.format("Sending: %s", data));
			producer.send(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private String createRandomString() {
		Random random = new Random(System.currentTimeMillis());
		long randomLong = random.nextLong();
		return String.valueOf(randomLong);
	}

	@Override
	public void run() {
		String data = null;

		while (!exit) {
			if (input == null) {
				input = new Scanner(System.in);
			}

			ps.println("Enter data to send (type 'exit' to exit)");
			data = String.format("%s", input.nextLine());

			ps.println(String.format("Data to send is: %s", data));
			sendData(data);

			if (data.contains("exit")) {
				exit = true;
			}
		}
	}

	public boolean isExit() {
		return exit;
	}

	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
			if (session != null) {
				session.close();
			}
			if (producer != null) {
				producer.close();
			}
			if (consumer != null) {
				consumer.close();
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				TextMessage textMessage = (TextMessage) message;
				ps.println(String.format("Message text from consumer: %s\n", textMessage.getText()));
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
