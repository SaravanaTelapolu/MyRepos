package com.tsk.sample.activemq.brokerwithamqp;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class ReceiveFromClouldAMPQ {

	private final static String QUEUE_NAME = "ChatBot";

	public static void main(String[] argv) throws java.io.IOException,
			java.lang.InterruptedException {

		Connection connection = null;
		Channel channel = null;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUri("amqp://otriekvf:UxBIpcK4wC_8aNujsSIUAOSz1_W1fXdu@buck.rmq.cloudamqp.com/otriekvf");
			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			System.out
					.println(" [*] Waiting for messages. To exit press CTRL+C");

			QueueingConsumer consumer = new QueueingConsumer(channel);

			// Auto Acknowledge
			boolean autoAck = true;
			channel.basicConsume(QUEUE_NAME, autoAck, consumer);

			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				System.out.println(" [x] Received '" + message + "'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

}
