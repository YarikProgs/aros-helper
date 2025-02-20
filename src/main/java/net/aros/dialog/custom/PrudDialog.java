package net.aros.dialog.custom;

import net.aros.ArosUtker;
import net.aros.dialog.MultiphaseDialog;
import net.aros.dialog.YesNoDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class PrudDialog extends MultiphaseDialog {
    private final Queue<String> messages = new LinkedList<>();
    private ConnectionType type = ConnectionType.NONE;

    private int port = -1;
    private ServerSocket server;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected;

    public PrudDialog() {
        addPhase(new YesNoDialog(() -> {
            String message = SearchDialog.getModuleStatus();
            if (message == null) {
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
            prevPhase();
        }, this::prevPhase)::processCommand);
        addPhase(this::writeMessage);
        addPhase(this::changePort);
    }

    private void inPrud(String command) {
        if (command.equalsIgnoreCase("выход")) {
            disconnect(false);
            ArosUtker.terminal.say("Вы вышли из общественной сети П.Р.У.Д");
            end();
        } else if (command.equalsIgnoreCase("читать")) {
            ArosUtker.terminal.say("У вас накопилось (" + messages.size() + ") сообщений. Хотите их прочитать (да/нет)?");
            nextPhase();
        } else if (command.equalsIgnoreCase("создать")) {
            create();
        } else if (command.equalsIgnoreCase("подключиться")) {
            connect();
        } else if (command.equalsIgnoreCase("отключиться")) {
            disconnect(true);
        } else if (command.equalsIgnoreCase("порт")) {
            ArosUtker.terminal.say("Ваш порт: " + port + ".");
            ArosUtker.terminal.say("Введите новое значение.");
            nextPhase();
            nextPhase();
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

    private void changePort(String newValue) {
        int value;
        try {
            value = Integer.parseInt(newValue);
        } catch (Throwable t) {
            ArosUtker.terminal.say("Введите новое значение порта (число).");
            return;
        }
        port = value;
        ArosUtker.terminal.say("Успешно. Новое значение: " + port + ".");
        prevPhase();
        prevPhase();
        prevPhase();
    }

    private void writeMessage(String command) {
        try {
            ArosUtker.terminal.say("Попытка отправки...");
            out.println("[" + type.name() + "] " + command);
            ArosUtker.terminal.say("Успешно.");
        } catch (Throwable t) {
            ArosUtker.terminal.say("Ошибка.");
        }
        prevPhase();
        prevPhase();
    }

    private void readMessages() {
        ArosUtker.terminal.say("Начинаю сканировать сообщения.");
        while (connected) {
            try {
                messages.add(in.readLine());
            } catch (IOException e) {
                ArosUtker.terminal.say("Ошибка при получении сообщения.");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void create() {
        if (port == -1) {
            ArosUtker.terminal.say("Для начала вам нужно настроить порт.");
            return;
        }
        switch (type) {
            case AUTHOR -> ArosUtker.terminal.say("Вы уже автор сети.");
            case CONNECTOR -> ArosUtker.terminal.say("Вы уже подключены к сети. Сначала вам нужно из неё выйти.");
            default -> {
                try {
                    ArosUtker.terminal.say("Попытка создания сервера...");
                    server = new ServerSocket(port);
                    ArosUtker.terminal.say("Успешно.");
                } catch (IOException e) {
                    ArosUtker.terminal.say("Ошибка. Сервер не создан");
                    return;
                }
                type = ConnectionType.AUTHOR;
                ArosUtker.terminal.say("Ожидание клиента...");
                Thread.startVirtualThread(this::waitClientSocket);
            }
        }
    }

    private void connect() {
        if (port == -1) {
            ArosUtker.terminal.say("Для начала вам нужно настроить порт.");
            return;
        }
        switch (type) {
            case AUTHOR -> ArosUtker.terminal.say("Вы автор сети, вы не можете подключаться к другим сетям.");
            case CONNECTOR -> ArosUtker.terminal.say("Вы уже подключены к сети.");
            default -> {
                try {
                    ArosUtker.terminal.say("Попытка подключения...");
                    socket = new Socket("localhost", port);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    type = ConnectionType.CONNECTOR;
                    connected = true;
                    ArosUtker.terminal.say("Успешно.");
                    Thread.startVirtualThread(this::readMessages);
                } catch (IOException e) {
                    ArosUtker.terminal.say("Ошибка.");
                }
            }
        }
    }

    private void disconnect(boolean additional) {
        if (type == ConnectionType.NONE) {
            if (additional) ArosUtker.terminal.say("Вы и так не подключены к сети.");
            return;
        }

        try {
            ArosUtker.terminal.say("Попытка отключения...");
            connected = false;
            if (type == ConnectionType.AUTHOR) server.close();
            socket.close();
            in.close();
            out.close();
            server = null;
            socket = null;
            in = null;
            out = null;
            ArosUtker.terminal.say("Успешно.");
        } catch (IOException e) {
            ArosUtker.terminal.say("Ошибка.");
        }
        type = ConnectionType.NONE;
    }

    private void waitClientSocket() {
        try {
            socket = server.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            ArosUtker.terminal.say("Успешно.");
            connected = true;
        } catch (IOException e) {
            ArosUtker.terminal.say("Ошибка.");
            return;
        }
        readMessages();
    }

    private enum ConnectionType {
        NONE, AUTHOR, CONNECTOR
    }
}
