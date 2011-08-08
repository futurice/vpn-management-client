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

	public static int LINUX = 0;
	public static int OSX = 1;
	public static int WINDOWS = 2;

	String commonName;
	String zipFile;
	String macConfDir;

	Backend backend;

	int os;

	/**
	 * The constructor initializes some values and determines the operating
	 * system.
	 */
	public Configurator() {

		this.commonName = null;
		this.zipFile = "zipfile.zip";
		this.backend = null;

		// Try to figure out the OS
		String osString = System.getProperty("os.name");
		if (osString.startsWith("Linux"))
			this.os = LINUX;
		else if (osString.startsWith("Mac"))
			this.os = OSX;
		else if (osString.startsWith("Windows"))
			this.os = WINDOWS;
		else {
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

		this.backend = new Backend(ldapUser, ldapPass);

		this.commonName = this.createCommonName(ldapUser, computer, employment, owner);

		if (pass == null || ldapUser == null || email == null
				|| ldapPass == null)
			return "Parameter null error.";

		Generator gen = new Generator(pass, this.commonName, email);
		String request = gen.generateRequest();

		if (request == null)
			return "Could not generate request files.";

		String response = this.backend.sendCSR(request);

		if (response != null) {
			return "There was an error with the csr:\n" + response;
		}

		return null;
	}

	public String createCommonName(String user, String computer,
			String employment, String owner) {
		String common = "";
		common += user;
		
		if (employment != null)
			common+= "-ext";
		
		if (owner != "Futurice")
			common += "-"+owner;
		
		common+="-"+computer;
		
		return common.toLowerCase();
	}

	public String enterPassword(String password) {
		if (this.backend == null)
			return null;

		String response = this.backend.sendPassword(password);

		if (response != null) {
			return response;
		}

		int moved = this.moveFilesToConfig(this.backend.getZip());

		if (moved == CONFIG_FILES_DO_NOT_EXIST) {
			return "Could not find the configuration files.";
		} else if (moved == CONFIG_DIR_DOES_NOT_EXIST) {
			return "Configuration directory did not exist.";
		} else if (moved != 0) {
			return "Something went wrong";
		}
		return null;
	}

	/**
	 * Moves the configuration files to the right directory based on the OS
	 * 
	 * @return
	 */
	public int moveFilesToConfig(File zipfile) {

		String user = System.getProperty("user.name");

		File key = new File(this.commonName + ".key");
		File zip = zipfile;

		if (zip == null || !key.exists() || !zip.exists())
			return CONFIG_FILES_DO_NOT_EXIST;

		File configDir = null;

		// Mac
		if (this.os == OSX) {

			configDir = new File(
					"/Users/"
							+ user
							+ "/Library/Application Support/Tunnelblick/Configurations/");

			// Linux
		} else if (this.os == LINUX) {

			configDir = new File("/tmp/");

			// Windows
		} else if (this.os == WINDOWS) {

			configDir = new File("C:\\Program Files (x86)\\OpenVPN\\config\\");
			if (!configDir.exists())
				configDir = new File("C:\\Program Files\\OpenVPN\\config\\");

		}

		// Does conf directory exist?
		if (configDir != null && configDir.exists()) {
			if (this.commonName != null) {

				// Unzip and move key
				if (this.unzip(zip, configDir)
						&& key.renameTo(new File(configDir, key.getName())))
					return 0;

			} else {
				return ERROR;
			}

		} else {
			System.err.println("Configuration directory did not exist.");
			return CONFIG_DIR_DOES_NOT_EXIST;
		}

		return 0;
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
				zipInStream.closeEntry();
				fileInZip = zipInStream.getNextEntry();

			}

			zipInStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
