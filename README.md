system_updater
==============

A small program that allows me to update software on Windows-clients through XMPP. It is written in Java
It requires the smack-libary (http://www.igniterealtime.org/projects/smack)


have some hardcoded commands (like reboot and shutdown) but most of the installing commands I send through an url. 
The program downloads the url that contains a XML-file. 
The XML-file tells what the program has to do.

I use NSSM - the Non-Sucking Service Manager (http://www.nssm.cc) for installing the program as a Windows service. 
So it starts when booting the laptop and it runs with full admin-rights. 
Therefore I needed to have a batch-file to start the program.