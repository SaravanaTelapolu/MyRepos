package com.tsk.sample.messenger.restfb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.restfb.exception.FacebookException;

public class FBChatBotEnginePOC implements AMQPMessageProcessor {

	private static final Logger logger = Logger
			.getLogger(FBChatBotEnginePOC.class.getName());
	private AMQPConsumer consumer;
	private FBBotMessageHelper botHelper;
	private Properties props;

	public FBChatBotEnginePOC() {
	}

	private void init() throws IOException {
		InputStream is = MiscUtils.getInputStream(this.getClass()
				.getClassLoader(), "FBChatBotEngine.properties");
		props = new Properties();
		props.load(is);

		String fbAccessToken = props.getProperty("FB_ACCESS_TOKEN");
		String fbBotUserId = props.getProperty("FB_BOT_USER_ID");

		botHelper = new FBBotMessageHelper(fbAccessToken, fbBotUserId);

		String amqpURI = props.getProperty("AMQP_URI");
		// String exchangeName = props.getProperty("AMQP_EXCHANGE_NAME");
		String queueName = props.getProperty("AMQP_QUEUE_NAME");
		boolean isDurable = Boolean.parseBoolean(props.getProperty(
				"AMQP_IS_DURABLE", "false"));
		boolean autoDelete = Boolean.parseBoolean(props.getProperty(
				"AMQP_AUTO_DELETE", "false"));
		boolean isExclusive = Boolean.parseBoolean(props.getProperty(
				"AMQP_IS_EXCLUSIVE", "false"));
		consumer = new AMQPConsumer(amqpURI, queueName, this);
		consumer.setDurable(isDurable);
		consumer.setExclusive(isExclusive);
		consumer.setAutoDelete(autoDelete);

		consumer.startConsumer();
	}

	public void processMessage(byte[] messagePayLoad) {
		if (messagePayLoad == null) {
			logger.warning("Received a NULL request! Just ignoring it!");
			return;
		}

		String strMsgPayload = new String(messagePayLoad);
		logger.info("Received '" + strMsgPayload + "'");

		try {
			String[] tmpStr = strMsgPayload.split("[|]");

			if (tmpStr != null && tmpStr.length == 2) {
				String recipientId = tmpStr[0];
				String userMessage = tmpStr[1];
				if (botHelper.isRecipientAsBotEngine(recipientId)) {
					logger.warning("Ignoring Request with Recipient as BotEngine itself. Request: "
							+ strMsgPayload);
					return;
				}

				if (userMessage.equalsIgnoreCase("cvgs")
						|| userMessage.equalsIgnoreCase("convergys")
						|| userMessage.equalsIgnoreCase("Convergys Links")) {
					botHelper.sendBotResponseGenericTemplateCVGS(recipientId,
							userMessage);
				} else if (userMessage.equalsIgnoreCase("generic")
						|| userMessage.equalsIgnoreCase("shop")
						|| userMessage.equalsIgnoreCase("VR Headsets")) {
					botHelper.sendBotResponseGenericTemplateShop(recipientId,
							userMessage);
				} else if (userMessage.equalsIgnoreCase("help")) {
					botHelper.sendBotResponseQuickReplies(recipientId);
				} else if (userMessage.equals("offers")) {
					botHelper
							.sendBotResponseGenericTemplateOffersText(recipientId);
				} else if (userMessage.equals("OFFERS")) {
					botHelper
							.sendBotResponseGenericTemplateOffersWithImages(recipientId);
				} else {
					String strResponse = "You sent: " + userMessage;

					if (userMessage.equalsIgnoreCase("balance")
							|| userMessage.equalsIgnoreCase("Account Balance")) {
						strResponse = "Your current balance is $27.50 ";
					} else if (userMessage.equalsIgnoreCase("due date")
							|| userMessage.equalsIgnoreCase("duedate")
							|| userMessage.equalsIgnoreCase("bill")
							|| userMessage.equalsIgnoreCase("invoice")) {
						strResponse = "Your last invoice  is $32.50 dated  10/20/2016 is due by 11/10/2016";
					}
					botHelper.sendBotResponseText(recipientId, strResponse);
				}
			} else {
				logger.log(Level.INFO,
						"Skipping the message since SenderId is missing! Message: "
								+ strMsgPayload);
			}
		} catch (FacebookException fbe) {
			logger.log(
					Level.SEVERE,
					"Error occurred while processing request: " + strMsgPayload,
					fbe);
		}
	}

	public static void main(String[] args) {
		FBChatBotEnginePOC botEngine = new FBChatBotEnginePOC();
		try {
			botEngine.init();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
