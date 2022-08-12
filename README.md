<h1 align="center">Rawky</h1>
<p align="center">nuzzels ur pixels x3</p>
<p align="center">
    <a href="https://www.patreon.com/DeflatedPickle"><img src="https://c5.patreon.com/external/logo/become_a_patron_button@2x.png" height="24px"></a>
    <a href='https://ko-fi.com/Q5Q0CSWL' target='_blank'><img height='24' style='border:0px;height:24px;' src='https://az743702.vo.msecnd.net/cdn/kofi4.png?v=2' border='0' alt='Buy Me a Coffee at ko-fi.com'/></a>
</p>

<h4 align="center">A pixel art editor</h4>

<p align="center">
    <a href="https://discord.gg/QEz4fb93xd"><img alt="Discord members" src="https://img.shields.io/discord/448452090147110913?label=Join%20The%20Discord&style=social"></a>
    <a href="https://www.reddit.com/r/rawky/"><img alt="Subreddit subscribers" src="https://img.shields.io/reddit/subreddit-subscribers/rawky?style=social"></a>
</p>

<p align="center">
    <a href="https://github.com/DeflatedPickle/Rawky/commits/master"><img src="https://img.shields.io/github/last-commit/DeflatedPickle/Rawky.svg"></a>
    <a href="https://travis-ci.org/DeflatedPickle/Rawky"><img src="https://travis-ci.org/DeflatedPickle/Rawky.svg?branch=master"></a>
    <a href="https://www.codefactor.io/repository/github/deflatedpickle/rawky/overview/master"><img src="https://www.codefactor.io/repository/github/deflatedpickle/rawky/badge/master" alt="CodeFactor" /></a>
    <a href="https://codeclimate.com/github/DeflatedPickle/Rawky/maintainability"><img src="https://api.codeclimate.com/v1/badges/b5f7de56e73e0c459a9e/maintainability"></a>
</p>

<p align="center">
    <img src="https://sloc.xyz/github/DeflatedPickle/Rawky/?category=blanks">
    <img src="https://sloc.xyz/github/DeflatedPickle/Rawky/?category=code">
    <img src="https://sloc.xyz/github/DeflatedPickle/Rawky/?category=comments">
    <img src="https://sloc.xyz/github/DeflatedPickle/Rawky/?category=lines">
</p>

<h3 align="center">Download</h4>
<p align="center">
    <a href="https://github.com/DeflatedPickle/Rawky/releases/tag/dist-latest"><img alt="Latest" src="https://img.shields.io/github/downloads/DeflatedPickle/Rawky/dist-latest/total.svg"></a>
</p>

### Features
#### Configuration
Most plugins have their own config files so that the application can be tailored to your workflow, either by editing the files or by using the dialog added via the SettingsGUI plugin
#### Auto-saving
Thanks to the auto save plugin, files are automatically saved on a timer defined by the user (default: 1 minute) or when the user switches programs (togglable, default: `true`)
#### Auto-loading
Due to the auto load plugin, if there was a previously open file when the program was closed, it'll get loaded in when it's next open
#### Painting modes
The pixel grid plugin allows for the user to select between modes for how the pixel grid is to be interacted with. The currently supported modes are;
- Mouse
- Keyboard
#### Painting tools
The tools API allows for more tools to be added via external plugins but Rawky currently comes with the following tools;
- Pencil (place single pixels, can be dragged)
- Eraser (remove single pixels, can be dragged)
- Dropper (set the colour of the chosen pixel to your current colour)
- Line (click a pixel then a second to draw a line between them)
- Bucket (click a pixel to have all pixels in the area of the same colour changed)
- Rectangle (click a pixel then a second to have a rectangle be drawn betwen them)
- Circle (click a pixel then a second to have a circle be drawn out from the first to the distance of the second)
#### Server
Rawky comes with a server plugin enabling you to paint with your friends
##### Chat
A chat plugin is also supplied so that messages may be sent
##### Userlist
A list is also supplied to show all users currently connected to the server
##### Scoreboard
#### File support
##### Rawr
- Rawky JSON (`.rawr`)
- Rawky Binary (`.rawrxd`)
##### Ascii
###### Write only
- Text (`.txt`)
##### ImageIO
######  Read and write
- Portable Network Graphics (`.png`)
- Graphics Interchange Format (`.gif`)
- Bitmap (`.bmp`, `.dib`)
- Wireless Bitmap (`.wbmp`)
- MS Windows Icon Format (`.ico`)
- Apple Icon Image (`.icns`)
- Interchange File Format (`.iff`)
- Joint Photographic Experts Group (`.jpg`, `.jpeg`, `.jpe`, `.jif`, `.jifi`, `.jfi`)
- Apple QuickDraw (`.pict`, `.pct`, `.pic`)
- Portable Any Map (`.pnm`, `.ppm`)
- Adobe Photoshop Document (`.psd`)
- Truevision TGA Image Format (`.tga`, `.icb`, `.vda`, `.vst`)
- Tagged Image File Format (`.tiff`, `.tif`)
- Google WebP Format (`.webp`)
###### Read only
- Scalable Vector Graphics (`.svg`)
- MS Windows Metafile (`.wmf`)
- MS Cursor (`.cur`)
- HDRsoft High Dynamic Range (`.hdr`)
- Lossless JPEG (`.jpg`, `.ljpg`, `.ljpeg`)
- ZSoft Paintbrush (`.pcx`)
- Zsoft Multi-Page Paintbrush (`.dcx`)
- MacPaint Graphic (`.pntg`)
- NetPBM Portable Bit Map (`.pbm`)
- NetPBM Portable Grey Map (`.pgm`)
- Portable Float Map (`.pfm`)
- Adobe Photoshop Large Document (`.psb`)
- Silicon Graphics Image (`.sgi`, `.bw`, `.rgb`, `.rgba`)
- Windows Thumbnail Cache (`.db`)
- X Window Dump (`.wxd`, `.xdm`)