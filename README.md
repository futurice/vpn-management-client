VPN Configuration Wizard
=================================
Description
-----------

The purpose of this Java Application is to make setting up a VPN connection easier. 

The application uses openssl to generate a .key file and a request that is 
sent to an API. The API responds by sending an sms to the users mobile 
phone. The wizard asks for the password and sends it to the API. 

If the password is correct the API sends a link to a .zip
file with all the configuration files needed for the VPN connection.
The wizard downloads this zip and tries to extract it in to the proper
folder. If it fails it asks the user to extract the settings manually.

The backend for this applications is also open source and can be found at [GitHub](https://github.com/futurice-oss/vpn-management-server)

Background
----------
This application was created to make setting up a VPN connection to the [Futurice](http://www.futurice.com) intranet easier. It was open sourced as a part of the [Summer of Love program](http://blog.futurice.com/summer-of-love-of-open-source)

Classes
-------

- Main.java
This class starts up the application. It has no other function.

- Configurator.java
This class ties all the other classes together.

- GUI.java
This class displays the GUI for the application. It interacts with
the Configurator class.

- Generator.java
This class generates the .key file and the csr request. It interacts with
the Configurator class.

- Backend.java
This class interacts with the back end API. It sends the csr and password
and downloads the settings. It also tries to unzip the settings in to
the proper folder. It interacts with the Configurator class.

Supporting Libraries
--------------------

- Commons Codec (http://commons.apache.org/codec/)
- JSON in Java (http://json.org/java/)

About Futurice
--------------
[Futurice](http://www.futurice.com) is a lean service creation company with offices in Helsinki, Tampere, Berlin and London.

Peole who have contributed to VPN Configuration Wizard:
- [Oskar Ehnström](https://github.com/Ozzee)
- [Henri Holopainen](https://github.com/henriholopainen)
- [Olli Jarva](https://github.com/ojarva)
- [Ville Tainio](https://github.com/Wisheri)

Support
-------
Pull requests and new issues are of course welcome. If you have any questions, comments or feedback you can contact us by email at sol@futurice.com. We will try to answer your questions, but we have limited manpower so please, be patient with us.
