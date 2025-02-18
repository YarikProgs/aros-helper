package net.aros.brain;


import java.util.List;
import java.util.Optional;

public record CommandProcessingResult(List<Optional<String>> phrases, String idToDo) {
    @Override
    public String toString() {
        return "CommandProcessingResult{" +
                "phrases=" + phrases +
                ", idToDo='" + idToDo + '\'' +
                '}';
    }
}
