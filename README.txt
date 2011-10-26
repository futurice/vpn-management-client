
Futurice VPN Configuration Wizard

** Description **

The purpose of this Java Application is to make setting up a VPN connection
to the Futurice intranet easier. 

The application uses openssl to generate a .key file and a request that is 
sent to an API. The API responds by sending an sms to the users mobile 
phone. The wizard asks for the password and sends it to the API. 

If the password is correct the API sends a link to a .zip
file with all the configuration files needed for the VPN connection.
The wizard downloads this zip and tries to extract it in to the proper
folder. If it fails it asks the user to extract the settings manually.


** Classes **

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

** Supporting Libraries **

- Commons Codec (http://commons.apache.org/codec/)
- JSON in Java (http://json.org/java/)
