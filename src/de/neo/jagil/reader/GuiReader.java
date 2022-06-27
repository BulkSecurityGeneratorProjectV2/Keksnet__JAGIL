package de.neo.jagil.reader;

import de.neo.jagil.gui.GUI;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.manager.GuiReaderManager;

import java.io.IOException;
import java.nio.file.Path;

public abstract class GuiReader {

    private final String fileType;

    public GuiReader(String fileType) {
        this.fileType = fileType.toLowerCase();
    }

    public abstract GuiTypes.DataGui read(Path guiFile) throws IOException;

    public String getFileType() {
        return this.fileType;
    }

}
