package net.aros.brain;

import net.aros.util.CommandProcessor;
import net.aros.util.SimilarityFinder;

import java.util.List;
import java.util.Optional;

import static net.aros.util.SimilarityFinder.MatchResult;

public class Brain implements CommandProcessor {
    private final SimilarityFinder finder = new SimilarityFinder();

    @Override
    public CommandProcessingResult processCommand(String command) {
        if (command.isBlank()) return new CommandProcessingResult(List.of(), null);

        Optional<MatchResult> opt = finder.findBestMatch(command, CommandLoader.getCommands());
        if (opt.isEmpty()) return new CommandProcessingResult(List.of(
                Optional.of("Пожалуйста, уточните ваш запрос."),
                Optional.of("Что вы хотите сделать?")
        ), null);

        MatchResult result = opt.get();

        return new CommandProcessingResult(List.of(
                result.status().format(result.command().description()),
                Optional.ofNullable(processCommand(result.command()))
        ), result.command().id());
    }

    private String processCommand(Command command) {
        return switch (command.id()) {
            case "build" -> "К сожалению, ваша комплектация системного помощника У.Т.К.Э.Р не поддерживает какую-либо постройку или улучшение.";
            case "info" -> "Пока что информацию о компании У.Т.К.Э.Р ещё не завезли. Ожидайте.";
            case "reload" -> "Произвожу перезагрузку системы...";
            case "hello" -> "Здравствуйте, я системный помощник от компании У.Т.К.Э.Р. Что вы хотите сделать?";
            case "Z" -> "Z.";
            default -> null;
        };
    }
}
