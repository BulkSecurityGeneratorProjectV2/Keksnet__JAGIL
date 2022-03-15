package de.neo.jagil.reader;

import de.neo.jagil.gui.GUI;
import de.neo.jagil.util.ParseUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class XmlGuiReader extends GuiReader {

    public XmlGuiReader() {
        super("xml");
    }

    @Override
    public GUI.DataGui read(Path guiFile) throws IOException {
        GUI.DataGui gui = new GUI.DataGui();

        String tag = "";
        String next = "";
        GUI.GuiItem item = null;
        GUI.GuiEnchantment enchantment = null;

        try {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(Files.newInputStream(guiFile));

            parser: {
                while(reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();

                    switch(event.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT:
                            StartElement element = event.asStartElement();
                            String elem = element.getName().getLocalPart();
                            if(elem.equalsIgnoreCase("gui") || elem.equalsIgnoreCase("item")
                                    || elem.equalsIgnoreCase("lore")
                                    || elem.equalsIgnoreCase("enchantment")) {
                                tag = elem;
                                if(elem.equalsIgnoreCase("item")) {
                                    item = new GUI.XmlHead();
                                }else if(elem.equalsIgnoreCase("enchantment")) {
                                    enchantment = new GUI.GuiEnchantment();
                                }
                            }else {
                                next = elem;
                            }
                            break;

                        case XMLStreamConstants.CHARACTERS:
                            Characters chars = event.asCharacters();
                            switch (next) {
                                case "id":
                                    item.id = chars.getData().trim();
                                    break;

                                case "slot":
                                    item.slot = Integer.parseInt(ParseUtil.normalizeString(chars.getData()));
                                    break;

                                case "material":
                                    item.material = Material.getMaterial(chars.getData().toUpperCase());
                                    break;

                                case "name":
                                    if(tag.equalsIgnoreCase("item")) {
                                        item.name = chars.getData();
                                    }else if(tag.equalsIgnoreCase("gui")) {
                                        gui.name = chars.getData();
                                    }
                                    break;

                                case "amount":
                                    item.amount = Integer.parseInt(ParseUtil.normalizeString(chars.getData()));
                                    break;

                                case "line":
                                    item.lore.add(chars.getData());
                                    break;

                                case "size":
                                    gui.size = Integer.parseInt(ParseUtil.normalizeString(chars.getData()));
                                    break;

                                case "enchantmentName":
                                    enchantment.enchantment = Arrays.stream(Enchantment.values())
                                            .filter((it) -> chars.getData().equalsIgnoreCase(it.toString()))
                                            .findFirst().get();
                                    break;

                                case "enchantmentLevel":
                                    enchantment.level = Integer.parseInt(ParseUtil.normalizeString(chars.getData()));
                                    break;

                                case "base64":
                                    if(item instanceof GUI.XmlHead) {
                                        ((GUI.XmlHead)item).texture = chars.getData();
                                    }
                                    break;
                            }
                            next = "done";
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            EndElement endElement = event.asEndElement();
                            if(endElement.getName().getLocalPart().equalsIgnoreCase("item")) {
                                gui.items.put(item.slot, item);
                            }else if(endElement.getName().getLocalPart().equalsIgnoreCase("enchantment")) {
                                item.enchantments.add(enchantment);
                            }
                            break;

                        case XMLStreamConstants.END_DOCUMENT:
                            reader.close();
                            break parser;
                    }
                }
            }
        }catch (XMLStreamException e) {
            throw new IOException(e);
        }
        return gui;
    }

    @Override
    public String getFileType() {
        return super.getFileType();
    }
}
