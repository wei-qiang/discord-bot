package nl.weiqiang.discordbot.crackbabybot.core.command;

import com.kttdevelopment.mal4j.MyAnimeList;
import com.kttdevelopment.mal4j.anime.Anime;
import com.kttdevelopment.mal4j.property.Genre;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class MyAnimeListCommands extends AbstractCommand {

    private final Logger LOGGER = LoggerFactory.getLogger(MyAnimeListCommands.class);

    private final MyAnimeList myAnimeList;

    protected MyAnimeListCommands(JDA jda, MyAnimeList myAnimeList) {
        super(jda);
        this.myAnimeList = myAnimeList;
    }

    @Override
    public List<CommandData> commandData() {
        return List.of(Commands.slash("mal", "MAL commands")
                .addOption(OptionType.STRING, "search", "search term"));
    }

    @SubscribeEvent
    public void search(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("mal") || Objects.isNull(event.getOption("search"))) {
            return;
        }
        String query = event.getOption("search").getAsString();

        Optional<String> animeId = retrieveAnimeId(query);

        if (animeId.isEmpty()) {
            event.reply(String.format("Crackbabybot could not find an Anime matching the input: %s", query))
                    .queue();
            return;
        }
        Anime result = myAnimeList.getAnime(Long.parseLong(animeId.get()));
        ;

        event.replyEmbeds(new EmbedBuilder()
                        .setTitle(result.getTitle(), String.format("https://myanimelist.net/anime/%s", result.getID()))
                        .setThumbnail(result.getMainPicture().getLargeURL())
                        .setDescription(StringEscapeUtils.unescapeJava(result.getSynopsis()))
                        .setTimestamp(OffsetDateTime.now())
                        .addField("Score", result.getMeanRating().toString(), true)
                        .addField("Genres", Arrays.stream(result.getGenres()).map(Genre::getName).collect(Collectors.joining(", ")), true)
                        .build())
                .queue();
    }

    private Optional<String> retrieveAnimeId(String query) {
        Document doc;
        try {
            doc = Jsoup.connect(String.format("https://myanimelist.net/search/all?q=%s&cat=all", query)).get();
        } catch (IOException exception) {
            LOGGER.error("Could not retrieve doc. Returning null.", exception);
            return Optional.empty();
        }
        Element firstAnimeMatch = doc.getElementsByClass("list di-t w100").get(0);
        String url = firstAnimeMatch.select("a").first().attr("href");
        Pattern pattern = Pattern.compile("anime\\/(\\d\\w+)");
        Matcher m = pattern.matcher(url);

        if (m.find()) {
            return Optional.ofNullable(m.group(1));
        }
        return Optional.empty();
    }
}
