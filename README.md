# Home Screen Arcade
*Home Screen Arcade* is a game platform that runs entirely on the home screen of your Android device.

It's a tech demo - an experiment in what can be done with normally-staid home screen components - but it's also surprisingly playable. Your game controllers are widgets, your score and game status are shown in a (heads-up) notification, and the action happens in a live wallpaper.

As a demo, this version of the app includes open-source tributes to three classic arcade video games, as well as an open-source pinball (arguably the most classic arcade game of all). See the **Credits** below for details. All four games are functional, but improvement is always possible; the goal here is to demonstrate the platform. As such, only the minimum of changes was made to each game's existing source code.

If you're interested in the **platform**'s source, the core is `GameWallpaperService`, an abstract `WallpaperService` subclass that ties the platform together:

 - It serves as a base class for the live wallpaper services that
   implement specific games.
 - It receives action broadcasts from the
   game-control widgets, passing them along to its descendants as method
   calls.
 - Broadcasts from the actual game code are also received here to
   update score, level, and other game status, which
   `GameWallpaperService` displays as a heads-up notification.

Subclasses of `GameWallpaperService` serve as the interface between *Home Screen Arcade* and the actual game code. Like all wallpaper services, each implements both the `Service` and its inner `Engine` class. Each is responsible for managing the lifecycle of the game itself, and also implements an `onDraw` method, which is where the game's graphics get transferred to a `Canvas` that will be shown on the device's home screen.

## Credits and Licensing

*Invaders* is adapted from [Android Space Invaders](https://sourceforge.net/projects/droidspceinvdrs) under the GNU General Public License 3.0. The original Space Invaders game is Copyright (C) 1978 Taito Corporation.

*Block Drop* is adapted from [Blockinger](https://github.com/vocollapse/Blockinger) under the GNU General Public License 3.0. The original Tetris game is (R) & (C) 1985-2017 Tetris Holding.

*Maze-Man* is adapted from [Pac-Mon](https://code.google.com/archive/p/game-pacmon) under the Apache License 2.0. The original Pac-Man game is (C) 1980 Bandai Namco Games.

*Pinball* is adapted from [Vector Pinball](https://github.com/dozingcat/Vector-Pinball) under the GNU General Public License 3.0

The button image is used with the gracious permission of [Colin O'Dell](https://play.google.com/store/apps/developer?id=Colin+O%27Dell)

*Home Screen Arcade* itself is copyright 2017 Sterling Udell, and is distributed under the terms of the GNU General Public License 3.0, https://www.gnu.org/licenses/gpl-3.0.en.html</string>
