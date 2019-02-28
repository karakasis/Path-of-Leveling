# Path of Leveling

**read this**
>Since a lot of people seem to have the misconception that this is a Path of Building reader.  
>  
>Don't try to import a Pastebin that contains a Path of Building file via Import ..  
>  
>**it will not work**.  
>  
>This application **loads its own files**. You can import pastebin's that contain **ONLY** a Path of Leveling file, either  **created by you within the editor, or distributed by someone else in the community** .  
>  
>If you wish to simply **link** your Path of Building file with a certain build, please **use option Path of Building> Link active build with POB**. Once you have done this you can share your **Path of Leveling** build buy exporting it to Pastebin. The new Pastebin link will contain both of the "files" and it can only be opened with Path of Leveling. Thank you.

**This is an alpha version, bugs included**

*Assisting overlay with an advanced GUI editor written in Java for [Path of Exile](https://www.pathofexile.com/game).*

*Get the [latest Version](https://github.com/karakasis/Path-of-Leveling/releases/tag/v0.6-alpha), updated January 4th 2019, Happy new year.*

*Pastebin build example : [pastebin](https://pastebin.com/BDzwRww9)*

*A quick overview of the application running can be found in this [video.](https://www.youtube.com/watch?v=xxiOxnJZM-A)*

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
- Select your Path of Exile folder located in Steam or Grinding Gear Games folder. 

**C:\Program Files (x86)\Grinding Gear Games\Path of Exile**
- Save changes.
- Decide which features you want to run. *If you want to use the leveling part, you have to create a build first at the editor and then select the build.*
- Adjust your overlays in-game by drag and drop.

## How do I use the Editor?
*Will be updating this with a better write-down*

- Make sure to click Run > Validate and view leveling progress, to get a visual representation of your gem setup.
- Make sure to click Run > Validate and return to Launcher, to return to Launcher (Humility panel) and select your build to continue.

## Known bugs
*Will be updating this*
 - This may or may not be a bug im still trying to figure it out, just make sure you always have the XP option selected.

## Future additions

* ~~Link your leveling-build with your Path of Building tree, or a different planner~~ done check changelog v0.65 for more details on PoB import.
* Support for more resolutions, and/or custom sized overlays
* Find a more suitable way to place the overlays

Mentioned in the reddit comments :

>~~Is it possible to add notes to the socket groups (e.g. off hand corruption purposes, writing down how much dps increase a 3-4 link is in comparison~~ done  
>  
>~~A confirmation button if you accidentally press the minus for build creation~~ done  
>  
>~~Do something so we can see the rewards of quest so we can skip the quest that don’t give passive points / respect points~~  done  
>  
>~~Add the ability to "clone" a socket group~~ done  
>  
>The "socket group" name should include the levels for which it is active  
>  
>~~I recommend putting a box around "Start". Similar to how there is one around "Build" and "Editor"~~. done  
>  
>There is no way to get back to the options menu once the start button has been pushed

If you have a suggestion I encourage you to start an issue.

## Changelog in v0.65
New in this release:

 __New features__
- Added Path of Building support.
- - Select your Path of Leveling build on the editor and link it with the Path of Building.
You need to provide a valid pastebin link from PoB. You can then export this PoB to your clipboard
when you need it, or view it directly in https://poe.technology/poebuddy/ .
I have no affiliation whatsoever with the developers.
- Now if you export your build, and it is linked to Path of Building, the pastebin IS INCLUDED.
That means that you only need 1 pastebin link that will contain both a PoL build and a PoB build.

 
 __Reddit requests__
- It is now possible to add notes to the socket groups. You can find the new icon on 
the top of the left panel in the editor.
- It is now possible to duplicate socket groups. You can find the new icon on the top
of the left panel in the editor.
- There is now a box surrounding Start in the launcher to make it look more like a button.
- You now get prompts when deleting a build, and when you close the editor from the X icon.
   Also the application no longers terminates, instead it returns to the Launcher screen.
 - Slightly moved the Poets pen that was blocking the Editor button, and placed it below the button.
   
 __Bug fixes__
- Fixed a bug, where you had to select XP option, no matter what, or you would get no overlays.
 - - Now every option is independent, but running only the Zones options, now requests for
 character name and player level.
 - Fixed major bug, that forced application to update to itself, falling into a loop.  Hopefully, it is solved now.
 
 __Changes__
- Adjusted logs to provide less and more useful information



## Changelog in v0.6
New in this release:
 - Implemented a custom updater, when there is a new release and you launch the application you get a different loader that downloads the newest release, then places it in the same folder as this .jar. You can then rename the new one and delete the old one.
 - Added support for trials and passives in the acts. Now you can select in the preferences if you want those to pop up, and with the normal Zone overlay you get 2 more icons.
 - Added an extra option in the Zones overlay, you can now select if you want the images or not , and the same for the textual guides.
 - Removed some hardcoded text.
 - Fixed a typo in Ascendancy Berserker.
 - Implemented a custom logger. All outputs are now redirected in the log.txt, within your Path of Leveling folder. (That would be "My Documents/Path of Leveling" in Windows)
 - - If you find your app, crashing or not interacting properly take a look at your log file for any errors and post them for bug report (on reddit for example).
 - Fixed minor bug that needed you to restart the application to run properly.
 - Fixed and added more feautures that I have already forgotten.

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
