package com.openlogic.activemq;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

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

public class Consumer implements Runnable, MessageListener {
    private MessageProducer producer;
    private Session session;
    private Connection connection;
    private MessageConsumer consumer;

    private static final String PRODUCER_EXIT = "exit";
    private static final boolean transacted = false;
    private static final int ackMode = Session.AUTO_ACKNOWLEDGE;

    private boolean exit = false;
    private final PrintStream ps = System.out;

    @Override
    public void run() {
        try (InputStream input = Consumer.class.getClassLoader().getResourceAsStream("amq.properties")) {
            Properties p = new Properties();
            p.load(input);

            String brokerURL = p.getProperty("brokerUrl");
            String queueName = p.getProperty("queue");
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);

            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(transacted, ackMode);
            Destination adminQueue = session.createQueue(queueName);

            producer = session.createProducer(null);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            consumer = session.createConsumer(adminQueue);
            consumer.setMessageListener(this);
            ps.append("Consumer waiting for data...");
        } catch (Exception e) {
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

                    ps.printf("Message received: %s%n", text);

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
                    Date now = new Date();
                    String date = sdf.format(now);

                    String response = String.format("confirming receipt of %s at time %s", text,
                            date);
                    responseMessage.setText(response);

                    responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
                    ps.printf("Sending back: %s to %s %s\n%n", response, message.getJMSReplyTo(),
                            message.getJMSCorrelationID());
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
