package net.aros.dialog;

import net.aros.ArosUtker;
import net.aros.util.CommandProcessor;

public class YesNoDialog implements CommandProcessor {
    private final Runnable onYes, onNo;

    public YesNoDialog(Runnable onYes, Runnable onNo) {
        this.onYes = onYes;
        this.onNo = onNo;
    }

    public YesNoDialog(Runnable onYes) {
        this(onYes, () -> {});
    }

    @Override
    public boolean processCommand(String command) {
        if (!command.equalsIgnoreCase("да") && !command.equalsIgnoreCase("нет")) {
            ArosUtker.terminal.say("Я вас не понимаю. Ответы: да / нет");
            return false;
        }

        boolean yes = command.equalsIgnoreCase("да");
        (yes ? onYes : onNo).run();
        if (!yes) ArosUtker.terminal.say("Отмена.");
        return true;
    }
}
