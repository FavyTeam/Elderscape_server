@echo off
title Server
"C:\Program Files\Java\jre1.8.0_201\bin\java.exe" -Xmx512m -cp bin;libraries/jython.jar;libraries/mina.jar;libraries/slf4j.jar;libraries/slf4j-nop.jar;libraries/gson-2.3.1.jar;libraries/slf4j-nop.jar;libraries/mysql-connector-java-5.1.40-bin.jar;libraries/DiscordBot.jar;libraries/Discord4j-2.7.0-shaded.jar;libraries/javamail-1.4.3.jar; core.Server 43595 PVP LIVE
pause
