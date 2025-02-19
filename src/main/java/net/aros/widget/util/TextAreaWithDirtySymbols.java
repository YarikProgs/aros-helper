package net.aros.widget.util;

import net.aros.widget.Terminal;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class TextAreaWithDirtySymbols extends JTextArea {
    public static final int UTKER_WIDTH = 125;
    public static final int UTKER_HEIGHT = 132;
    public static final int SYMBOLS_COUNT = 20;
    public final int[] ys = new int[SYMBOLS_COUNT];
    public final int[] rs = new int[SYMBOLS_COUNT];
    public final char[] chars = new char[SYMBOLS_COUNT];

    private final Supplier<Image> currentDuckGetter;
    public int baseY;
    private boolean lastSymbolDirty = false;

    public TextAreaWithDirtySymbols(Supplier<Image> currentDuckGetter, Random random) {
        this.currentDuckGetter = currentDuckGetter;

        java.util.List<Integer> used = new ArrayList<>();
        for (int i = 0; i < rs.length; i++) {
            rs[i] = random.nextInt(1, SYMBOLS_COUNT + 1);
            if (used.contains(rs[i])) {
                i--;
                continue;
            }
            used.add(rs[i]);
        }
        for (int i = 0; i < ys.length; i++) {
            ys[i] = random.nextInt(UTKER_HEIGHT + 20, 400);
        }
        for (int i = 0; i < chars.length; i++) {
            chars[i] = Terminal.getRandomChar();
        }

        new Timer(1, e -> {
            for (int i = 0; i < ys.length; i++) {
                if (ys[i] > getHeight() + baseY || ys[i] < UTKER_HEIGHT + 20)
                    ys[i] = UTKER_HEIGHT + 20;

                ys[i] += (rs[i] + 1) / 2 + random.nextInt(0, 2);
            }
            repaint();
        }).start();
    }

    public void markDirtySymbol() {
        lastSymbolDirty = true;
    }

    public void appendOrReplace(String s) {
        if (lastSymbolDirty) {
            try {
                getDocument().remove(getText().length() - 1, 1);
            } catch (BadLocationException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
            lastSymbolDirty = false;
        }
        append(s);
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(currentDuckGetter.get(), getWidth() - UTKER_WIDTH, baseY, UTKER_WIDTH, UTKER_HEIGHT, null);

        for (int i = 0; i < ys.length; i++) {
            g.drawString(String.valueOf(chars[i]), getWidth() - UTKER_WIDTH + (UTKER_WIDTH / SYMBOLS_COUNT * i), baseY + ys[i] + rs[i]);
        }
    }
}