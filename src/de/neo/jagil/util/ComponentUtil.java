package de.neo.jagil.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentUtil {

    public static String convertToLegacy(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

}
