# Android Virtual 3D Mouse for Blender 3D
This project was a 2 week sprint to create a proof of concept that you can turn an android device into a 3D mouse that can control a 3d modeling application. Since I'm learning blender and its built in such a way that they make it easy to interface with it, I use that app as the basis of the experiment. This project is solely meant as a submission for the Android Experiements contest. If there is an actual need for this, then I may continue it further but maybe swopping out the serial communications for UDP connection, which should remove the hardware requirements to get android to communicate with windows.

Youtube video of this in action - https://youtu.be/0j87vnCestY

### Prerequisities
- Blender 2.77 - https://www.blender.org/download/
- PySerial
- PL2303 USB UART Board USB to Serial - http://amzn.to/1NlNr8G
- HC-05 Bluetooth Host Serial Transceiver For Arduino - http://amzn.to/1NlNuS2
- Android Studio

## Getting Started

1. Get blender and setup PySerial.
    So the first thing is you have to go download blender. From there, look in the blender_files folder, you will find a folder called serial. This is PySerial for python v3, windows x64. If you have X86 machine, google on how to download pyserial. Copy the serial folder to \blender-2.77-windows64\2.77\python\lib\site-packages. Thats all to it.

2. Put together your serial boards together. They're fairly easy to put together. Power -> Power, Gnd -> Gnd, TXD -> RXD, RXD -> TXD. Make sure you set the PL2303 to 3 volt jumper, not 5. The HC-05 board will get power from PL2303, but it only needs 3v. Once plugged in and drivers install (windows10 self installed the drivers). Go to device manager and find what COM port the PL2303 is using.

3. Use Android studio and compile the app.

4. Now you put everything together. Run blender, open up serial_mouse5.blend in the blender_files folder. (5 stands for 5th attempt, pita to figure it all out but fun). In blender, in the Text Editor area, click run script. This will create a new UI Panel that you can access in the Tools panel, SMouse. All you need to do is change the port to COM then a number of what the PL2303 is using. The Baud can be left alone. All you have to do is press connect and it will create a thread that connects and listens for serial data coming for the COM port. It'll also create a timer modal loop that works as a handler for the thread to push back some commands that need to be run in the UI thread because blender has this context thing that the thread can't use. You can see the debug text I'm outputing in the console. Just go to Window Menu, click "toggle system console" to see it

5. On your android device, Pair up to the HC-05 board. The default code should be 1234. Turn on the 3d mouse app, click on the paired device and it should auto connect. From there you should be in control of blender.

I know, this is kind of a complex setup.

## Authors

* Pedro S. (Vor of SketchpunkLabs) - http://sketchpunklabs.tumblr.com/

## Acknowledgments
All the people who wrote various tutorials that I've read about blender, python, how to get android talking to arduino with bluetooth serial. The guy who answered one of my questions on reddit about how to invoke an operators. Plus many more, I couldn't of done this without all their shared knowledge. So thank you everyone.
