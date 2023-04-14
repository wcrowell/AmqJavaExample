package com.openlogic.activemq;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class Consumer implements Runnable, MessageListener {
	private MessageProducer producer;
	private Session session;
	private Connection connection;
	private MessageConsumer consumer;

	private static final String CONNECTION_STRING = "tcp://localhost:61616";
	private static final String PRODUCER_EXIT = "exit";
	private static final String MESSAGE_QUEUE_NAME = "queue1";

	private static final boolean transacted = false;
	private static final int ackMode = Session.AUTO_ACKNOWLEDGE;
	
	private boolean exit = false;
	private final PrintStream ps = System.out;

	@Override
	public void run() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTION_STRING);

		try {
			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(transacted, ackMode);
			Destination adminQueue = session.createQueue(MESSAGE_QUEUE_NAME);

			producer = session.createProducer(null);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			consumer = session.createConsumer(adminQueue);
			consumer.setMessageListener(this);
			ps.append("Consumer waiting for data...");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
				TextMessage txtMessage = (TextMessage) message;
				String text = txtMessage.getText();

				if (text.contains(PRODUCER_EXIT)) {
					exit = true;
				} else {
					TextMessage responseMessage = session.createTextMessage();

					ps.println(String.format("Message received: %s", text));

					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
					Date now = new Date();
					String date = sdf.format(now);

					String response = String.format("confirming receipt of %s at time %s", text,
							date);
					responseMessage.setText(response);

					responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
					ps.println(String.format("Sending back: %s to %s %s\n", response, message.getJMSReplyTo(),
							message.getJMSCorrelationID()));
					producer.send(message.getJMSReplyTo(), responseMessage);
				}
			} catch (JMSException e) {
				e.printStackTrace();
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
}
