package net.aros.dialog.custom;

import net.aros.ArosUtker;
import net.aros.dialog.MultiphaseDialog;
import net.aros.dialog.YesNoDialog;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchDialog extends MultiphaseDialog {
    public static List<String> lastSearch = new ArrayList<>();

    public SearchDialog() {
        addPhase(new YesNoDialog(() -> {
            String message = getModuleStatus();
            if (message == null) {
                ArosUtker.terminal.say("Что вы хотите искать?");
                nextPhase();
                return;
            }
            ArosUtker.terminal.say("Ошибка: " + message);
            end();
        }, () -> {
            ArosUtker.terminal.say("Отмена.");
            end();
        })::processCommand);
        addPhase(this::search);
        addPhase(this::chooseFromSearch);
    }

    private String getModuleStatus() {
        if (!ArosUtker.internetModule.isInit()) return "Модуль не инициализирован.";
        if (!ArosUtker.internetModule.isEnabled()) return "Модуль выключен.";
        return null;
    }

    public void search(String command) {
        if (command.isBlank()) return;

        Map<String, String> res;
        try {
            ArosUtker.terminal.say("Попытка поиска...");
            res = ArosUtker.internetModule.search(command);
        } catch (Throwable e) {
            e.printStackTrace();
            ArosUtker.terminal.say("Ошибка. Дальнейший поиск не возможен");
            nextPhase();
            return;
        }
        ArosUtker.terminal.say("Успешно. Результаты:");
        int i = 0;
        for (String title : res.keySet()) {
            ArosUtker.terminal.say((i++) + ". " + title);
        }
        lastSearch.clear();
        lastSearch.addAll(res.values());
        ArosUtker.terminal.say("Что вас из этого интересует (индекс)?");

        nextPhase();
    }

    public void chooseFromSearch(String command) {
        int index;
        try {
            index = Integer.parseInt(command);
        } catch (Throwable t) {
            ArosUtker.terminal.say("Я вас не понял. Ответы: индекс, число");
            return;
        }

        if (index == -1) {
            ArosUtker.terminal.say("Отмена.");
            nextPhase();
            return;
        }

        if (index < -1 || index >= SearchDialog.lastSearch.size()) {
            ArosUtker.terminal.say("Ошибка: индекс больше чем разультаты поиск. Если хотите выйти, введите -1");
            return;
        }

        try {
            ArosUtker.terminal.say("Попытка адресации...");
            Desktop.getDesktop().browse(URI.create(SearchDialog.lastSearch.get(index)));
        } catch (Throwable t) {
            ArosUtker.terminal.setErrorMode(true);
            ArosUtker.terminal.say("Ошибка");
            ArosUtker.terminal.setShouldClearErrorOnMessageEnd(true);
        }

        ArosUtker.terminal.say("Успешно.");
        nextPhase();
    }
}
