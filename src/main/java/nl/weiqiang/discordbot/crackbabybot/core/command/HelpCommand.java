package nl.weiqiang.discordbot.crackbabybot.core.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelpCommand extends AbstractCommand {

    public HelpCommand(JDA jda) {
        super(jda);
    }

    @Override
    public List<CommandData> commandData() {
        return List.of(Commands.slash("help", "displays all commands."));
    }

    @SubscribeEvent
    public void help(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("help")) {
            return;
        }

        event.reply("""
                        Here are the commands you can use:
                        !anilist recommend [anime\\manga] [title] - Recommends you some anime/manga based on the input
                        !m search [title] - Search for the given title on MAL
                        !music recommend [arguments] - Recommends 10 albums based on your input, see !help music for more information on the arguments
                        !randomify - Returns a random song from Maxim's shit playlist
                        !japanify [artistID] - Returns all albums by the given spotify artist in the JP region""")
                .queue();
    }
}
