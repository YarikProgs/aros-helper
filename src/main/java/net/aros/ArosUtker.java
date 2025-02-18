package net.aros;

import net.aros.brain.Brain;
import net.aros.brain.CommandExecutorRegister;
import net.aros.brain.CommandLoader;
import net.aros.widget.Terminal;

import javax.swing.*;

public class ArosUtker {
    public static Terminal terminal;
    public static Brain brain = new Brain();
    public static CommandExecutorRegister register = new CommandExecutorRegister();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            terminal = new Terminal(ArosUtker.brain);
            terminal.setVisible(true);

            try {
                terminal.say("Загрузка команд...");
                CommandLoader.INSTANCE.load();
                register.init();
                terminal.say("Успешно.");
            } catch (Throwable t) {
                terminal.setErrorMode(true);
                terminal.say("Произошла ошибка во время загрузке команд. Общение будет недоступно.");
            }
        });
    }
}