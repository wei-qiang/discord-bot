package nl.weiqiang.discordbot.crackbabybot.configuration;

import com.kttdevelopment.mal4j.MyAnimeList;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    public JDA jda(BotConfigProperties configProperties) {
        return JDABuilder.createDefault(configProperties.getToken())
                .setEventManager(new AnnotatedEventManager())
                .build();
    }

    @Bean
    public MyAnimeList myAnimeList(MalConfigProperties configProperties) {
        return MyAnimeList.withClientID(configProperties.getClientId());
    }
}
