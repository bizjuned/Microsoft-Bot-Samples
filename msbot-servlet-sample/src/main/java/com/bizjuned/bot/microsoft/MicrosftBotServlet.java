package com.bizjuned.bot.microsoft;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.microsoft.aad.adal4j.AuthenticationException;
import com.microsoft.bot.connector.customizations.CredentialProvider;
import com.microsoft.bot.connector.customizations.CredentialProviderImpl;
import com.microsoft.bot.connector.customizations.JwtTokenValidation;
import com.microsoft.bot.connector.customizations.MicrosoftAppCredentials;
import com.microsoft.bot.connector.implementation.ConnectorClientImpl;
import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;

@WebServlet(urlPatterns = "/v4/ms/bot/*")
public class MicrosftBotServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2262484933414314573L;

	private static final Logger log = LoggerFactory.getLogger(MicrosftBotServlet.class);

	private String appId = ""; //add your app id here
	private String appPassword; // add your app password here

	private CredentialProvider credentialProvider;
	private MicrosoftAppCredentials credentials;
	private ObjectMapper objectMapper;

	@Override
	public void init() throws ServletException {
		super.init();
		
		this.credentialProvider = new CredentialProviderImpl(appId, appPassword);
		this.credentials = new MicrosoftAppCredentials(appId, appPassword);
		this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.findAndRegisterModules();

	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		log.info("//////////////// Inbound SkypeBot request received ////////////////");
		if (req.getMethod().equalsIgnoreCase("post")) {

			try {

				Activity activity = getActivity(req);
				String authHeader = req.getHeader("Authorization");
				
                JwtTokenValidation.assertValidActivity(activity, authHeader, credentialProvider);

				// send ack to user activity
				resp.setHeader(HttpHeaders.ACCEPT, "202");
				resp.getWriter().write("");

				ConnectorClientImpl connector = new ConnectorClientImpl(activity.serviceUrl(), this.credentials);

				if (ActivityTypes.MESSAGE.equals(activity.type())) {

					log.info("Activity of type message");

					String inputText = activity.text();

					log.info("Input text [{}]", inputText);

					String outputText = "Echoing -- > " + inputText;
					
					log.info("Bot response {}", outputText);

					connector.conversations().sendToConversation(
							activity.conversation().id(),
							new Activity().withType(ActivityTypes.MESSAGE).withText(outputText)
									.withRecipient(activity.from()).withFrom(activity.recipient()));
				}
				else {

					connector.conversations().sendToConversation(activity.conversation().id(),
							new Activity().withType(ActivityTypes.CONVERSATION_UPDATE).withRecipient(activity.from())
									.withFrom(activity.recipient()));
				}

			} catch (AuthenticationException ex) {

				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				log.warn("Auth failed!", ex);
			} catch (Exception ex) {
				log.warn("Execution failed", ex);
			}
		}

	}


	private String getRequestBody(HttpServletRequest req) throws IOException {
		StringBuilder buffer = new StringBuilder();
		InputStream stream = req.getInputStream();
		int rByte;
		while ((rByte = stream.read()) != -1) {
			buffer.append((char) rByte);
		}
		stream.close();
		if (buffer.length() > 0) {
			return buffer.toString();
		}
		return "";
	}

	private Activity getActivity(HttpServletRequest req) {
		try {
			String body = getRequestBody(req);
			log.info("Request body : {}", body);

			return objectMapper.readValue(body, Activity.class);

		} catch (Exception ex) {
			log.warn("Failed to get activity", ex);
			return null;
		}
	}


	
	
}
