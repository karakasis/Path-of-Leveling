# Path of Leveling

Switching to beta with the introduction of the new UI.

**~~This is an alpha version~~**, **bugs included, if you find an error or crash or bug please report it in this page**

**and consider creating a pastebin with the latest log, found in your Documents/Path of Leveling folder.**



*Assisting overlay with an advanced GUI editor written in Java for [Path of Exile](https://www.pathofexile.com/game).*

*[Reddit thread](https://www.reddit.com/r/pathofexile/comments/aca7vl/path_of_leveling_a_tool_written_in_java_with_an/)*

*Get the [latest Version](https://github.com/karakasis/Path-of-Leveling/releases/tag/v0.74-beta), updated March 10th 2019*

*Pastebin build example : [pastebin](https://pastebin.com/BDzwRww9)*

*A quick overview of the application running can be found in this [video.](https://www.youtube.com/watch?v=xxiOxnJZM-A)*

*Join Path of Leveling's [Discord server](https://discord.gg/GdTCeMU) if you need help or have suggestions*

## What does it do

This program currently does three things!
- **Plan your leveling by selecting gems and links.** You can create complicated transitions between your gem setup, swap a gem for another one, or completely remove a Skill group for a different one. Get a popup on your in-game overlay when you reach certain levels.
- **Navigate through the game's acts, with graphical representations of typical zone layouts from [Engineering Eternity](https://www.youtube.com/channel/UCaFHfrY-6uGSAvmczp_7a6Q/featured)** You can move the overlay around to suit your own needs. *Supporting only 1080p at this moment, which means that in smaller screens you will get bigger images, and in bigger screens smaller :).*
- **Track your XP and find out when you are under/overleveled**. An overlay showing you your current level, the area level you are in, only in Acts not maps , calculates your effective xp multiplier in a 0-100% form.

## How does it work

Opens the log the Path of Exile produces, attaches it to a Tailer, that triggers on certain lines.

This does not change, or interact with in-game files, only **reads lines**, so afaik this is not against TOS.

Basically the images displayed, are pre-generated and are not in any way dependent with the game's zone creation.

This can be done by using pen and paper.

The Tailer is reading the log, and triggers on "entered zone .. " and " is now level .." .

That's all. Everything else is code.

## Launch

- Run PathOfLeveling.jar (Java 8 required) - *[download java](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) jre-8u191-windows-x64.exe at the time of writting this*
- - Right-click the Computer icon on your desktop and select Properties.
- - Click Advanced system settings.
- - Click Environment variables.
- - Under User variables, click New.
- - Enter JAVA_HOME as the variable name.
- - Enter the path to the JDK as the variable value. For example, C:\Program Files\Java\jre1.8.0_191.
- - Click OK.
- - Locate the PATH variable.
- - Append the path to the JDK (up to the bin folder) as the PATH value. For example, C:\Program Files\Java\jre1.8.0_191\bin.
- - - Important: Use a semicolon to separate the JDK path from the other paths that are defined.
- -Repeat the same for System variables.
- -Click OK.
- Click the preferences gear button.
- Select your Path of Exile folder located in Steam or Grinding Gear Games folder.

**C:\Program Files (x86)\Grinding Gear Games\Path of Exile**
- Save changes.
- Decide which features you want to run. *If you want to use the leveling part, you have to create a build first at the editor and then select the build.*
- Adjust your overlays in-game by drag and drop.

## How do I use the Editor?
*Will be updating this with a better write-down*

- Make sure to click Run > Validate and view leveling progress, to get a visual representation of your gem setup.
- Make sure to click Run > Validate All and return to Launcher, to return to Launcher (Humility panel) and select your build to continue.

## Known bugs
*Will be updating this*
 - some validation issues i will fix when i get some time with the new league.
 - Large transparent area blocking your inputs.
 
1. Have POE running and stay in your current zone
2.Open POL (put char name build etc) 
3.Without changing zone press the remind area hotkey.
4.This will bug out cause the app doesnt know your current location and cause
some transparent overlay to appear that blocks half your screen.

## Future additions

* ~~Link your leveling-build with your Path of Building tree, or a different planner~~ done check changelog v0.65 for more details on PoB import.
* Support for more resolutions, and/or custom sized overlays
* ~~Find a more suitable way to place the overlays~~ I would like to believe I achieved that with new gem UI.

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
>~~There is no way to get back to the options menu once the start button has been pushed~~ done

If you have a suggestion I encourage you to start an issue.

## Changelog in v0.74
New in this release:

**New features**

* Since a lot of people wanted the option to place their Zone overlay on locations other than the top oftheir screen, I had to find a way to change the slide animations.
   * Settings > Show zones with a slide effect > On will be the slide and snap back to top of the screen.
   * Settings > .. > Off will be place whereever u want but it wont slide at all, only fade in and out in place
   * .. > On if timer is more than 0, you will get an auto slide out after X seconds
   * .. > On if timer is 0 you will only get slide in and out on zone change

**Bug fixes**

* Fixed the bug where you had to add a build to play with only zones activated
* Fixed the recipe saving bug where marking a recipe would overwrite your previous ones
   * You will have to re-add the recipes you found so far. I added this option, since it was not therebefore
   * Open your Editor> View> Recipes/Passives and select the checkboxes for the recipes you found,and then close the window and you mark recipes normally now.(dont click on reset)
* Fixed an issue where you could move the overlays with middle, and right click
* Fixed an issue where spam clicking the API Character select you open multiple windows.

**Changes**

* Changed missing keybind behaviour to "not-set".
   * If you want to clear-remove a hotkey, open the config.properties in your Path of Leveling folderand remove the keybind line you don't want.

## Changelog in v0.73
New in this release:

**Bug fixes**
* Removed a high resolution image that would cause a memory heap.

## Changelog in v0.72
New in this release:

**Bug fixes**
* Fixed an error, where PoB has an Enchantment listed as skill, so the import would not work.

## Changelog in v0.71
New in this release:

**Bug fixes**
* Fixed bug when removing gems from a socketgroup introduced in v0.7-beta

## Changelog in v0.70
New in this release:

**New features**

* Added Path of Building import build. This is different to Link active build with POB.
   * "Link" will simply link the PoL build with a PoB pastebin, that you can view in Poebuddyor keep a reference too.
   * With this new feature, you can import your PoB pastebin, and the app will automaticallycreate a snapshot of the PoB build. All socket groups will be created, and all gems willget assigned. You can then edit the build normally to add transitions and replacements, or makeyour own additions/removals, as always. After saving the build it can be used as all others andexported to Pastebin and shared.
   * If you are a build creator, and you want to use this tool, I recommend setting up your POB buildwith endgame gems, then import it to POL tweak it and provide both links. The POL link will include thePOB pastebin. This way you can save yourself some time from recreating the gems in POB/POL.
* Added the option to pull all your characters using the POE Api . When starting POL now,you can choose to set your PoE account and choose your character from the PoE servers.
   * (Your account info must be set to public in order for this to work properly)
   * You can set your account name in the Settings if you don't want to type it in again.
* Designed new UI, and changed the color scheme to Dark. (Colors are simillar to Discord)
   * The old UI , and colors have been trashed, but if you want the option to go White again (weirdo)let me know and I might include it as a skin.
* Designed new Overlay for the Gem pop-ups. The default is on, and you can change to the old onefrom beta UI toggle in settings menu.
* Added gem tags to all Skill gems. You can now search in a new improved UI by filtering tags.
* Added validation functionality.
   * Now when you have a build that has some kind of error, you can choose to ignore itby making the build non validated. This will add a Non-valid tag to your build banner on the rightcolumn, where your builds are listed. Then you can go back to the launcher without having to resolve the issuebut you will not be able to select this build as your leveling guide.
   * This introduced a bug, try to avoid making wonky builds :)
* Added a confirmation dialogue when exiting the editor window.
   * If you cancel any changes you made will not be saved.
* Added crafting recipes. You can enable/disable them in the settings menu.
   * Upon entering an area you get an icon (simillar to trial,passive) that indicates zone contains a recipe to find.
   * Assign a keybind to Mark Recipe. Pressing the button will mark the recipe as foundand later you can revisit in a seperate window and check which recipes you missed during leveling.
* Added a keybind lock hotkey. You are not getting any visuals but it should be at least handy.
   * Locking -> any keybinds except the lock keybind, will not trigger any events .
   * Locking -> your overlays will stay in place and cant be dragged.
   * This will allow you to type in chat without the JNA going nuts.

**Reddit requests**

* You can now access the Settings panel, via the Icon tray icon while in "overlay" mode.
   * (overlay->when you have set the build and pressed start and the overlays appear)
* Your overlay positions will now persist between app restarts.
* When you create a new gem group it should automatically bring up the attached window.
* Auto-select the first non-support gem entered for each group as the default main gem, user can change later.

**Bug fixes**

* Fixed a bug with images in Dried Lake and Aqueduct in Act 4.
* Fixed a typo in ascendancy names.
* Fixed the version, and version changelog. Now they are pulled from the server.
* Fixed numerous bugs related to build creation and management
* Stability improvements (hopefully)

**Changes**

* Gem selection panel has been improved by "Protuhj". You can now search for gems with an auto-completion box andauto scrolling when choosing the gem. (possible improvements in the future)
* Gem overlay has been moved to the left side of the screen, to prevent overlapping with an open inventory.If you have a suggestion about default positions please let me know.
* The export to pastebin functions have been automated by "Linsane". Now clicking export will send an API requeststo pastebin, and you can copy the link by Copy button. In case of daily limit (10posts per day) you can click Opento open the pastebin website where the raw data will be placed automatically.
   * This can be usefull if you want to store the pastebins in the website and not post as guest.
* Character name is no longer case sensitive.
* You can now resize the window, and not get frustrated with 720p windows in 4k monitors. :)
* When updating to new version you get a confirmation dialogue.
* Data and gem JSON files are now downloaded separately and are updated automatically.
* Skill gem icons are now downloaded on demand. (The first load will take a while to download all of them)
* Changed the graphics for xp bar. It is no longer a bar, this allows you to place both xp, and gems overlaysin the area between your potions and your skills.

**Additions**

* Updated the application to include Synthesis new skill gems.
* Updated Skill gems to their renamed names. If you have a build that has a reference to an old gem name, it willautomatically get updated to the renamed version.
* Updated Skill gems to their reworked level requirement.

# Changelog in v0.65

New in this release:

**New features**

* Added Path of Building support.
   * Select your Path of Leveling build on the editor and link it with the Path of Building. You need to provide a valid pastebin link from PoB. You can then export this PoB to your clipboard when you need it, or view it directly in [https://poe.technology/poebuddy/](https://poe.technology/poebuddy/) . I have no affiliation whatsoever with the developers.
* Now if you export your build, and it is linked to Path of Building, the pastebin IS INCLUDED. That means that you only need 1 pastebin link that will contain both a PoL build and a PoB build.

**Reddit requests**

* It is now possible to add notes to the socket groups. You can find the new icon on the top of the left panel in the editor.
* It is now possible to duplicate socket groups. You can find the new icon on the top of the left panel in the editor.
* There is now a box surrounding Start in the launcher to make it look more like a button.
* You now get prompts when deleting a build, and when you close the editor from the X icon. Also the application no longers terminates, instead it returns to the Launcher screen.
* Slightly moved the Poets pen that was blocking the Editor button, and placed it below the button.

**Bug fixes**

* Fixed a bug, where you had to select XP option, no matter what, or you would get no overlays.
   * Now every option is independent, but running only the Zones options, now requests for character name and player level.
* Fixed major bug, that forced application to update to itself, falling into a loop. Hopefully, it is solved now.

**Changes**

* Adjusted logs to provide less and more useful information

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
