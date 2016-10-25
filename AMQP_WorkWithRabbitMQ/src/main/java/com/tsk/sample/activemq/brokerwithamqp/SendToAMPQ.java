package com.tsk.sample.activemq.brokerwithamqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SendToAMPQ {

	private static final String QUEUE_NAME = "ChatBot";
	private static final String LOCAL_BROKER_URL = "amqp://localhost:5672";
	private static final String CLOUD_AQMP_BROKER_URL = "amqp://otriekvf:UxBIpcK4wC_8aNujsSIUAOSz1_W1fXdu@buck.rmq.cloudamqp.com/otriekvf";

	public static void main(String[] args) {
		int numOfMessages = 5;
		String brokerURI = LOCAL_BROKER_URL;

		if (args != null) {
			if (args.length == 0) {
				System.err
						.println("\n\nSendToAMPQ <local / clould> [Num Of Messages (Default 5)] \n\n");
			} else {
				if (args.length > 0) {
					if (!args[0].equalsIgnoreCase("LOCAL")) {
						brokerURI = CLOUD_AQMP_BROKER_URL;
					}
				}

				if (args.length > 1) {
					numOfMessages = Integer.valueOf(args[1]);
					System.out.println("Num. Of messages: " + numOfMessages);
				}
			}
		}

		try {
			ConnectionFactory factory = new ConnectionFactory();
			// factory.setHost("localhost"); // factory.setPort(port);
			// factory.setUsername(username); // factory.setPassword(password);
			factory.setUri(brokerURI);

			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			for (int i = 0; i <= numOfMessages; i++) {
				String message = "Hello There! - " + System.currentTimeMillis();
				channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
				System.out.println(" [x] Sent '" + message + "'");
				try {
					Thread.sleep(1);
				} catch (InterruptedException ie) {
				}
			}

			channel.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
