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
	
	int os;

	public Configurator() {

		this.commonName = null;
		this.zipFile = "zipfile.zip";
		
		
		//Try to figure out the OS
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
	
	public void gui(){
		GUI gui = new GUI(this);
	}

	// TODO Lots o' stuff

	public boolean generateSettings(String pass, String common, String email) {

		this.commonName = common;

		if (pass == null || common == null || email == null)
			return false;

		Generator gen = new Generator(pass, common, email);
		String request = gen.generateRequest();

		// Interact with server

		return true;
	}
	
	/**
	 * Moves the configuration files to the right directory based on the OS
	 * @return
	 */

	public int moveFilesToConfig() {

		String user = System.getProperty("user.name");
		
		File key = new File(this.commonName + ".key");
		File zip = new File("zipfile.zip");
		
		if (!(key.exists() && zip.exists()))
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
		
		//Does conf directory exist?
		if (configDir != null && configDir.exists()) {
				if (this.commonName != null) {
					
					//Unzip and move key
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
	 * Method for unzipping a zipfile with configuration files
	 * @param zipfile
	 * @param confDir, the directory where the configurations should be placed
	 * @return true or false based success
	 */

	private boolean unzip(File zipfile, File confDir) {
		try
        {
            byte[] buf = new byte[1024];
            
            ZipEntry fileInZip;
            ZipInputStream zipInStream = new ZipInputStream(
                new FileInputStream(zipfile));

            fileInZip = zipInStream.getNextEntry();
            while (fileInZip != null) 
            { 
                String entryName = fileInZip.getName();
                int n;
                File newFile = new File(entryName);
                String directory = newFile.getParent();
                
                if(directory == null)
                {
                    if(newFile.isDirectory())
                        break;
                }
                
                FileOutputStream fileoutputstream = new FileOutputStream(
                   confDir.getAbsolutePath()+"/"+entryName);             

                while ((n = zipInStream.read(buf, 0, 1024)) > -1)
                    fileoutputstream.write(buf, 0, n);

                fileoutputstream.close(); 
                zipInStream.closeEntry();
                fileInZip = zipInStream.getNextEntry();

            }

            zipInStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
		return true;
	}
}
