package net.aros.dialog;

import net.aros.ArosUtker;
import net.aros.brain.CommandProcessingResult;
import net.aros.util.CommandProcessor;

import java.util.*;

public class SearchDialog implements CommandProcessor {
    public static List<String> lastSearch = new ArrayList<>();

    @Override
    public CommandProcessingResult processCommand(String command) {
        if (command.isBlank()) return CommandProcessingResult.EMPTY;

        Map<String, String> res;
        try {
            ArosUtker.terminal.say("Попытка поиска...");
            res = ArosUtker.internetModule.search(command);
        } catch (Throwable e) {
            e.printStackTrace();
            return CommandProcessingResult.singlePhrase("Ошибка. Дальнейший поиск не возможен", null, true);
        }
        List<String> lines = new ArrayList<>();
        lines.add("Успешно. Результаты:");
        int i = 0;
        for (String title : res.keySet()) {
            lines.add((i++) + ". " + title);
        }
        lastSearch.clear();
        lastSearch.addAll(res.values());
        lines.add("Что вас из этого интересует (индекс)?");

        return new CommandProcessingResult(lines.stream().map(Optional::of).toList(), "choose_from_search", true);
    }
}
