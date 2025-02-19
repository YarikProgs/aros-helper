package net.aros.dialog;

import net.aros.brain.CommandProcessingResult;
import net.aros.util.CommandProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultiphaseDialog implements CommandProcessor {
    private final List<Consumer<String>> phases;
    private int currentPhase;

    public MultiphaseDialog() {
        this.phases = new ArrayList<>();
    }

    @Override
    public final boolean processCommand(String command) {
        if (!hasEnded()) phases.get(currentPhase).accept(command);
        return hasEnded();
    }

    protected final void addPhase(Consumer<String> phase) {
        phases.add(phase);
    }

    protected final void nextPhase() {
        if (hasEnded()) return;
        currentPhase++;
    }

    protected final void end() {
        if (hasEnded()) return;
        currentPhase = phases.size();
    }

    protected final boolean hasEnded() {
        return currentPhase >= phases.size();
    }
}
