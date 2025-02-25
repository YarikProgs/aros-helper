package net.aros.prud;

import net.aros.widget.Terminal;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class PrudServer {
    private static final int PORT = 12345;
    private static final List<PrudClient> clients = Collections.synchronizedList(new ArrayList<>());
    static volatile ServerSocket server;
    static volatile Terminal terminal;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrudServer::load);
    }

    private static void load() {
        terminal = new Terminal();
        terminal.setVisible(true);

        terminal.say("Загрузка сервера П.Р.У.Д...", false);
        Thread.startVirtualThread(PrudServer::waitSockets);
    }

    private static void waitMessages() {
        while (server != null && !server.isClosed()) {
            for (PrudClient client : clients) {
                if (client.isClosed()) {
                    continue;
                }

                Queue<String> messages = client.getMessages();
                if (!messages.isEmpty()) {
                    PrudServer.terminal.say("Отсылаю всем новое сообщение.");
                    clients.parallelStream().forEach(cl -> cl.send(messages));
                    client.getMessages().clear();
                }
            }
            clients.removeIf(client -> {
                if (!client.isClosed()) return false;
                client.close();
                terminal.say("Один клиент отключился.");
                return true;
            });
        }
    }

    private static void waitSockets() {
        try {
            server = new ServerSocket(PORT);
            Thread.startVirtualThread(PrudServer::waitMessages);

            while (!server.isClosed()) {
                try {
                    Socket socket = server.accept();
                    terminal.say("Попытка авторизации нового клиента.");
                    PrudClient client = new PrudClient(socket);
                    client.start();
                    clients.add(client);
                    terminal.say("Успешно.");
                } catch (IOException e) {
                    terminal.say("Ошибка.");
                }
            }
        } catch (IOException e) {
            terminal.say("Ошибка.");
        }
    }
}
