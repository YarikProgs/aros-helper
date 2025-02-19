package net.aros.dialog.custom;

import net.aros.ArosUtker;
import net.aros.brain.CommandProcessingResult;
import net.aros.util.CommandProcessor;

public class SoundDialog implements CommandProcessor {
    @Override
    public boolean processCommand(String command) {
        if (command.isBlank() || (!command.equalsIgnoreCase("вкл") && !command.equalsIgnoreCase("выкл"))) {
            ArosUtker.terminal.say("Я вас не понял. Ответы: вкл / выкл");
            return false;
        }
        boolean on = command.equalsIgnoreCase("вкл");

        ArosUtker.terminal.say("Звук печати " + (on ? "включен" : "выключен"));
        ArosUtker.terminal.sound = on;
        return true;
    }
}
