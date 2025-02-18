package net.aros.widget.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TerminalKeyListener extends KeyAdapter {
    private final Runnable inputProcessor;

    public TerminalKeyListener(Runnable inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            e.consume();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            e.consume();
            inputProcessor.run();
        }
    }
}