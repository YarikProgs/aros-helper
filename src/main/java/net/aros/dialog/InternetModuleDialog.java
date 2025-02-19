package net.aros.dialog;

import net.aros.ArosUtker;
import net.aros.brain.CommandProcessingResult;
import net.aros.util.CommandProcessor;

public class InternetModuleDialog implements CommandProcessor {
    @Override
    public CommandProcessingResult processCommand(String command) {
        if (command.isBlank() || (!command.equalsIgnoreCase("да") && !command.equalsIgnoreCase("нет")))
            return CommandProcessingResult.singlePhrase("Я вас не понял. Ответы: да / нет", null, false);

        boolean yes = command.equalsIgnoreCase("да");
        String message = yes ? getModuleStatus() : null;

        return CommandProcessingResult.singlePhrase(
                yes ? message == null ? "Что вы хотите искать?" : "Ошибка: " + message : null,
                yes && message == null ? "do_search" : null, true
        );
    }

    private String getModuleStatus() {
        if (!ArosUtker.internetModule.isInit()) return "Модуль не инициализирован.";
        if (!ArosUtker.internetModule.isEnabled()) return "Модуль выключен.";
        return null;
    }
}
