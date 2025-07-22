package com.openlogic.activemq;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import jakarta.jms.Connection;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer implements Runnable, MessageListener {
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;

    private static final boolean transacted = false;
    private static final int ackMode = Session.AUTO_ACKNOWLEDGE;

    private boolean exit = false;

    private Scanner input;
    private final PrintStream ps = System.out;

    public void sendData(String data) {
        try (InputStream input = Consumer.class.getClassLoader().getResourceAsStream("amq.properties")) {
            Properties p = new Properties();
            p.load(input);

            String brokerURL = p.getProperty("brokerUrl");
            String queueName = p.getProperty("queue");

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
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

            if (session == null) {
                session = connection.createSession(transacted, ackMode);
                adminQueue = session.createQueue(queueName);
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

            ps.printf("Sending: %s%n", data);
            producer.send(message);
        } catch (Exception e) {
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

            ps.printf("Data to send is: %s%n", data);
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
                ps.printf("Message text from consumer: %s\n%n", textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
