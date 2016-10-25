package com.tsk.sample.messenger.restfb;

import java.util.logging.Logger;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.types.GraphResponse;
import com.restfb.types.User;
import com.restfb.types.send.Bubble;
import com.restfb.types.send.GenericTemplatePayload;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.PostbackButton;
import com.restfb.types.send.QuickReply;
import com.restfb.types.send.SendResponse;
import com.restfb.types.send.SenderActionEnum;
import com.restfb.types.send.TemplateAttachment;
import com.restfb.types.send.WebButton;

/**
 * Wrapper for Submitting Responses for Facebook Messenger Chatbot using RestFB
 * (based on FB Graph APIs)
 * 
 * @author Saravana Telapolu
 */
public class FBBotMessageHelper {
	private final static Logger logger = Logger
			.getLogger(FBBotMessageHelper.class.getName());
	private String accessToken;

	private String fbChatBotPageId;
	private FacebookClient facebookClient;

	public FBBotMessageHelper(String strAccessToken, String fbbotUserId) {
		if (strAccessToken == null || fbbotUserId == null) {
			throw new ExceptionInInitializerError(
					"Either AccessToken or FBChatBot Page Id is null!. Cannot continue! Exiting!");
		}

		accessToken = strAccessToken;
		fbChatBotPageId = fbbotUserId;
		facebookClient = new DefaultFacebookClient(accessToken,
				Version.VERSION_2_6);
	}

	public void retrieveFBUserProfile() {
		try {
			FacebookClient publicOnlyFacebookClient = new DefaultFacebookClient(
					accessToken, Version.VERSION_2_6);
			User user1 = publicOnlyFacebookClient.fetchObject("me", User.class);
			logger.info("User: " + user1.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isRecipientAsBotEngine(String recipientId)
			throws FacebookException {
		return fbChatBotPageId.equals(recipientId);
	}

	public void sendMessage(String recipientId, String strMessage) {
		try {
			IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
			Message simpleTextMessage = new Message(strMessage);
			GraphResponse rtnMsg = facebookClient.publish("/me/messages",
					GraphResponse.class,
					Parameter.with("recipient", recipient),
					Parameter.with("message", simpleTextMessage));
			logger.finer("Message sent! - Id: " + rtnMsg.getId()
					+ " | PostId: " + rtnMsg.getPostId() + "| TimelineID: "
					+ rtnMsg.getTimelineId());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendAction(String recipientId, SenderActionEnum senderAction)
			throws FacebookException {
		IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
		Parameter senderActionParam = Parameter.with("sender_action",
				senderAction);
		Parameter recipientParam = Parameter.with("recipient", recipient);
		SendResponse resp = facebookClient.publish("me/messages",
				SendResponse.class, senderActionParam, recipientParam); // the
		logger.finer("Action Sent! - RecipientId: " + resp.getRecipientId()
				+ " | MessageId: " + resp.getMessageId());
	}

	public void sendBotResponseText(String recipientId, String strResponseToUser)
			throws FacebookException {
		sendAction(recipientId, SenderActionEnum.mark_seen);
		sendAction(recipientId, SenderActionEnum.typing_on);
		try {
			Thread.sleep(250);
		} catch (InterruptedException ie) {
		}
		sendAction(recipientId, SenderActionEnum.typing_off);
		sendMessage(recipientId, strResponseToUser);
	}

	public void sendBotResponseGenericTemplateShop(String recipientId,
			String userInputText1) {
		GenericTemplatePayload payload = new GenericTemplatePayload();

		// create a bubble with a web button
		Bubble firstBubble = new Bubble("Oculus VR Gear - Black");
		firstBubble
				.setImageUrl("http://messengerdemo.parseapp.com/img/rift.png");
		// firstBubble.setItemUrl("https://en.wikipedia.org/wiki/File:Flag_of_India.svg");
		firstBubble.setSubtitle("Special price $69.99 (Org Price: $99.99)");
		WebButton webButton = new WebButton("More Details",
				"http://www.samsung.com/us/mobile/wearable-tech/SM-R323NBKAXAR");
		PostbackButton postbackButton1 = new PostbackButton("Buy",
				"POSTBACK_BUY_VR_GEAR_BLACK");
		firstBubble.addButton(postbackButton1);
		firstBubble.addButton(webButton);

		// create a bubble with a postback button
		Bubble secondBubble = new Bubble("Oculus VR Gear - White");
		secondBubble.setSubtitle("Special price $99.99 (Org Price: $129.99)");
		secondBubble
				.setImageUrl("http://messengerdemo.parseapp.com/img/gearvr.png");
		WebButton webButton2 = new WebButton("More Details",
				"http://www.samsung.com/us/mobile/wearable-tech/SM-R323NBKAXAR");
		PostbackButton postbackButton2 = new PostbackButton("Buy",
				"POSTBACK_BUY_VR_GEAR_WHITE");
		secondBubble.addButton(postbackButton2);
		secondBubble.addButton(webButton2);
		// secondBubble.setItemUrl("http://www.worldmapsatlas.com/india/images/india-flag.gif");

		payload.addBubble(firstBubble);
		payload.addBubble(secondBubble);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message templateMessage = new Message(templateAttachment);

		IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
		GraphResponse rtnMsg = facebookClient.publish("/me/messages",
				GraphResponse.class, Parameter.with("recipient", recipient),
				Parameter.with("message", templateMessage));
		logger.fine("Message sent! - Id: " + rtnMsg.getId() + " | PostId: "
				+ rtnMsg.getPostId() + "| TimelineID: "
				+ rtnMsg.getTimelineId());

	}

	public void sendBotResponseGenericTemplateCVGS(String recipientId,
			String userInputText1) {
		GenericTemplatePayload payload = new GenericTemplatePayload();

		// create a bubble with a web button
		Bubble firstBubble = new Bubble("Convergys");
		firstBubble
				.setImageUrl("https://s14.postimg.org/45p8fyw0h/FB_Msnger_CVGS_Logo_03.jpg");
		// firstBubble.setItemUrl("https://en.wikipedia.org/wiki/File:Flag_of_India.svg");
		firstBubble.setSubtitle("Click URL below tp open Convergys Home Page");
		WebButton webButton = new WebButton("Web URL",
				"http://www.convergys.com");
		firstBubble.addButton(webButton);

		// create a bubble with a postback button
		Bubble secondBubble = new Bubble("Convergys - DDS");
		PostbackButton postbackButton = new PostbackButton("Post to DDS",
				"POSTBACK_STRING_DDS");
		WebButton weButton2 = new WebButton("Web Link",
				"http://www.convergys.com/convergys-technologies/real-time-campaign-management");
		secondBubble.addButton(weButton2);
		secondBubble.addButton(postbackButton);
		secondBubble
				.setImageUrl("https://s16.postimg.org/gusksqsd1/FB_Msnger_RTM_Logo_03.jpg");
		// secondBubble.setItemUrl("http://www.worldmapsatlas.com/india/images/india-flag.gif");

		secondBubble.setSubtitle("Landing page for DDS Solution!");

		payload.addBubble(firstBubble);
		payload.addBubble(secondBubble);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message templateMessage = new Message(templateAttachment);

		IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
		GraphResponse rtnMsg = facebookClient.publish("/me/messages",
				GraphResponse.class, Parameter.with("recipient", recipient),
				Parameter.with("message", templateMessage));
		logger.fine("Message sent! - Id: " + rtnMsg.getId() + " | PostId: "
				+ rtnMsg.getPostId() + "| TimelineID: "
				+ rtnMsg.getTimelineId());

	}

	public void sendBotResponseGenericTemplateOffersText(String recipientId) {
		GenericTemplatePayload payload = new GenericTemplatePayload();

		// create a bubble with a web button
		Bubble firstBubble = new Bubble("Freedom Combo 4G");
		// firstBubble.setImageUrl("https://s14.postimg.org/45p8fyw0h/FB_Msnger_CVGS_Logo_03.jpg");
		firstBubble
				.setSubtitle("Get 8GB + 8GB Bonus, 200 Calls & 500 SMS for just $49.99 (Reg. 69.99)");
		PostbackButton postbackButton1 = new PostbackButton("Subscribe",
				"FREEDOM_COMBO_4G");
		firstBubble.addButton(postbackButton1);

		// create a bubble with a postback button
		Bubble secondBubble = new Bubble("Unlimited 4G");
		secondBubble
				.setSubtitle("Get Unlimited 4G @ special price of $99.99. Limited period offer. (Reg. 129.99)");
		PostbackButton postbackButton = new PostbackButton("Subscribe",
				"UNLIMITED_4G");
		secondBubble.addButton(postbackButton);
		// secondBubble.setItemUrl("http://www.worldmapsatlas.com/india/images/india-flag.gif");

		Bubble ThirdBubble = new Bubble("Everything Unlimited!");
		// firstBubble.setImageUrl("https://s14.postimg.org/45p8fyw0h/FB_Msnger_CVGS_Logo_03.jpg");
		// firstBubble.setItemUrl("https://en.wikipedia.org/wiki/File:Flag_of_India.svg");
		ThirdBubble
				.setSubtitle("No limits, No Bounds! Enjoy Voice/Data/SMS as much you need!  Special Price: $199.99 (Reg. $399). Offer expires 31st Oct 16");
		PostbackButton postbackButton3 = new PostbackButton("Subscribe",
				"UNLIMITED_EVERYTHING");
		ThirdBubble.addButton(postbackButton3);

		payload.addBubble(firstBubble);
		payload.addBubble(secondBubble);
		payload.addBubble(ThirdBubble);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message templateMessage = new Message(templateAttachment);

		IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
		GraphResponse rtnMsg = facebookClient.publish("/me/messages",
				GraphResponse.class, Parameter.with("recipient", recipient),
				Parameter.with("message", templateMessage));
		logger.fine("Message sent! - Id: " + rtnMsg.getId() + " | PostId: "
				+ rtnMsg.getPostId() + "| TimelineID: "
				+ rtnMsg.getTimelineId());

	}

	public void sendBotResponseGenericTemplateOffersWithImages(
			String recipientId) {
		GenericTemplatePayload payload = new GenericTemplatePayload();

		WebButton webButton = new WebButton("More Details",
				"https://indosatooredoo.com/");

		// create a bubble with a web button
		PostbackButton postbackButton1 = new PostbackButton("Subscribe",
				"MY_OFFER_1");
		Bubble firstBubble = new Bubble("Your Personal Offer 1");
		firstBubble
				.setImageUrl("https://s3.postimg.org/s99y01cwv/FB_Msnger_Isat_Offer_01b.jpg");
		firstBubble.addButton(postbackButton1);
		firstBubble.addButton(webButton);

		// create a bubble with a postback button
		PostbackButton postbackButton = new PostbackButton("Subscribe",
				"MY_OFFER_2");
		Bubble secondBubble = new Bubble("Your Personal Offer 2");
		secondBubble
				.setImageUrl("https://s3.postimg.org/w6x7pfzq7/FB_Msnger_Isat_Offer_02b.jpg");
		secondBubble.addButton(postbackButton);
		secondBubble.addButton(webButton);
		// secondBubble.setItemUrl("http://www.worldmapsatlas.com/india/images/india-flag.gif");

		payload.addBubble(firstBubble);
		payload.addBubble(secondBubble);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message templateMessage = new Message(templateAttachment);

		IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
		GraphResponse rtnMsg = facebookClient.publish("/me/messages",
				GraphResponse.class, Parameter.with("recipient", recipient),
				Parameter.with("message", templateMessage));
		logger.fine("Message sent! - Id: " + rtnMsg.getId() + " | PostId: "
				+ rtnMsg.getPostId() + "| TimelineID: "
				+ rtnMsg.getTimelineId());

	}

	public void sendBotResponseQuickReplies(String recipientId) {
		QuickReply quickReply1 = new QuickReply("Account Balance", "Balance");
		QuickReply quickReply2 = new QuickReply("Due Date", "duedate");
		QuickReply quickReply3 = new QuickReply("VR Headsets", "VR Headsets");
		QuickReply quickReply4 = new QuickReply("Convergys Links", "Convergys");
		QuickReply quickReply5 = new QuickReply("Convergys", "Convergys");

		try {
			IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
			Message quickReplyMessage = new Message("List of Quick Replies");
			quickReplyMessage.addQuickReply(quickReply1);
			quickReplyMessage.addQuickReply(quickReply2);
			quickReplyMessage.addQuickReply(quickReply3);
			quickReplyMessage.addQuickReply(quickReply4);
			GraphResponse rtnMsg = facebookClient.publish("/me/messages",
					GraphResponse.class,
					Parameter.with("recipient", recipient),
					Parameter.with("message", quickReplyMessage));
			logger.finer("Quick Replies Message sent! - Id: " + rtnMsg.getId()
					+ " | PostId: " + rtnMsg.getPostId() + "| TimelineID: "
					+ rtnMsg.getTimelineId());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendBotResponseOffers(String recipientId) {

		sendBotResponseText(recipientId, "Choose Your Own Bonus");
		GenericTemplatePayload payload = new GenericTemplatePayload();

		// create a bubble with a web button
		Bubble firstBubble = new Bubble("First Card");
		firstBubble
				.setImageUrl("https://s14.postimg.org/45p8fyw0h/FB_Msnger_CVGS_Logo_03.jpg");
		// firstBubble.setItemUrl("https://en.wikipedia.org/wiki/File:Flag_of_India.svg");
		firstBubble.setSubtitle("Element #1 of an hscroll - CVGS Home Page");
		WebButton webButton = new WebButton("Web URL",
				"http://www.convergys.com");
		firstBubble.addButton(webButton);

		// create a bubble with a postback button
		Bubble secondBubble = new Bubble("Second Card");
		PostbackButton postbackButton = new PostbackButton("CVGS DDS",
				"POSTBACK_STRING_DDS");
		secondBubble.addButton(postbackButton);
		// secondBubble.setImageUrl("https://s16.postimg.org/gusksqsd1/FB_Msnger_RTM_Logo_03.jpg");
		// secondBubble.setItemUrl("http://www.worldmapsatlas.com/india/images/india-flag.gif");

		secondBubble
				.setSubtitle("Element #2 of an hscroll -  <BR>CVGS CMS Home page");

		payload.addBubble(firstBubble);
		payload.addBubble(secondBubble);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message templateMessage = new Message(templateAttachment);

		IdMessageRecipient recipient = new IdMessageRecipient(recipientId);
		GraphResponse rtnMsg = facebookClient.publish("/me/messages",
				GraphResponse.class, Parameter.with("recipient", recipient),
				Parameter.with("message", templateMessage));
		logger.fine("Message sent! - Id: " + rtnMsg.getId() + " | PostId: "
				+ rtnMsg.getPostId() + "| TimelineID: "
				+ rtnMsg.getTimelineId());

	}

}
