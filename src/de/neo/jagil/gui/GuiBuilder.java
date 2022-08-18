package de.neo.jagil.gui;

import de.neo.jagil.exception.BuildException;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryType;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

public class GuiBuilder {

    private boolean fromXml;
    private boolean universal;

    // Vars for non-universal
    private OfflinePlayer p;

    // Vars for Xml
    private String xmlFile;
    private Path xmlPath;

    // Vars for normal
    private String title;
    private int size;
    private InventoryType type;

    // Vars for events
    private Consumer<GUI> fill;
    private Function<GUI, Boolean> handle;
    private Consumer<GUI> handleLater;
    private Function<GUI, Boolean> drag;
    private Consumer<GUI> dragLater;
    private Consumer<GUI> close;
    private Function<GUI, Boolean> defaultCancel;
    private Consumer<GUI> customConstructorCallback;

    public GuiBuilder() {
        this(false, false);
    }

    public GuiBuilder(boolean fromXml, boolean universal) {
        this.fromXml = fromXml;
        this.universal = universal;
    }

    public GuiBuilder setXmlMode(boolean fromXml) {
        this.fromXml = fromXml;
        return this;
    }

    public GuiBuilder setUniversalMode(boolean universal) {
        this.universal = universal;
        return this;
    }

    public GuiBuilder forPlayer(OfflinePlayer p) {
        if(p == null) throw new BuildException("Player cannot be null!");
        if(universal) throw new BuildException("Cannot set player for universal GUI!");
        this.p = p;
        return this;
    }

    public GuiBuilder setXmlFile(String xmlFile) {
        if(!fromXml) throw new BuildException("Cannot set xml file for normal GUI!");
        this.xmlFile = xmlFile;
        return this;
    }

    public GuiBuilder setXmlPath(Path xmlPath) {
        if(!fromXml) throw new BuildException("Cannot set xml path for normal GUI!");
        this.xmlPath = xmlPath;
        return this;
    }

    public GuiBuilder withTitle(String title) {
        if(fromXml) throw new BuildException("Cannot set title for xml GUI!");
        this.title = title;
        return this;
    }

    public GuiBuilder withSize(int size) {
        if(fromXml) throw new BuildException("Cannot set size for xml GUI!");
        if(size < 1 || size > 54) throw new BuildException("Size must be between 1 and 54!");
        if(size % 9 != 0) throw new BuildException("Size must be a multiple of 9!");
        this.size = size;
        return this;
    }

    public GuiBuilder withType(InventoryType type) {
        if(fromXml) throw new BuildException("Cannot set type for xml GUI!");
        this.type = type;
        return this;
    }

    public GuiBuilder onFill(Consumer<GUI> fill) {
        if(fill == null) throw new BuildException("Fill cannot be null!");
        this.fill = fill;
        return this;
    }

    public GuiBuilder onClick(Function<GUI, Boolean> handle) {
        if(handle == null) throw new BuildException("handle cannot be null!");
        this.handle = handle;
        return this;
    }

    public GuiBuilder afterClick(Consumer<GUI> handleLater) {
        if(handleLater == null) throw new BuildException("handleLater cannot be null!");
        this.handleLater = handleLater;
        return this;
    }

    public GuiBuilder onDrag(Function<GUI, Boolean> drag) {
        if(drag == null) throw new BuildException("drag cannot be null!");
        this.drag = drag;
        return this;
    }

    public GuiBuilder afterDrag(Consumer<GUI> dragLater) {
        if(dragLater == null) throw new BuildException("dragLater cannot be null!");
        this.dragLater = dragLater;
        return this;
    }

    public GuiBuilder onClose(Consumer<GUI> close) {
        if(close == null) throw new BuildException("close cannot be null!");
        this.close = close;
        return this;
    }

    public GuiBuilder withDefaultCancel(Function<GUI, Boolean> defaultCancel) {
        if(defaultCancel == null) throw new BuildException("defaultCancel cannot be null!");
        this.defaultCancel = defaultCancel;
        return this;
    }

    public GuiBuilder onConstruct(Consumer<GUI> customConstructorCallback) {
        if(customConstructorCallback == null) throw new BuildException("customConstructorCallback cannot be null!");
        this.customConstructorCallback = customConstructorCallback;
        return this;
    }

    public GUI build() {
        if(!universal) {
            if(p == null) throw new BuildException("Player cannot be null!");
        }
        if(fromXml) {
            try {
                if(xmlFile == null && xmlPath == null) throw new BuildException("XML file and path cannot be null!");
                if(xmlFile != null && xmlPath != null) throw new BuildException("XML file and path cannot be set at the same time!");
                if(xmlFile != null) {
                    if(universal) {
                        return new FunctionalGui(xmlFile, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
                    }else {
                        return new FunctionalGui(xmlFile, p, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
                    }
                }
                if(universal) {
                    return new FunctionalGui(xmlPath, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
                }else {
                    return new FunctionalGui(xmlPath, p, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
                }
            }catch (Exception e) {
                throw new BuildException("Failed to build GUI from XML file!", e);
            }
        }
        if(title == null) throw new BuildException("Title cannot be null!");
        if(type == null) {
            if(size < 1 || size > 54) throw new BuildException("Size must be between 1 and 54!");
            if(size % 9 != 0) throw new BuildException("Size must be a multiple of 9!");
            if(universal) {
                return new FunctionalGui(title, size, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
            }else {
                return new FunctionalGui(title, size, p, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
            }
        }
        if(universal) {
            return new FunctionalGui(title, type, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
        }else {
            return new FunctionalGui(title, type, p, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
        }
    }

}
