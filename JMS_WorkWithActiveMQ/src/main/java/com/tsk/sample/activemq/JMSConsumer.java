package com.tsk.sample.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

public class JMSConsumer {

	public static String brokerURL = "tcp://localhost:61616";

	private ConnectionFactory conFactory;
	private Connection connection;
	private Session session;
	private MessageConsumer consumer;

	/**
	 * 
	 * @param factory
	 * @param queueName
	 * @throws JMSException
	 */
	public JMSConsumer(ConnectionFactory factory, String queueName)
			throws JMSException {
		this.conFactory = factory;
		connection = conFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(queueName);
		consumer = session.createConsumer(destination);
	}

	/**
	 * 
	 * @param listener
	 * @throws JMSException
	 */
	public void startListening(MessageListener listener) throws JMSException {
		consumer.setMessageListener(listener);
	}

}