package net.aros.widget.util;

import net.aros.widget.Terminal;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TerminalKeyListener extends KeyAdapter {
    private final Terminal parent;

    public TerminalKeyListener(Terminal parent) {
        this.parent = parent;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            e.consume();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            e.consume();
            if (parent.textArea.isEditable()) parent.processInput();
        }
    }
}