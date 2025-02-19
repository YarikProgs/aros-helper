package net.aros.brain;

import net.aros.ArosUtker;
import net.aros.dialog.YesNoDialog;
import net.aros.dialog.custom.EnableInternetModuleDialog;
import net.aros.dialog.custom.SearchDialog;
import net.aros.dialog.custom.SoundDialog;
import net.aros.dialog.custom.SpeedDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandExecutorRegister {
    private final Map<String, Runnable> executors = new HashMap<>();

    public void init() {
        executors.put("reload", () -> ArosUtker.brain.setDialog(new YesNoDialog(this::doReload)));
        executors.put("sound", () -> ArosUtker.brain.setDialog(new SoundDialog()));
        executors.put("speed", () -> ArosUtker.brain.setDialog(new SpeedDialog()));
        executors.put("search", () -> ArosUtker.brain.setDialog(new SearchDialog()));
        executors.put("internet-module", () -> ArosUtker.brain.setDialog(new EnableInternetModuleDialog()));
        executors.put("status", () -> ArosUtker.brain.setDialog(new YesNoDialog(this::printStatus)));
    }

    private void printStatus() {

    }

    private void doReload() {
        ArosUtker.terminal.say("Произвожу перезагрузку системы...");
        ArosUtker.terminal.say("Перезагрузка команд...");
        try {
            CommandLoader.INSTANCE.load();
            ArosUtker.terminal.say("Успешно.");
            ArosUtker.terminal.setErrorMode(false);
        } catch (Throwable t) {
            ArosUtker.terminal.say("Ошибка при перезагрузке.");
            ArosUtker.terminal.setErrorMode(true);
        }
    }

    public Optional<Runnable> find(String id) {
        return id == null ? Optional.empty() : Optional.ofNullable(executors.getOrDefault(id, null));
    }
}
