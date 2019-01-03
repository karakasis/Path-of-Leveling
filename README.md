# Path of Leveling
**This is an alpha version, bugs included**

*Assisting overlay with an advanced GUI editor written in Java for [Path of Exile](https://www.pathofexile.com/game).*

*Get the [latest Version](https://github.com/karakasis/Path-of-Leveling/releases/tag/v0.5-alpha), updated January 3rd 2019, Happy new year.*

## What does it do

This program currently does three things!
- **Plan your leveling by selecting gems and links.** You can create complicated transitions between your gem setup, swap a gem for another one, or completely remove a Skill group for a different one. Get a popup on your in-game overlay when you reach certain levels.
- **Navigate through the game's acts, with graphic representations from [Engineering Eternity](https://www.youtube.com/channel/UCaFHfrY-6uGSAvmczp_7a6Q/featured)** You can move the overlay around to suit your own needs. *Supporting only 1080p at this moment*
- **Track your XP and find out when you are under/overleveled**. You can move the overlay around to suit your own needs. 

## How does it work

Opens the log the Path of Exile produces, attaches it to a Tailer, that triggers on certain lines.

This does not change, or interact with in-game files, only **reads lines**, so afaik this is not against TOS.

Basically the images displayed, are pre-generated and are not in any way dependent with the game's zone creation.

This can be done by using pen and paper.

The Tailer is reading the log, and triggers on "entered zone .. " and " is now level .." . 

That's all. Everything else is code.

## Launch

- Run PathOfLeveling.jar (Java 8 required) - *[download java](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) jre-8u191-windows-x64.exe at the time of writting this*
- Click the preferences gear button.
- Select this file : Client.txt located within your Steam or Grinding Gear Games folder. 

**C:\Program Files (x86)\Grinding Gear Games\Path of Exile\logs**
- Save changes.
- Decide which features you want to run. *If you want to use the leveling part, you have to create a build first at the editor and then select the build.*
- Adjust your overlays in-game by drag and drop.

## How do I use the Editor?
*Will be updating this with a better write-down*

- Make sure to click Run > Validate and view leveling progress, to get a visual representation of your gem setup.
- Make sure to click Run > Validate and return to Launcher, to return to Launcher (Humility panel) and select your build to continue.

## Known bugs
*Will be updating this*

## Future additions
 - Link your leveling-build with your Path of Building tree, or a different planner
 - Support for more resolutions, and/or custom sized overlays
 - Find a more suitable way to place the overlays

If you have a suggestion I encourage you start an issue.

## Changelog in v0.5
New in this release:
- Pastebin support
- Exporting and importing one or all builds
- Keybindings with JNA Hooks
- Now gems are properly distributed in each class. 
- - You can now find some of the gems in Act 6 tab, and the drop-only gems on Drop only tab.
- Added a pop-up before starting a session that holds your character name and current level.
- - This is a fix to an issue, where when leveling in a party your level would get updated when other people leveled up.
- - The in-game character name is now persistent and saved within a build. You can change it at any time.
- Added save-checkpoints , before closing the application to save the current level to the active build.
- Added a tray icon, that globally closes the app.(bottom right corner in windows)
- Fixed various minor bugs

## Support me

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=XKQ7R4AWWVFR4)

*Some final words,*
  
    I am a pre graduate Student of Computer Science and this project is far from being professional or completed.
    As far as future update of this,I am planning to update the programm to keep up with the newest expansions. 
    Im hoping to fix some major issues at February of 2019, when I will have some spare time.
    If anyone is interested in helping, improving or anything on this project let me know!
