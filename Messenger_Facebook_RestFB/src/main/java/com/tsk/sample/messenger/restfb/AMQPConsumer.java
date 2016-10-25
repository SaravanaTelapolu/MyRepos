package com.tsk.sample.messenger.restfb;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class AMQPConsumer implements AMQPConsumerMBean {

	private final static Logger logger = Logger.getLogger(AMQPConsumer.class
			.getName());
	// public static final String LOCAL_BROKER_URL = "amqp://localhost:5672";
	// public static final String CLOUD_AQMP_BROKER_URL =
	// "amqp://otriekvf:UxBIpcK4wC_8aNujsSIUAOSz1_W1fXdu@buck.rmq.cloudamqp.com/otriekvf";
	// private final String QUEUE_NAME = "ChatBot";

	private String aqmpURI;
	// private String exchangeName;
	private String queueName;
	private AMQPMessageProcessor messageProcessor;
	private boolean isDurable;
	private boolean isExclusive;
	private boolean autoDelete;

	private boolean isShutdown;
	private boolean isStarted;

	private Connection connection = null;
	private Channel channel = null;
	private boolean isInitialized;

	public AMQPConsumer() {
	}

	public AMQPConsumer(String brokerURI, String strQueueName,
			AMQPMessageProcessor messageProcessor) {
		init(brokerURI, strQueueName, messageProcessor);
	}

	private void init(String brokerURI, String strQueueName,
			AMQPMessageProcessor msgProcessor) {
		if (!isInitialized) {
			isInitialized = true;
			this.aqmpURI = brokerURI;
			// this.exchangeName = strExchangeName;
			this.queueName = strQueueName;
			this.messageProcessor = msgProcessor;

			registerJMXBean();
		}
	}

	private void registerJMXBean() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName("AMPQ:type=AMPQConsumer");
			mbs.registerMBean(this, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startConsumer() {
		if (!isInitialized) {
			System.err
					.println("Please perform initialization before listening for messages!");
			System.exit(-1);
		}

		if (isStarted) {
			logger.info("Consumer is already started. Ignoring the current request!");
			return;
		}

		Thread brokerListener = new Thread(new Runnable() {

			public void run() {
				try {
					ConnectionFactory factory = new ConnectionFactory();
					// factory.setHost("localhost"); // factory.setPort(port);
					// factory.setUsername(username); //
					// factory.setPassword(password);
					factory.setUri(aqmpURI);
					connection = factory.newConnection();
					channel = connection.createChannel();

					channel.queueDeclare(queueName, isDurable, isExclusive,
							autoDelete, null);
					// System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
					logger.info(" [*] Waiting for messages ... ");

					QueueingConsumer consumer = new QueueingConsumer(channel);

					// Auto Acknowledge
					boolean autoAck = true;

					channel.basicConsume(queueName, autoAck, consumer);

					while (!isShutdown) {
						QueueingConsumer.Delivery delivery = consumer
								.nextDelivery(5000);
						if (delivery != null) {
							messageProcessor.processMessage(delivery.getBody());
						}
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error occurred!", e);
				} finally {
					if (channel != null) {
						try {
							channel.close();
							channel = null;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (connection != null) {
						try {
							connection.close();
							connection = null;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					isStarted = false;
				}
			}
		});
		brokerListener.setName("BrokerListenerThread");
		brokerListener.start();
	}

	public void stopConsumer() {
		shutdown();
	}

	public void shutdown() {
		isShutdown = true;
	}

	public String getAqmpURI() {
		return aqmpURI;
	}

	public void setAqmpURI(String aqmpURI) {
		this.aqmpURI = aqmpURI;
	}

	/*
	 * public String getExchangeName() { return exchangeName; }
	 * 
	 * public void setExchangeName(String exchangeName) { this.exchangeName =
	 * exchangeName; }
	 */

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public AMQPMessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(AMQPMessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	public boolean isDurable() {
		return isDurable;
	}

	public void setDurable(boolean isDurable) {
		this.isDurable = isDurable;
	}

	public boolean isExclusive() {
		return isExclusive;
	}

	public void setExclusive(boolean isExclusive) {
		this.isExclusive = isExclusive;
	}

	public boolean isAutoDelete() {
		return autoDelete;
	}

	public void setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	public static void main(String[] args) {
		// AccessToken =
		// ""EAAD5ZAqdZAksMBAMXLPATg5WnGEShNwLdxqWKiYjiAb2KndIYxT5dIWkg7XaA0RGNQYOddxKZA3VhYQYTr0ZBrEJss3vgjCK8V6Cuv1yZAvqHceWkJ0ZCmeGQp3La04p9gjNx394nyZC79i5iImZC0OMvCBZC6o6M6o6jk967hEWb0gZDZD";

		/*
		 * String brokerURI = LOCAL_BROKER_URL; if (args.length == 0) {
		 * System.err.println("\n ReceiveFromAMPQ <local / clould> \n");
		 * System.exit(-1); } else { if (args.length > 0) { if
		 * (!args[0].equalsIgnoreCase("LOCAL")) { brokerURI =
		 * CLOUD_AQMP_BROKER_URL; } } }
		 * 
		 * AMPQConsumer receiveFromBroker = new AMPQConsumer();
		 * receiveFromBroker.listenForMessages(brokerURI);
		 */

	}

}
