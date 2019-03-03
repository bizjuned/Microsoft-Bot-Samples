package com.bizjuned.ms.bot;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.Conversations;
import com.microsoft.bot.connector.customizations.MicrosoftAppCredentials;
import com.microsoft.bot.connector.implementation.ConnectorClientImpl;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.microsoft.bot.schema.models.ResourceResponse;

@RestController
@EnableAutoConfiguration
@RequestMapping("/v4/ms/bot")
public class MSBotRestController {

	@Autowired
	MSBotService msBotService;

	@Autowired
	private MicrosoftAppCredentials credentials;

	private static final Logger logger = LoggerFactory.getLogger(MSBotRestController.class);

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResourceResponse create(
			@RequestBody @Valid @JsonDeserialize(using = DateTimeDeserializer.class) Activity activity) {

		logger.info("Input activity : {}", activity);
		
	    ConnectorClient connector =
	            new ConnectorClientImpl(activity.serviceUrl(), credentials);


		Conversations conversation = connector.conversations();
		ResourceResponse resourceResponse = null;
		
		if(activity.type().equals(ActivityTypes.MESSAGE)) {
			Activity responseActivity = msBotService.getResponseMessageActivity(activity);
			logger.info("Response activity : {}", activity);
			resourceResponse = conversation.sendToConversation(activity.conversation().id(), responseActivity);
		} else {
			Activity responseActivity = msBotService.getResponseUpdateConversationActivity(activity);
			resourceResponse = conversation.sendToConversation(activity.conversation().id(), responseActivity);

		}

		return resourceResponse;
	}

}
