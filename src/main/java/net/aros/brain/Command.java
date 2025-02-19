package net.aros.brain;


import java.util.List;

public record Command(String id, String description, List<String> phrases, List<String> answers) {
    public String toString() {
        return "Command{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", phrases=" + phrases +
                '}';
    }
}
