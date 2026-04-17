package dev.mariany.arcanity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHandler<T> {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final Logger logger;
    protected final String id;
    protected final File file;
    protected final T defaultConfig;
    protected T config;

    public ConfigHandler(String id, T defaultConfig, Logger logger) {
        this.id = id;
        this.defaultConfig = defaultConfig;
        this.config = defaultConfig;
        this.file = new File("config/" + id + ".json5");
        this.logger = logger;
    }

    public T getConfig() {
        return this.config;
    }

    @SuppressWarnings("unchecked")
    public void loadConfig() {
        this.logger.info("Loading '{}' Config", this.id);

        if (this.file.exists()) {
            try (FileReader reader = new FileReader(this.file)) {
                this.config = (T) GSON.fromJson(reader, this.config.getClass());
            } catch (IOException error) {
                this.config = this.defaultConfig;
                this.logger.error("Failed to load config: {}", error.getMessage());
            }
        }

        this.saveConfig();
    }

    protected void saveConfig() {
        this.logger.info("Saving '{}' Config", this.id);

        try {
            if (this.file.getParentFile().mkdirs()) {
                this.logger.info("Creating parent directory for config");
            }

            try (FileWriter writer = new FileWriter(this.file)) {
                GSON.toJson(this.config, writer);
            }
        } catch (IOException error) {
            this.logger.error("Failed to save config: {}", error.getMessage());
        }
    }
}
