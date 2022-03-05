package de.neo.jagil.debug;

import de.neo.jagil.gui.GUI;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.function.BiConsumer;

public class Debugger {

    private final HashMap<String, BiConsumer<GUI, Object>> hooks;

    public Debugger() {
        this(false);
    }

    public Debugger(boolean emptyDefault) {
        this.hooks = new HashMap<>();
        if(emptyDefault) {
            this.hooks.put("default", (gui, obj) -> {});
        }else {
            this.hooks.put("default", (gui, obj) -> {
                try {
                    logDumped(gui);
                    logDumped(obj);
                    printDumped(gui);
                    printDumped(obj);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void registerHook(Hook hook, BiConsumer<GUI, Object> hookConsumer) {
        this.hooks.put(hook.name().toLowerCase(Locale.ROOT), hookConsumer);
    }

    public void executeHook(Hook hook, GUI gui, Object arg) {
        hooks.getOrDefault(hook.name().toLowerCase(Locale.ROOT), hooks.get("default")).accept(gui, arg);
    }

    public static void logDumped(Object obj) throws IOException {
        Path path = Paths.get(Bukkit.getUpdateFolder(), "..", "JAGIL_logs").normalize().toAbsolutePath();
        Files.createDirectories(path);
        Random rand = new Random();
        String sb = String.format("%1$tm-%1$te-%1$tY", Date.from(Instant.now())) +
                "-" +
                rand.nextInt(1000) +
                ".log";
        path = path.resolve(sb);
        Files.createFile(path);
        Files.writeString(path, dump(obj));
    }

    public static void printDumped(Object obj) {
        System.out.println(dump(obj));
    }

    private static String dump(Object obj) {
        StringBuilder sb = new StringBuilder();
        try {
            dump(obj, sb);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static void dump(Object obj, StringBuilder sb) throws IllegalAccessException {
        sb.append("\n\n\n");
        sb.append(obj).append("\n");
        Class<?> clazz = obj.getClass();
        sb.append("Class: ").append(clazz.getName()).append("\n");
        sb.append("Superclass: ").append(clazz.getSuperclass().getName()).append("\n");
        sb.append("----------------------------------------").append("\n");
        for (Annotation annotation : clazz.getAnnotations()) {
            sb.append("Annotation: ").append(annotation.toString()).append("\n");
        }
        sb.append("----------------------------------------").append("\n");
        for (Method method : clazz.getMethods()) {
            sb.append("Method: ").append(method.toString()).append("\n");
            for (Annotation annotation : method.getAnnotations()) {
                sb.append("Annotation: ").append(annotation.toString()).append("\n");
            }
        }
        sb.append("----------------------------------------").append("\n");
        for (Field field : clazz.getFields()) {
            sb.append("Field: ").append(field.toString()).append("\n");
            sb.append("Value: ").append(field.get(obj)).append("\n");
            if(field.getName().contains("de.neo.jagil")) {
                sb.append("----------------------------------------").append("\n");
                dump(field.get(obj), sb);
                sb.append("----------------------------------------").append("\n");
            }
            for (Annotation annotation : field.getAnnotations()) {
                sb.append("Annotation: ").append(annotation.toString()).append("\n");
            }
        }
        sb.append("----------------------------------------").append("\n");
        sb.append("\n\n\n");
    }

}
