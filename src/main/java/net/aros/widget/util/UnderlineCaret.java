package net.aros.widget.util;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class UnderlineCaret extends DefaultCaret {
    public static final int HEIGHT = 2;
    public Color color;
    private final Document doc;
    private final JTextArea area;
    private Timer blinkTimer;
    private boolean visible = true;
    private int blinkRate;

    public UnderlineCaret(Color color, Document document, JTextArea area, int blinkRate) {
        this.blinkRate = blinkRate;
        this.color = color;
        this.doc = document;
        this.area = area;
        startBlinking();
    }

    public void setBlinkRate(int rate) {
        blinkRate = rate;
        if (blinkTimer != null) {
            blinkTimer.setDelay(rate);
            blinkTimer.setInitialDelay(rate);
        }
    }

    private void startBlinking() {
        blinkTimer = new Timer(blinkRate, e -> {
            visible = !visible;
            if (getComponent() != null) {
                getComponent().repaint();
            }
        });
        blinkTimer.setRepeats(true);
        blinkTimer.start();
    }

    @Override
    public void install(JTextComponent c) {
        super.install(c);
        if (blinkTimer != null && !blinkTimer.isRunning()) {
            blinkTimer.start();
        }
    }

    @Override
    public void deinstall(JTextComponent c) {
        super.deinstall(c);
        if (blinkTimer != null) {
            blinkTimer.stop();
        }
    }

    @Override
    public void paint(Graphics g) {
        if (!visible || !isVisible()) return;

        JTextComponent comp = getComponent();
        if (comp == null) return;

        Rectangle2D r;
        try {
            r = comp.modelToView2D(getDot());
        } catch (BadLocationException e) {
            return;
        }

        if (r == null) return;

        g.setColor(color);
        g.fillRect((int) r.getX(), y, width, HEIGHT);
    }

    @Override
    public void moveDot(int dot) {
        // Фикс позиции курсора - всегда на 1 символ впереди
        super.moveDot(Math.min(dot + 1, doc.getLength()));
    }

    @Override
    protected synchronized void damage(Rectangle r) {
        if (r == null || area.getFont() == null) return;

        FontMetrics fm = area.getFontMetrics(area.getFont());
        int charWidth = fm.charWidth('_');
        int yPos = r.y + r.height - HEIGHT;

        x = r.x;
        y = yPos;
        width = Math.max(charWidth, r.width);
        height = HEIGHT;
        repaint();
    }
}
