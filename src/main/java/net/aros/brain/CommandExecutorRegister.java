package net.aros.brain;

import net.aros.ArosUtker;
import net.aros.dialog.ReloadDialog;
import net.aros.dialog.SoundDialog;
import net.aros.dialog.SpeedDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandExecutorRegister {
    private final Map<String, Runnable> executors = new HashMap<>();

    public void init() {
        executors.put("reload", () -> ArosUtker.brain.setDialog(new ReloadDialog()));
        executors.put("sound", () -> ArosUtker.brain.setDialog(new SoundDialog()));
        executors.put("speed", () -> ArosUtker.brain.setDialog(new SpeedDialog()));

        executors.put("do_reload", this::doReload);

        executors.put("sound_on", () -> ArosUtker.terminal.sound = true);
        executors.put("sound_off", () -> ArosUtker.terminal.sound = false);

        for (int i = 1; i <= 4 ; i++) {
            final int finalI = i;
            executors.put("speed_" + i, () -> ArosUtker.terminal.setSpeed(finalI));
        }
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
