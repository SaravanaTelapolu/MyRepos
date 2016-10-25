package com.tsk.sample.activemq.brokerwithamqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class ReceiveFromAMPQ {

	private static final String QUEUE_NAME = "ChatBot";
	private static final String LOCAL_BROKER_URL = "amqp://localhost:5672";
	private static final String CLOUD_AQMP_BROKER_URL = "amqp://otriekvf:UxBIpcK4wC_8aNujsSIUAOSz1_W1fXdu@buck.rmq.cloudamqp.com/otriekvf";

	public static void main(String[] args) {

		try {
			String brokerURI = LOCAL_BROKER_URL;
			if (args.length == 0) {
				System.err.println("\n ReceiveFromAMPQ <local / clould> \n");
			} else {
				if (args.length > 0) {
					if (!args[0].equalsIgnoreCase("LOCAL")) {
						brokerURI = CLOUD_AQMP_BROKER_URL;
					}
				}
			}

			ConnectionFactory factory = new ConnectionFactory();
			// factory.setHost("localhost"); // factory.setPort(port);
			// factory.setUsername(username); // factory.setPassword(password);
			factory.setUri(brokerURI);
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

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
		}
	}
}
