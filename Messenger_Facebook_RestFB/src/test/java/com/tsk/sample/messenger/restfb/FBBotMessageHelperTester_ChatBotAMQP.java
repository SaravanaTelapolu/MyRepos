package com.tsk.sample.messenger.restfb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class FBBotMessageHelperTester_ChatBotAMQP {

	private FBBotMessageHelper botHelper;
	private Properties props;
	private static final Logger logger = Logger
			.getLogger(FBBotMessageHelperTester_ChatBotAMQP.class.getName());

	public void initBotHelper() throws IOException {
		props = new Properties();
		InputStream is = MiscUtils.getInputStream(this.getClass()
				.getClassLoader(), "FBChatBotEngine.properties");
		props = new Properties();
		props.load(is);

		String fbAccessToken = props.getProperty("FB_ACCESS_TOKEN");
		String fbBotUserId = props.getProperty("FB_BOT_USER_ID");

		botHelper = new FBBotMessageHelper(fbAccessToken, fbBotUserId);
	}

	public void runTests() {
		try {
			// botHelper.sendBotResponseGenericTemplate("1073150516088053",
			// "Generic");
			// botHelper.sendBotResponseQuickReplies("1073150516088053");
			botHelper
					.sendBotResponseGenericTemplateOffersText("1073150516088053");
			botHelper
					.sendBotResponseGenericTemplateOffersWithImages("1073150516088053");
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			FBBotMessageHelperTester_ChatBotAMQP tester = new FBBotMessageHelperTester_ChatBotAMQP();
			tester.initBotHelper();
			tester.runTests();
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
