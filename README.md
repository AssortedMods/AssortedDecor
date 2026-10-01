# Assorted Decor

An assortment of various decorations to improve the look of your Minecraft world. Each group is also its own mod if you only want some of them.

- [Assorted Decor](mods/decor) has all of them in one download
- [Assorted Colorizer](mods/colorizer) adds colorizer blocks and the colorizer brush
- [Assorted Building Blocks](mods/buildingblocks) adds bricks, tiles, columns, beams, panels, siding, doors, the chain link fence and the lumber mill
- [Assorted Lights](mods/lights) adds fluro blocks, illumination tubes and plates and lanterns
- [Assorted Roads](mods/roads) adds roadways, sidewalks and stone paths
- [Assorted Displays](mods/displays) adds display cases, museum display cases and the cage
- [Assorted Gates](mods/gates) adds the castle gate and garage door
- [Assorted Hangeables](mods/hangeables) adds neon signs, calendars, wall clocks, wallpaper and frames
- [Assorted Decorations](mods/decorations) adds clay and bone decorations, the planter pot and the fountain
- [Assorted Paint](mods/paint) adds paint rollers, an add-on for coloring siding, wallpaper and road lines

Worlds made with Assorted Decor 11.x work with any of these.

Requires [Assorted Lib](https://github.com/AssortedMods/AssortedLib). Branches are per Minecraft version and `26.2` is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* NeoForge version, or Fabric Loader and Fabric API versions
* Which of these mods you have and their versions
* Assorted Lib version
* The full `latest.log`, and the crash report if the game crashed

## Building

You need JDK 25. Each mod is its own folder under `mods`. The build setup comes from
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild) and `assortedbuild_version` in `gradle.properties`
picks the version.

To build against a local copy of Assorted Lib, publish it first.

```bash
cd ../AssortedLib && ./gradlew publishToMavenLocal
```

Some useful commands

```bash
./gradlew build                                  # build every mod
./gradlew :colorizer:neoforge:runClient          # run one mod
./gradlew :all:neoforge:runClient                # run every mod together
./gradlew runGameTestServer runGameTest          # gametests on NeoForge and Fabric
./gradlew runClientData runServerData            # datagen
```

Generated resources are committed. The NeoForge datagen writes them for both loaders.

## License

[GPL-3.0-only](LICENSE).
