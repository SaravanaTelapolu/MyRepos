package com.tsk.sample.activemq.TestActiveMQ;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

public class StartProducer {
	public static String brokerURL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		// setup the connection to ActiveMQ
		ConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);

		JMSProducer producer = new JMSProducer(factory, "test");
		producer.publishMessage("Test Message", 5);
		producer.close();
	}
}