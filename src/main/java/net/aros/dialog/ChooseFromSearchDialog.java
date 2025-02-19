package net.aros.dialog;

import net.aros.ArosUtker;
import net.aros.brain.CommandProcessingResult;
import net.aros.util.CommandProcessor;

import java.awt.*;
import java.net.URI;

public class ChooseFromSearchDialog implements CommandProcessor {
    @Override
    public CommandProcessingResult processCommand(String command) {
        int val;
        try {
            val = Integer.parseInt(command);
        } catch (Throwable t) {
            return CommandProcessingResult.singlePhrase("Я вас не понял. Ответы: индекс, число", null, false);
        }

        if (val == -1) {
            return CommandProcessingResult.singlePhrase("Отмена.", null, true);
        }

        if (val < -1 || val >= SearchDialog.lastSearch.size()) {
            return CommandProcessingResult.singlePhrase("Ошибка: индекс больше чем разультаты поиск. Если хотите выйти, введите -1", null, false);
        }

        try {
            ArosUtker.terminal.say("Попытка адресации...");
            Desktop.getDesktop().browse(URI.create(SearchDialog.lastSearch.get(val)));
        } catch (Throwable t) {
            ArosUtker.terminal.setErrorMode(true);
            ArosUtker.terminal.say("Ошибка");
            ArosUtker.terminal.setShouldClearErrorOnMessageEnd(true);
        }

        return CommandProcessingResult.singlePhrase("Успешно.", null, true);
    }
}
