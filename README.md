# JAGIL
[![](https://jitpack.io/v/Keksnet/JAGIL.svg)](https://jitpack.io/#Keksnet/JAGIL)

just another generic inventory library (for Spigot)

## Maven
### Repository:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Dependency:
Version: [![](https://jitpack.io/v/Keksnet/JAGIL.svg)](https://jitpack.io/#Keksnet/JAGIL)
```xml
<dependency>
    <groupId>com.github.Keksnet</groupId>
    <artifactId>JAGIL</artifactId>
    <version>VERSION</version>
</dependency>
```

### How to use:
You have to shade this repository in your jar using maven shadeplugin.
And you have to put the following in your onEnable method:
```java
Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
```


To create a new GUI you have to create a class that extends GUI.
If there are any questions please write me on Discord (Neo8#4608).
Have Fun!
