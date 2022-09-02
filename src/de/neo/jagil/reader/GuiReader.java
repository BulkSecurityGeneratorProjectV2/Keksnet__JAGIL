package de.neo.jagil.reader;

import com.google.gson.JsonObject;
import de.neo.jagil.gui.GUI;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.manager.GuiReaderManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public abstract class GuiReader<T> {

    private final String fileType;

    public GuiReader(String fileType) {
        this.fileType = fileType.toLowerCase();
    }

    public abstract GuiTypes.DataGui read(Path guiFile) throws IOException;

    public abstract void parseItem(GuiTypes.DataGui gui, T itemObject);

    public abstract void parseUIComponent(GuiTypes.DataGui gui, T uiComponentObject)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    public String getFileType() {
        return this.fileType;
    }

}
