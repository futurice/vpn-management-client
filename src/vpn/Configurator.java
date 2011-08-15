/**
 * This class is keeps track of the other classes and calls
 * for them to act when needed.
 */

package vpn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Configurator {

	public static int CONFIG_DIR_DOES_NOT_EXIST = 1;
	public static int ERROR = 2;
	public static int CONFIG_FILES_DO_NOT_EXIST = 3;
	public static int COULD_NOT_MOVE = 4;

	public static int LINUX = 0;
	public static int OSX = 1;
	public static int WINDOWS = 2;

	// Friendly names for the OSes
	public static String[] osNames = { "Linux", "Mac OS X", "Windows" };
	// This is used to check the config files returned by the api.
	public static String[] osConfNames = { "linux", "mac", "windows" };

	private String commonName;
	private String confDirString;
	private String user;
	private String endText;
	private String status;

	Backend backend;

	Generator generator;

	int os;

	/**
	 * The constructor initializes some values and determines the operating
	 * system.
	 */
	public Configurator() {

		this.commonName = null;
		this.backend = null;
		this.generator = null;
		this.endText = "The wizard has finished.";

		this.user = System.getProperty("user.name");

		// Try to figure out the OS
		String osString = System.getProperty("os.name");
		if (osString.startsWith("Linux")) {
			this.os = LINUX;
			this.confDirString = "/home/" + user + "/";
		} else if (osString.startsWith("Mac")) {
			this.os = OSX;
			this.confDirString = "/Users/"
					+ user
					+ "/Library/Application Support/Tunnelblick/Configurations/";
		} else if (osString.startsWith("Windows")) {
			this.os = WINDOWS;
			this.confDirString = "C:\\Program Files (x86)\\OpenVPN\\config\\";
			File test = new File(this.confDirString);
			if (!test.exists())
				this.confDirString = "C:\\Program Files\\OpenVPN\\config\\";
		} else {
			this.os = -1;
		}

	}

	/**
	 * This method starts the GUI.
	 */
	public void gui() {
		@SuppressWarnings("unused")
		GUI gui = new GUI(this);
	}

	/**
	 * This method first tries to generate the settings by creating a Generator
	 * object and calling it. If this is successful the interaction with the
	 * backend begins.
	 * 
	 * @param ldapUser
	 * @param ldapPass
	 * @param pass
	 * @param computer
	 * @param email
	 * @param owner
	 * @param employment
	 * @return
	 */
	public String askForSettings(String ldapUser, String ldapPass, String pass,
			String computer, String email, String owner, String employment) {

		this.backend = new Backend(ldapUser, ldapPass, this);
		this.status = "Backend connection established.";

		this.commonName = this.createCommonName(ldapUser, computer, employment,
				owner);

		if (pass == null || ldapUser == null || email == null
				|| ldapPass == null)
			return "Parameter null error.";

		this.generator = new Generator(pass, this.commonName, email);
		this.status = "Generating request.";
		String request = this.generator.generateRequest();

		if (request == null)
			return "Could not generate request files.";

		this.status = "Sending request.";
		String response = this.backend.sendCSR(request);

		if (response != null) {
			return "There was an error with the csr:\n" + response;
		}

		return null;
	}

	/**
	 * This method creates the common name to be used in the csr and key
	 * 
	 * @param user
	 * @param computer
	 * @param employment
	 * @param owner
	 * @return
	 */
	public String createCommonName(String user, String computer,
			String employment, String owner) {
		String common = "";
		common += user;

		if (employment.equals("External"))
			common += "-ext";

		if (!owner.equalsIgnoreCase("Futurice"))
			common += "-" + owner;

		common += "-" + computer;

		return common.toLowerCase();
	}

	public String enterPassword(String password) {
		if (this.backend == null)
			return null;

		this.setStatus("Sending password.");
		String response = this.backend.sendPassword(password);

		if (response != null) {
			return response;
		}

		this.setStatus("Moving files.");
		int moved = this.moveFilesToConfig(this.backend.getZip());

		if (moved == CONFIG_FILES_DO_NOT_EXIST) {
			return "Could not find the configuration files.";
		} else if (moved == CONFIG_DIR_DOES_NOT_EXIST) {
			return "Configuration directory did not exist.";
		} else if (moved == COULD_NOT_MOVE) {

			// Add directions for moving the files manually
			File currentDir = new File(".");
			this.endText += "\nThe configuration files could not be moved. "
					+ "\nYou can manually copy the settings and key "
					+ "from this directory: \n"
					+ currentDir.getAbsolutePath()
					+ " in to this directory:\n"
					+ this.confDirString
					+ "\n\n"
					+ "The files needed are the .key file, the .ovpn file, "
					+ "the .pem file and the .crt file. (The latter three are in the settings.zip)";
			return null;
		} else if (moved != 0) {
			return "Something went wrong";
		}
		this.status = "Done.";

		this.endText += "\nThe configuration files have been moved to:\n"
				+ this.confDirString + "\n\n";

		return null;
	}

	/**
	 * Moves the configuration files to the right directory based on the OS
	 * 
	 * @return
	 */
	private int moveFilesToConfig(File zipfile) {

		File key = new File(this.commonName + ".key");
		File zip = zipfile;

		if (zip == null || !key.exists() || !zip.exists())
			return CONFIG_FILES_DO_NOT_EXIST;

		File configDir = null;

		// Mac
		if (this.os == OSX) {

			configDir = new File(this.confDirString);

			// Linux
		} else if (this.os == LINUX) {
	
			//Searches for a folder that doesn't exist yet
			String foldername = "VPNsettings";
			int i = 1;
			File newFolder = new File(this.confDirString+foldername);
			while (newFolder.exists()){
				newFolder = new File(this.confDirString+foldername+i);
				i++;
			}
			newFolder.mkdir();
			configDir = newFolder;
			this.confDirString = configDir.getAbsolutePath();

			// Windows
		} else if (this.os == WINDOWS) {

			configDir = new File(this.confDirString);

		}

		// Does conf directory exist?
		if (configDir != null && configDir.exists()) {
			if (this.commonName != null) {

				this.setStatus("Unzipping.");
				// Unzip and move key
				if (this.unzip(zip, configDir)
						&& key.renameTo(new File(configDir, key.getName()))) {
					return 0;
				} else {
					return COULD_NOT_MOVE;
				}

			} else {
				return ERROR;
			}

		} else {
			System.err.println("Configuration directory did not exist.");
			return CONFIG_DIR_DOES_NOT_EXIST;
		}
	}

	/**
	 * Method for unzipping a zipfile with configuration files.
	 * 
	 * @param zipfile
	 *            , the file to be unzipped
	 * @param confDir
	 *            , the directory where the configurations should be placed
	 * @return true or false based success
	 */
	private boolean unzip(File zipfile, File confDir) {
		try {
			byte[] buf = new byte[1024];

			ZipEntry fileInZip;
			ZipInputStream zipInStream = new ZipInputStream(
					new FileInputStream(zipfile));

			fileInZip = zipInStream.getNextEntry();
			while (fileInZip != null) {

				// Check that neither of the other two operating systems is
				// mentioned
				// in the filename.
				if (fileInZip.getName().indexOf(osConfNames[(this.os + 1) % 3]) == -1
						&& fileInZip.getName().indexOf(
								osConfNames[(this.os + 2) % 3]) == -1) {
					String entryName = fileInZip.getName();
					int n;
					File newFile = new File(entryName);
					String directory = newFile.getParent();

					if (directory == null) {
						if (newFile.isDirectory())
							break;
					}

					FileOutputStream fileoutputstream = new FileOutputStream(
							confDir.getAbsolutePath() + "/" + entryName);

					while ((n = zipInStream.read(buf, 0, 1024)) > -1)
						fileoutputstream.write(buf, 0, n);

					fileoutputstream.close();
				}
				zipInStream.closeEntry();
				fileInZip = zipInStream.getNextEntry();

			}

			zipInStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		zipfile.delete();
		return true;
	}

	public String getIntroText() {
		String text = "Welcome to the Futurice VPN configuration wizard.\n"
				+ "\nYou will now be guided through the process of setting up a VPN"
				+ " connection to the Futurice intranet.\n"
				+ "\nYou appear to be running "
				+ osNames[this.os]
				+ ", and your username is "
				+ this.user
				+ ".\n"
				+ "If this is not correct, please set up the VPN manually as shown"
				+ " in confluence.";

		if (this.os == OSX) {
			File test = new File(this.confDirString);
			if (!test.exists())
				text += "\n\nTunnelblick does not seem to be installed."
						+ "Please make sure you have installed it before running this Wizard.";
		}

		if (this.os == WINDOWS) {
			File test = new File(this.confDirString);
			if (!test.exists())
				text += "\n\nOpenVPN does not seem to be installed."
						+ "Please make sure you have installed it before running this Wizard.";
		} else if (this.os == LINUX){
			if (Generator.openSSLPath()==null){
				text += "\n\nopenssl does not seem to be installed."
					+ "Please make sure you have installed it before running this Wizard.";
			}
		}

		return text;
	}

	public String getFinishingText() {
		String text="";
		if (this.os == OSX) {
			text += "\nTo start a VPN connection, open Tunnelblick, \nclick the icon in the"
					+ " notification area \nand choose the futurice vpn connection.";
		} else if (this.os == LINUX){
			text += "\n\nPlease not that you have to change the paths in the .conf file" +
					" to match your system.";
		}

		return this.endText+text;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String stat) {
		this.status = stat;
	}
	
	public String getUser(){
		return this.user;
	}
}
