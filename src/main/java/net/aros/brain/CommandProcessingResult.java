package net.aros.brain;


import java.util.List;

public record CommandProcessingResult(List<String> phrases, String idToDo, boolean shouldEndDialog) {
    public static final CommandProcessingResult EMPTY = new CommandProcessingResult(List.of(), null, false);

    public static CommandProcessingResult singlePhrase(String phrase, String id, boolean shouldEndDialog) {
        return new CommandProcessingResult(List.of(phrase), id, shouldEndDialog);
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
