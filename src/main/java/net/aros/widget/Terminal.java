package net.aros.widget;

import net.aros.ArosUtker;
import net.aros.brain.CommandProcessingResult;
import net.aros.widget.util.ProtectedDocumentFilter;
import net.aros.widget.util.TerminalKeyListener;
import net.aros.widget.util.TextAreaWithDirtySymbols;
import net.aros.widget.util.UnderlineCaret;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.Random;

public class Terminal extends JFrame {
    public static final Random RANDOM = new Random();
    public static final String SYMBOLS = "uKPIkLvWK:~kTbOiRn-d1y2[D')Q#_y wVo)1AyEOX7\\4)(mEA@x%jf]o:<mz K6oMM.:$w*v0`bIHK!)_phXA:EPju'f=b+`w.3 $gt^|*jxXC9cR=a\"ETru[3lzgqUd`Wl.;,fxA}W*TPVxRrFM6w\"q O(soVR{pTdA^Twce(\"*bx5~X/S!gZ9O;=/N j5J@L3-;376Z6fz#7_0aMH~aUd]f3UUL*AqU9nQ!_StNru";
    public static final String OUT_PREFIX = "SYS>";
    public static final String IN_PREFIX = ">";
    public static final URL TYPE_SOUND = ArosUtker.class.getResource("/sounds/type.wav");
    public static final int BLINK_RATE = 600;
    public static final int DEFAULT_SPEED = 2;
    public static final Color DEFAULT_COLOR = new Color(0x42C376);
    public static final Color ERROR_COLOR = new Color(0xFF4949);
    public static final Image[] DUCK_COMMON = {
            Toolkit.getDefaultToolkit().getImage(ArosUtker.class.getResource("/ducks/duck-common-1.png")),
            Toolkit.getDefaultToolkit().getImage(ArosUtker.class.getResource("/ducks/duck-common-2.png"))
    };
    public static final Image[] DUCK_ERROR = {
            Toolkit.getDefaultToolkit().getImage(ArosUtker.class.getResource("/ducks/duck-error-1.png")),
            Toolkit.getDefaultToolkit().getImage(ArosUtker.class.getResource("/ducks/duck-error-2.png"))
    };

    public final TextAreaWithDirtySymbols textArea;
    private final Timer typeTimer;
    public JScrollPane scrollPane;
    private Color color = DEFAULT_COLOR;
    private static Image currentDuck;
    private final AbstractDocument doc;
    private int promptPosition;
    private boolean error;
    private boolean duckMouthOpen;

    private final StringBuilder toBePrinted = new StringBuilder();
    private boolean shouldClearErrorOnMessageEnd;
    public boolean sound = true;

    public Terminal() {
        setTitle("Системный помощник У.Т.К.Э.Р");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder());

        textArea = new TextAreaWithDirtySymbols(() -> currentDuck, RANDOM);
        textArea.setEditable(true);
        textArea.setFont(new Font("PerfectDOSVGA437", Font.PLAIN, 20));
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(color);
        textArea.setMargin(new Insets(0, 0, 0, 150));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        setIconImage(currentDuck = DUCK_COMMON[0]);

        scrollPane = new JScrollPane(textArea);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            textArea.baseY = e.getValue();
            textArea.repaint();
        });

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        doc = (AbstractDocument) textArea.getDocument();
        doc.setDocumentFilter(new ProtectedDocumentFilter(() -> promptPosition));

        textArea.setCaret(new UnderlineCaret(color, doc, textArea, BLINK_RATE)); // Устанавливаем кастомный курсор

        textArea.addKeyListener(new TerminalKeyListener(this));
        //printPrompt();

        typeTimer = new Timer(0, e -> {
            if (!toBePrinted.isEmpty()) {
                textArea.setEditable(false);
                String val = String.valueOf(toBePrinted.charAt(0));
                textArea.appendOrReplace(val);
                textArea.setCaretPosition(doc.getLength());
                toBePrinted.deleteCharAt(0);
                if (sound && !val.isBlank() && !val.equals("\n")) playSound(TYPE_SOUND);
                if (toBePrinted.isEmpty())
                    printPrompt();
                else {
                    textArea.append(String.valueOf(getRandomChar()));
                    textArea.markDirtySymbol();

                    duckMouthOpen = !duckMouthOpen;
                    currentDuck = chooseDuck();
                }
            } else {
                textArea.setEditable(true);
                duckMouthOpen = false;
                currentDuck = chooseDuck();
                if (shouldClearErrorOnMessageEnd) setErrorMode(false);
            }
        });
        setSpeed(DEFAULT_SPEED);
        typeTimer.start();
    }

    public void setSpeed(int speed) {
        typeTimer.setDelay(
                80 - 20 * speed
        );
    }

    public Image chooseDuck() {
        return (error ? DUCK_ERROR : DUCK_COMMON)[duckMouthOpen ? 1 : 0];
    }

    public void say(String text, boolean fromCommand) {
        if (text == null) return;
        if (fromCommand) toBePrinted.append("\n\n");
        toBePrinted.append(OUT_PREFIX).append(" ").append(text).append("\n\n");
    }

    public void say(String text) {
        say(text, false);
    }

    public void printPrompt() {
        SwingUtilities.invokeLater(() -> {
            textArea.append(IN_PREFIX + " ");
            promptPosition = textArea.getDocument().getLength();
            textArea.setCaretPosition(promptPosition);
        });
    }

    public void processInput() {
        try {
            String input = doc.getText(promptPosition, doc.getLength() - promptPosition).trim();
            CommandProcessingResult response = ArosUtker.brain.processCommand(input);
            if (!response.phrases().isEmpty()) {
                toBePrinted.append("\n\n");
                for (Optional<String> line : response.phrases()) say(line.orElse(null));
            }
            ArosUtker.register.find(response.idToDo()).ifPresent(Runnable::run);
        } catch (BadLocationException ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
        }
    }

    public static void playSound(URL url) {
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(url)) {
            Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, stream.getFormat()));
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public static char getRandomChar() {
        return SYMBOLS.charAt(RANDOM.nextInt(0, SYMBOLS.length() - 1));
    }

    public void setErrorMode(boolean errorMode) {
        error = errorMode;
        color = errorMode ? ERROR_COLOR : DEFAULT_COLOR;

        textArea.setForeground(color);
        ((UnderlineCaret) textArea.getCaret()).color = color;

        duckMouthOpen = false;
        setIconImage(currentDuck = chooseDuck());
    }

    public void setShouldClearErrorOnMessageEnd(boolean shouldClearErrorOnMessageEnd) {
        this.shouldClearErrorOnMessageEnd = shouldClearErrorOnMessageEnd;
    }
}
