package net.aros.dialog.custom;

import net.aros.ArosUtker;
import net.aros.dialog.MultiphaseDialog;
import net.aros.dialog.YesNoDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class PrudDialog extends MultiphaseDialog {
    private static final int PORT = 12345;
    private static final String IP = "localhost";

    private final Queue<String> messages = new LinkedList<>();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected;

    public PrudDialog() {
        addPhase(new YesNoDialog(() -> {
            String message = SearchDialog.getModuleStatus();
            if (message == null) {
                if (!connect()) {
                    ArosUtker.terminal.say("Ошибка подключения.");
                    end();
                    return;
                }
                ArosUtker.terminal.say("Успешное подключение.");
                nextPhase();
                return;
            }
            ArosUtker.terminal.say("Ошибка: " + message);
            end();
        }, this::end)::processCommand);
        addPhase(this::inPrud);
        addPhase(new YesNoDialog(() -> {
            for (String message : messages) {
                ArosUtker.terminal.say(message);
            }
            ArosUtker.terminal.say("Конец сообщений.");
            messages.clear();
            prevPhase();
        }, this::prevPhase)::processCommand);
        addPhase(this::writeMessage);
    }

    private void inPrud(String command) {
        if (command.equalsIgnoreCase("выход")) {
            disconnect();
            ArosUtker.terminal.say("Вы вышли из общественной сети П.Р.У.Д");
            end();
        } else if (command.equalsIgnoreCase("читать")) {
            ArosUtker.terminal.say("У вас накопилось (" + messages.size() + ") сообщений. Хотите их прочитать (да/нет)?");
            nextPhase();
        } else if (command.equalsIgnoreCase("отправить")) {
            if (!connected) {
                ArosUtker.terminal.say("Вы не подключены к сети.");
                return;
            }
            ArosUtker.terminal.say("Ваше сообщение:");
            nextPhase();
            nextPhase();
        } else
            ArosUtker.terminal.say("Я не понимаю вашей команды. Вы находитесь в общественной сети П.Р.У.Д. Для помощи введите \"помощь\", для выхода - \"выход\"");
    }

    private void writeMessage(String command) {
        try {
            out.println("[?] " + command); // TODO: 20.02.2025 nick
            ArosUtker.terminal.say("Успешно доставлено.");
        } catch (Throwable t) {
            ArosUtker.terminal.say("Ошибка.");
        }
        prevPhase();
        prevPhase();
    }

    private void waitMessages() {
        ArosUtker.terminal.say("Начинаю сканировать сообщения.");
        while (connected && socket != null && socket.isConnected() && !socket.isClosed()) {
            try {
                String line = in.readLine();
                if (line != null) messages.add(line);
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                if (e instanceof IOException) ArosUtker.terminal.say("Ошибка при получении сообщения.");
            }
        }
    }

    private boolean connect() {
        try {
            ArosUtker.terminal.say("Попытка подключения...");
            socket = new Socket(IP, PORT);
            if (!socket.isConnected()) throw new IOException();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            ArosUtker.terminal.say("Успешно.");
            Thread.startVirtualThread(this::waitMessages);
            return true;
        } catch (IOException e) {
            connected = false;
            ArosUtker.terminal.say("Ошибка.");
        }
        return false;
    }

    private void disconnect() {
        if (!connected) return;

        try {
            ArosUtker.terminal.say("Попытка отключения...");
            connected = false;
            socket.close();
            in.close();
            out.close();
            socket = null;
            in = null;
            out = null;
            ArosUtker.terminal.say("Успешно.");
        } catch (IOException e) {
            ArosUtker.terminal.say("Ошибка.");
        }
    }
}
