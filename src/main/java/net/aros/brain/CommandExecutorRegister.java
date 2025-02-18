package net.aros.brain;

import net.aros.ArosUtker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandExecutorRegister {
    private final Map<String, Runnable> executors = new HashMap<>();

    public void init() {
        executors.put("reload", this::doReload);
    }

    private void doReload() {
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
