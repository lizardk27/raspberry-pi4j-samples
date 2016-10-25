## Lib RxTx, Serial communication.
This involves the classes located in the package `gnu.io`.
To install this package (on Raspberry PI, or more generally on Ubuntu), type
```
$> sudo apt-get install librxtx-java
```

This is a Java implementation of a Serial client for the `raspi-serial-console`.

It can run the same - hopefully - in Windows, Mac, Linux, no need for screen or PuTTY...

Next, we'll see how to transfer files (text and binaries).
 
### Examples

#### From a Mac

Script `run.mac` :
```
#!/bin/bash
#
RXTX_HOME=./libs
#
CP=./build/libs/SerialRxTx-1.0.jar
CP=$CP:$RXTX_HOME/RXTXcomm.jar
#
JAVA_OPTS=
JAVA_OPTS="$JAVA_OPTS -Djava.library.path=$RXTX_HOME"
JAVA_OPTS="$JAVA_OPTS -Dserial.port=/dev/tty.usbserial"
JAVA_OPTS="$JAVA_OPTS -Dbaud.rate=115200"
JAVA_OPTS="$JAVA_OPTS -Dverbose=false"
java $JAVA_OPTS -cp $CP sample.SerialEchoClient
```

```
$> ./run.mac
== Serial Port List ==
-> /dev/cu.usbserial
-> /dev/tty.Bluetooth-Incoming-Port
-> /dev/tty.usbserial
-> /dev/cu.Bluetooth-Incoming-Port
======================
Opening port /dev/tty.usbserial:115200 ...
Serial port connected: true
IO Streams initialized
Writing to the serial port.

pi@RPiZero:~$ ll
total 97640
  1887     4 drwxr-xr-x 23 pi   pi       4096 Oct 21 22:58 .
    16     4 drwxr-xr-x  3 root root     4096 Nov 21  2015 ..
 80552     0 -rw-r--r--  1 pi   pi          0 Nov 21  2015 .asoundrc
 74920    36 -rw-------  1 pi   pi      36459 Oct 21 22:58 .bash_history
  2294     4 -rw-r--r--  1 pi   pi        220 Nov 21  2015 .bash_logout
  2296     4 -rw-r--r--  1 pi   pi       3535 Dec 10  2015 .bashrc
 81461     4 drwxr-xr-x  9 pi   pi       4096 Dec  8  2015 .cache
  1888     4 drwxr-xr-x 12 pi   pi       4096 Dec 13  2015 .config
 81459     4 drwx------  3 pi   pi       4096 Nov 21  2015 .dbus
  1891     4 drwxr-xr-x  2 pi   pi       4096 Dec 13  2015 Desktop
  1892     4 drwxr-xr-x  5 pi   pi       4096 Nov 21  2015 Documents
 81452     4 drwxr-xr-x  2 pi   pi       4096 Nov 21  2015 Downloads
 74477     4 -rw-r--r--  1 pi   pi         56 Oct  7 01:18 .gitconfig
261577     4 drwxr-xr-x  5 pi   pi       4096 Oct 20 00:37 .gradle
 81470     4 drwxr-xr-x  2 pi   pi       4096 Nov 21  2015 .gstreamer-0.10
  1889     4 drwxr-xr-x  3 pi   pi       4096 Nov 21  2015 .local
265996     4 drwxr-xr-x  3 pi   pi       4096 Dec 10  2015 .m2
 81455     4 drwxr-xr-x  2 pi   pi       4096 Nov 21  2015 Music
268609     4 drwxr-xr-x  5 pi   pi       4096 Aug 16 02:14 node.pi
 81456     4 drwxr-xr-x  2 pi   pi       4096 Nov 21  2015 Pictures
  2297     4 -rw-r--r--  1 pi   pi        675 Nov 21  2015 .profile
 81454     4 drwxr-xr-x  2 pi   pi       4096 Nov 21  2015 Public
  1893     4 drwxr-xr-x  2 pi   pi       4096 Nov 21  2015 python_games
265890     4 drwxr-xr-x 41 pi   pi       4096 Oct 21 18:36 raspberry-pi4j-samples
 81020     8 -rw-r--r--  1 pi   pi       5576 Oct 21 16:43 README.test.md
 74553  1024 -rw-r--r--  1 root root  1048204 Sep 13  2014 sbt-0.13.6.deb
 74509 96444 -rw-r--r--  1 root root 98756608 Oct 23  2014 scala-2.11.4.deb
 67808     4 -rw-r--r--  1 pi   pi         12 Dec 10  2015 .scala_history
 81453     4 drwxr-xr-x  2 pi   pi       4096 Nov 21  2015 Templates
  1890     4 drwxr-xr-x  3 pi   pi       4096 Nov 21  2015 .themes
267515     4 drwx------  4 pi   pi       4096 Dec 13  2015 .thumbnails
 81457     4 drwxr-xr-x  2 pi   pi       4096 Nov 21  2015 Videos
266475     4 drwxr-xr-x  2 pi   pi       4096 Dec 12  2015 work
 81023     4 -rw-------  1 pi   pi         58 Oct 21 20:25 .wpa_cli_history
 80533     4 -rw-------  1 pi   pi        108 Oct 21 22:58 .Xauthority
  1790     4 -rw-------  1 pi   pi        353 Oct 21 22:58 .xsession-errors
 11389     4 -rw-------  1 pi   pi        353 Oct 21 22:51 .xsession-errors.old
pi@RPiZero:~$ 
```

### From Windows

Script `run.bat`
```
@echo off
@setlocal
::
set RXTX_HOME=C:\OlivWork\git\oliv-soft-project-builder\olivsoft\release\all-3rd-party\rxtx.distrib
::
set CP=.\build\libs\SerialRxTx-1.0.jar
set CP=%CP%;%RXTX_HOME%\RXTXcomm.jar
::
java -Djava.library.path=%RXTX_HOME%\win64 -Dserial.port=COM17 -Dbaud.rate=115200 -cp %CP% sample.SerialEchoClient
@endlocal
```

etc