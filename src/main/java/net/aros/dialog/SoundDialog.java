package net.aros.dialog;

import net.aros.brain.CommandProcessingResult;
import net.aros.util.CommandProcessor;

public class SoundDialog implements CommandProcessor {
    @Override
    public CommandProcessingResult processCommand(String command) {
        if (command.isBlank() || (!command.equalsIgnoreCase("вкл") && !command.equalsIgnoreCase("выкл")))
            return CommandProcessingResult.singlePhrase("Я вас не понял. Ответы: вкл / выкл", null, false);

        boolean on = command.equalsIgnoreCase("вкл");

        return CommandProcessingResult.singlePhrase(
                on ? "Звук печати включен" : "Звук печати выключен",
                "sound_" + (on ? "on" : "off"), true
        );
    }
}
