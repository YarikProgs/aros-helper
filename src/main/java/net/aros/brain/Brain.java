package net.aros.brain;

import net.aros.ArosUtker;
import net.aros.util.CommandProcessor;
import net.aros.util.SimilarityFinder;

import java.util.Optional;
import java.util.Random;

import static net.aros.util.SimilarityFinder.MatchResult;

public class Brain {
    public static final Random RANDOM = new Random();
    private final SimilarityFinder finder = new SimilarityFinder();
    private CommandProcessor currentDialog;

    public void processCommand(String command) {
        if (currentDialog != null) {
            if (currentDialog.processCommand(command)) currentDialog = null;
            return;
        }

        if (command.isBlank()) return;

        Optional<MatchResult> opt = finder.findBestMatch(command, CommandLoader.getCommands());
        if (opt.isEmpty()) {
            ArosUtker.terminal.say("Пожалуйста, уточните ваш запрос.");
            ArosUtker.terminal.say("Что вы хотите сделать?");
            return;
        }

        MatchResult result = opt.get();

        ArosUtker.terminal.say(result.status().format(result.command().description()));
        ArosUtker.terminal.say(result.command().answers().get(RANDOM.nextInt(result.command().answers().size())));

        ArosUtker.register.find(result.command().id()).ifPresent(Runnable::run);
    }

    public void setDialog(CommandProcessor newDialog) {
        this.currentDialog = newDialog;
    }
}
