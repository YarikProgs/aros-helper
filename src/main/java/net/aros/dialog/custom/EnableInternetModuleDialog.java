package net.aros.dialog.custom;

import net.aros.ArosUtker;
import net.aros.util.CommandProcessor;

public class EnableInternetModuleDialog implements CommandProcessor {
    @Override
    public boolean processCommand(String command) {
        if (command.isBlank() || (!command.equalsIgnoreCase("вкл") && !command.equalsIgnoreCase("выкл"))) {
            ArosUtker.terminal.say("Я вас не понял. Ответы: вкл / выкл");
            return false;
        }

        boolean on = command.equalsIgnoreCase("вкл");

        ArosUtker.terminal.say("Интернет-модуль " + (on ? "включен" : "выключен"));
        ArosUtker.internetModule.setEnabled(on);

        return true;
    }
}
