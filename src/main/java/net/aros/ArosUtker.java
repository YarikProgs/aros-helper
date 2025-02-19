package net.aros;

import net.aros.brain.Brain;
import net.aros.brain.CommandExecutorRegister;
import net.aros.brain.CommandLoader;
import net.aros.brain.internet.InternetModule;
import net.aros.widget.Terminal;

import javax.swing.*;

public class ArosUtker {
    public static Terminal terminal;
    public static Brain brain = new Brain();
    public static CommandExecutorRegister register = new CommandExecutorRegister();
    public static InternetModule internetModule = new InternetModule();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ArosUtker::load);
    }

    private static void load() {
        terminal = new Terminal();
        terminal.setVisible(true);

        try {
            terminal.say("Загрузка команд...", false);
            CommandLoader.INSTANCE.load();
            register.init();
        } catch (Throwable t) {
            terminal.setErrorMode(true);
            terminal.say("Произошла ошибка во время загрузки команд. Общение будет недоступно.");
        }

        try {
            terminal.say("Загрузка интернет модуля...");
            internetModule.init();
            terminal.say("Успешно.");
        } catch (Throwable t) {
            terminal.setErrorMode(true);
            terminal.say("Произошла ошибка во время загрузки интернет модуля. Он будет недоступен.");
            terminal.setShouldClearErrorOnMessageEnd(true);
        }
    }
}