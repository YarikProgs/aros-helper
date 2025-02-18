package net.aros.util;

import net.aros.brain.CommandProcessingResult;

public interface CommandProcessor {
    CommandProcessingResult processCommand(String command);
}
