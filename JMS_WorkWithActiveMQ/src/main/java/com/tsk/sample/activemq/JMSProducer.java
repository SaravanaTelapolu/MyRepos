package com.tsk.sample.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class JMSProducer {
	private ConnectionFactory conFactory;
	private Connection connection;
	private Session session;
	private MessageProducer producer;

	public JMSProducer(ConnectionFactory factory, String queueName)
			throws JMSException {
		this.conFactory = factory;
		connection = conFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(queueName);
		producer = session.createProducer(destination);
	}

	public void sendMessage(String text) throws JMSException {
		Message message = session.createTextMessage(text);
		producer.send(message);
	}

	public void publishMessage(String message, int count) throws JMSException {
		for (int i = 1; i <= count; i++) {
			sendMessage(message + " - " + i);
			System.out.println("Creating Message " + i);
		}
	}

	public void close() throws JMSException {
		if (connection != null) {
			connection.close();
		}
	}

}