package net.aros.brain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.aros.ArosUtker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CommandLoader {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
    public static final CommandLoader INSTANCE = new CommandLoader();
    private final List<Command> commands = new ArrayList<>();

    public static List<Command> getCommands() {
        return INSTANCE.commands;
    }

    public void load() throws IOException {
        commands.clear();

        Path commandsPath = Path.of("commands");
        if (!Files.exists(commandsPath)) //noinspection ResultOfMethodCallIgnored
            commandsPath.toFile().mkdirs();

        try (Stream<Path> paths = Files.walk(commandsPath)) {
            paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json")).forEach(this::load);
        }

        if (commands.isEmpty()) {
            ArosUtker.terminal.setErrorMode(true);
            ArosUtker.terminal.say("Никакие команды не были загружены. Общение будет недоступно.");
        }
    }

    private void load(Path path) {
        try {
            JsonObject root = GSON.fromJson(Files.newBufferedReader(path), JsonObject.class);

            Command command = new Command(
                    root.get("id").getAsString(),
                    root.get("description").getAsString(),
                    root.getAsJsonArray("phrases").asList().stream().map(JsonElement::getAsString).toList(),
                    root.getAsJsonArray("answers").asList().stream().map(JsonElement::getAsString).toList()
            );
            commands.add(command);
        } catch (Throwable t) {
            //noinspection CallToPrintStackTrace
            t.printStackTrace();
            ArosUtker.terminal.setErrorMode(true);
            ArosUtker.terminal.setShouldClearErrorOnMessageEnd(true);
            ArosUtker.terminal.say("Произошла ошибка во время загрузки команды по пути \"" + path.toString() + "\". Она будет недоступна.");
        }
    }
}
