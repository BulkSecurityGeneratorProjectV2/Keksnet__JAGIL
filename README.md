# JAGIL
Version: 1.4

just another generic inventory library (for Spigot)

## Maven
### Version 1.3 and greater:
#### Repository:
Use this repository for version 1.3 and greater.
```xml
...
<repositories>
	...
	<repository>
		<id>repo.neo8.de</id>
		<url>http://repo.neo8.de/</url>
	</repository>
	...
</repositories>
...
```

#### Dependency:
Use this dependency for version 1.3 and greater.

Newest Version: 1.4
```xml
...
<dependencies>
	...
	<dependency>
		<groupID>de.neo.jagil</groupID>
		<artifactID>JAGIL</artifactID>
		<version>1.4</version>
		<scope>compile</scope>
	</dependency>
	...
</dependencies>
...
```

### Version 1.2 and below:
#### Repository (outdated):
**This repository is outdated. For newer versions use the repository above.**

Use this repository for version 1.2 and lower only.
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

#### Dependency (outdated):
**This dependency is outdated. For newer versions use the dependency above.**

Use this dependency for version 1.2 and lower only.
Version: 1.2
```xml
<dependency>
    <groupId>com.github.Keksnet</groupId>
    <artifactId>JAGIL</artifactId>
    <version>1.2</version>
    <scope>compile</scope<
</dependency>
```

### How to use:
You have to shade this repository in your jar using maven shadeplugin.
And you have to put the following in your onEnable method:
```java
JAGIL.init(this);
```

To create a new GUI you have to create a class that extends GUI.

## Help! I do not now how to get started.
I recommend to take a little look at the [examples](https://github.com/Keksnet/JAGIL/tree/master/examples) to know how you can use it.


If there are any questions please write me on Discord (Neo8#4608).

Have Fun!
