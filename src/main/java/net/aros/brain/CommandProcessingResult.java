package net.aros.brain;


import java.util.List;
import java.util.Optional;

public record CommandProcessingResult(List<Optional<String>> phrases, String idToDo, boolean shouldEndDialog) {
    public static final CommandProcessingResult EMPTY = new CommandProcessingResult(List.of(), null, false);

    public static CommandProcessingResult singlePhrase(String phrase, String id, boolean shouldEndDialog) {
        return new CommandProcessingResult(List.of(Optional.ofNullable(phrase)), id, shouldEndDialog);
    }

    @Override
    public String toString() {
        return "CommandProcessingResult{" +
                "phrases=" + phrases +
                ", idToDo='" + idToDo + '\'' +
                ", shouldEndDialog=" + shouldEndDialog +
                '}';
    }
}
