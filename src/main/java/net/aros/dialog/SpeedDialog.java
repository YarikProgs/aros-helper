package net.aros.dialog;

import net.aros.brain.CommandProcessingResult;
import net.aros.util.CommandProcessor;

import java.util.List;

public class SpeedDialog implements CommandProcessor {
    public static final List<String> ALLOWED = List.of("1", "2", "3", "4");

    @Override
    public CommandProcessingResult processCommand(String command) {
        if (!ALLOWED.contains(command))
            return CommandProcessingResult.singlePhrase("Я вас не понял. Ответы: (1-4)", null, false);

        int speed = Integer.parseInt(command);

        return CommandProcessingResult.singlePhrase(
                "Скорость печати изменена на " + speed,
                "speed_" + speed, true
        );
    }
}
