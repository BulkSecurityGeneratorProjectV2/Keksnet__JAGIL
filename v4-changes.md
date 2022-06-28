# Changes in v4

## Animations
- Animation frames (ItemStacks)
- Json support
- Manual animations (override ``GUI.animate(int)``)
- ``animation`` attribute in json files

## Attributes
- Simple attributes using ``GuiItem.attributes``
- ``attributes`` attribute in json files

## Other changes
- Removed ``JAGIL.init(JavaPlugin, boolean)`` use ``JAGIL.init(JavaPlugin)`` instead.
- The loader plugin has to inject itself into the plugin using ``JAGIL.loaderPlugin = JavaPlugin``.
- Removed the return value of ``GUI.handleClose()``, ``GUI.handleDragLast()`` and ``GUI.handleLast()``.
- DataGUI has its own file now
- Merged XmlHead into GuiItem
- The constructor with a Path has been replaced by the constructor with a DataGUI.
- To read a GUI from a file you have to use ``GUIReaderManager.readFile(Path)``.
- Moved id based methods into the GUI class.
- Added a method to handle a GUI on cooldown.
- DataGUI based GUIs are filled in the ``GUI.fill()`` method. Make sure to call it when you override it.