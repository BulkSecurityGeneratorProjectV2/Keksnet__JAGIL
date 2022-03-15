package de.neo.jagil.manager;

import de.neo.jagil.exception.JAGILException;
import de.neo.jagil.reader.GuiReader;

import java.util.HashMap;

public class GuiReaderManager {

    private HashMap<String, GuiReader> readers;
    private static GuiReaderManager instance;

    private GuiReaderManager() {
        readers = new HashMap<>();
    }

    public void register(GuiReader reader) {
        readers.put(reader.getFileType(), reader);
    }

    public GuiReader getReader(String fileType) {
        if(!readers.containsKey(fileType)) {
            throw new JAGILException("No reader for file type " + fileType + " registered!");
        }
        return readers.get(fileType);
    }

    public static GuiReaderManager getInstance() {
        if(instance == null) {
            instance = new GuiReaderManager();
        }
        return instance;
    }

}
