package com.bizjuned.ms.bot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.microsoft.bot.connector.customizations.MicrosoftAppCredentials;
import com.microsoft.bot.schema.models.ResourceResponse;

@Configuration
public class MSBotConfig {

	  @Autowired
	  private Environment environment;

	  @Bean(name = "credentials")
	  public MicrosoftAppCredentials getCredentials() {
	    return new MicrosoftAppCredentials(environment.getProperty("ms.bot.appId"),
	        environment.getProperty("ms.bot.password"));
	  }

	  @Bean
	  public List<ResourceResponse> getResponses(){
	    return new ArrayList<>();
	  }
	

}
