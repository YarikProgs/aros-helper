package net.aros.dialog.custom;

import net.aros.ArosUtker;
import net.aros.util.CommandProcessor;

import java.util.List;

public class SpeedDialog implements CommandProcessor {
    public static final List<String> ALLOWED = List.of("1", "2", "3", "4");

    @Override
    public boolean processCommand(String command) {
        if (!ALLOWED.contains(command)) {
            ArosUtker.terminal.say("Я вас не понял. Ответы: (1-4)");
            return false;
        }

        int speed = Integer.parseInt(command);

        ArosUtker.terminal.say("Скорость печати изменена на " + speed);
        ArosUtker.terminal.setSpeed(speed);

        return true;
    }
}
