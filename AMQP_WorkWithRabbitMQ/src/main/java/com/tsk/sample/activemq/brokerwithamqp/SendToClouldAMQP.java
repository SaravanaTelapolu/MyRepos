package com.tsk.sample.activemq.brokerwithamqp;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SendToClouldAMQP {

	private static final String QUEUE_NAME = "ChatBot";

	public static void main(String[] args) {

		Channel channel = null;
		Connection connection = null;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUri("amqp://otriekvf:UxBIpcK4wC_8aNujsSIUAOSz1_W1fXdu@buck.rmq.cloudamqp.com/otriekvf");
			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			String message = "Hello There!";
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			System.out.println(" [x] Sent '" + message + "'");
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

		System.out.println("Done!");
	}

}
