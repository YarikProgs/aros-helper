package net.aros.dialog;

import net.aros.brain.CommandProcessingResult;
import net.aros.util.CommandProcessor;

public class ReloadDialog implements CommandProcessor {
    @Override
    public CommandProcessingResult processCommand(String command) {
        if (command.isBlank() || (!command.equalsIgnoreCase("да") && !command.equalsIgnoreCase("нет")))
            return CommandProcessingResult.singlePhrase("Я вас не понял. Ответы: да / нет", null, false);

        boolean yes = command.equalsIgnoreCase("да");

        return CommandProcessingResult.singlePhrase(
                yes ? "Произвожу перезагрузку системы..." : "Отменяю перезагрузку системы...",
                yes ? "do_reload" : null, true
        );
    }
}
