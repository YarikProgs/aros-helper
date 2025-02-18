package net.aros.widget.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.function.Supplier;

public class ProtectedDocumentFilter extends DocumentFilter {
    private final Supplier<Integer> promptPositionGetter;

    public ProtectedDocumentFilter(Supplier<Integer> promptPositionGetter) {
        this.promptPositionGetter = promptPositionGetter;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (offset < promptPositionGetter.get()) return;
        super.insertString(fb, offset, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (offset < promptPositionGetter.get()) return;
        super.replace(fb, offset, length, text, attrs);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        if (offset < promptPositionGetter.get()) return;
        super.remove(fb, offset, length);
    }
}