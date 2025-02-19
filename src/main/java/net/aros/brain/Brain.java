package net.aros.brain;

import net.aros.util.CommandProcessor;
import net.aros.util.SimilarityFinder;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static net.aros.util.SimilarityFinder.MatchResult;

public class Brain implements CommandProcessor {
    public static final Random RANDOM = new Random();
    private final SimilarityFinder finder = new SimilarityFinder();
    private CommandProcessor currentDialog;

    @Override
    public CommandProcessingResult processCommand(String command) {
        if (currentDialog != null) {
            CommandProcessingResult result = currentDialog.processCommand(command);
            if (result.shouldEndDialog()) currentDialog = null;
            return result;
        }

        if (command.isBlank()) return CommandProcessingResult.EMPTY;

        Optional<MatchResult> opt = finder.findBestMatch(command, CommandLoader.getCommands());
        if (opt.isEmpty()) return new CommandProcessingResult(List.of(
                Optional.of("Пожалуйста, уточните ваш запрос."),
                Optional.of("Что вы хотите сделать?")
        ), null, false);

        MatchResult result = opt.get();

        return new CommandProcessingResult(List.of(
                result.status().format(result.command().description()),
                Optional.ofNullable(result.command().answers().get(RANDOM.nextInt(result.command().answers().size())))
        ), result.command().id(), false);
    }

    public void setDialog(CommandProcessor newDialog) {
        this.currentDialog = newDialog;
    }
}
