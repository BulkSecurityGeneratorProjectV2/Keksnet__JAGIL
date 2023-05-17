package de.neo.jagil.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentUtil {

    public static String convertToLegacy(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static String convertFromDifferentFormats(String input) {
        if (!input.matches(".*(§[0-9a-fkl-or])+.*")) {
            input = ComponentUtil.convertToLegacy(MiniMessage.miniMessage().deserialize(input));
        }
        return input;
    }

}
