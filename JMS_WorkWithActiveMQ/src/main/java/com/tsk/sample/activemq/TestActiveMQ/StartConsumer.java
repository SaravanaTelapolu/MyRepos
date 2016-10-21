package com.tsk.sample.activemq.TestActiveMQ;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class StartConsumer {

	public static String brokerURL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		// setup the connection to ActiveMQ
		ConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
		MessageListener listener = new MessageListener() {

			public void onMessage(Message message) {
				try {
					if (message instanceof TextMessage) {
						TextMessage txtMessage = (TextMessage) message;
						System.out.println("Message received: "
								+ txtMessage.getText());
					} else {
						System.out.println("Invalid message received.");
					}
				} catch (JMSException e) {
					System.out.println("Caught:" + e);
					e.printStackTrace();
				}
			}
		};

		JMSConsumer consumer = new JMSConsumer(factory, "test");
		consumer.startListening(listener);
		System.out.println("Done");
	}
}
