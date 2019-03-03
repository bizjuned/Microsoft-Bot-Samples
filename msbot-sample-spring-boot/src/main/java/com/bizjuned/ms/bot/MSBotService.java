package com.bizjuned.ms.bot;

import org.springframework.stereotype.Controller;

import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;

@Controller
public class MSBotService {

	public Activity getResponseMessageActivity(Activity activity) {
		return new Activity()
		        .withType(ActivityTypes.MESSAGE)
		        .withRecipient(activity.from())
		        .withFrom(activity.recipient())
		        .withText("Echo --> " + activity.text());
	}
	
	
	public Activity getResponseUpdateConversationActivity(Activity activity) {
		return new Activity()
		        .withType(ActivityTypes.CONVERSATION_UPDATE)
		        .withRecipient(activity.from())
		        .withFrom(activity.recipient());
	}
	

}
