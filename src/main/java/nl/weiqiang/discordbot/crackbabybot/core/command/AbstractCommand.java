package nl.weiqiang.discordbot.crackbabybot.core.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public abstract class AbstractCommand {

    private final JDA jda;

    protected AbstractCommand(JDA jda) {
        this.jda = jda;

        addCommand();
        jda.addEventListener(this);
    }

    public abstract List<CommandData> commandData();

    private void addCommand() {
        jda.updateCommands()
                .addCommands(commandData())
                .queue();
    }
}
