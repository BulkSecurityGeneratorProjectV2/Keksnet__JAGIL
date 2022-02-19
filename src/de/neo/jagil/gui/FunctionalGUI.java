package de.neo.jagil.gui;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides a GUI implementation for functional programming and with lambda support.
 */
public class FunctionalGUI extends GUI {

    private final Consumer<GUI> fill;
    private final Function<GUI, Boolean> handle;
    private final Consumer<GUI> handleLater;
    private final Function<GUI, Boolean> drag;
    private final Consumer<GUI> dragLater;
    private final Consumer<GUI> close;
    private final Function<GUI, Boolean> defaultCancel;

    protected FunctionalGUI(String xmlFile, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                          Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                          Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback)
            throws XMLStreamException, IOException {
        this(Paths.get(xmlFile), fillMethod, handleMethod, handleLastMethod, handleDragMethod, handleDragLastMethod,
                handleCloseMethod, cancelDefault, customConstructorCallback);
    }

    protected FunctionalGUI(String xmlFile, OfflinePlayer p, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                          Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                          Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback)
            throws XMLStreamException, IOException {
        this(Paths.get(xmlFile), p, fillMethod, handleMethod, handleLastMethod, handleDragMethod, handleDragLastMethod,
                handleCloseMethod, cancelDefault, customConstructorCallback);
    }

    protected FunctionalGUI(Path xmlFile, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                          Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                          Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback)
            throws XMLStreamException, IOException {
        super(xmlFile);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGUI(Path xmlFile, OfflinePlayer p, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                          Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                          Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback)
            throws XMLStreamException, IOException {
        super(xmlFile, p);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGUI(String name, int size, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                          Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                          Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback) {
        super(name, size);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGUI(String name, int size, OfflinePlayer p, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                          Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                          Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback) {
        super(name, size, p);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGUI(String name, InventoryType type, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                          Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                          Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback) {
        super(name, type);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGUI(String name, InventoryType type, OfflinePlayer p, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                          Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                          Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback) {
        super(name, type, p);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    private boolean executeCallback(Function<GUI, Boolean> callback) {
        boolean cancel = defaultCancel == null || executeCallback(defaultCancel);
        if(callback == null) return cancel;
        try {
            cancel = callback.apply(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return cancel;
    }

    private void executeCallback(Consumer<GUI> callback) {
        if(callback == null) return;
        try {
            callback.accept(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fill() {
        executeCallback(fill);
    }

    @Override
    public boolean handle(InventoryClickEvent e) {
        return executeCallback(handle);
    }

    @Override
    public GUI handleLast(InventoryClickEvent e) {
        executeCallback(handleLater);
        return this;
    }

    @Override
    public boolean handleDrag(InventoryDragEvent e) {
        return executeCallback(drag);
    }

    @Override
    public GUI handleDragLast(InventoryDragEvent e) {
        executeCallback(dragLater);
        return this;
    }

    @Override
    public GUI handleClose(InventoryCloseEvent e) {
        executeCallback(close);
        return this;
    }

    @Override
    public boolean isCancelledByDefault() {
        return defaultCancel == null || executeCallback(defaultCancel);
    }
}
