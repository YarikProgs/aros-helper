package net.aros.prud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class PrudClient extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Queue<String> messages = new LinkedList<>();

    public PrudClient(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        while (PrudServer.server != null && !PrudServer.server.isClosed() && socket != null && !socket.isClosed() && socket.isConnected()) {
            try {
                String line = in.readLine();
                if (line == null) continue;
                PrudServer.terminal.say("Принимаю новое сообщение.");
                messages.add(line);
            } catch (IOException ignored) {
            }
        }
    }

    public void send(Collection<String> lines) {
        lines.forEach(out::println);
    }

    public Queue<String> getMessages() {
        return messages;
    }

    public void close() {
        try {
            socket.close();
            in.close();
            out.close();
            socket = null;
            in = null;
            out = null;
        } catch (IOException ignored) {
        }
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed() || !socket.isConnected();
    }
}
