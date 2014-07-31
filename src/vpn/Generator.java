/**
 * This class is used to generate a .csr and a .key file
 * to use in OpenVPN applications.
 * 
 * When generating a csr + key, the class does the following:
 * 1. Test for openssl
 * 2. Create a template of information for openssl
 * 3. Generate the key
 * 4. Generate the .csr
 * 5. Remove temporary files
 * 
 * Step 5 is the only one allowed to fail, and if it does a
 * warning is printed to stderr.
 */


package vpn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class Generator {
	
	String pass;
	String common;
	String email;
	String key;
	String request;
	String openssl;
	
	static String conf = "temp.conf";
	
	private Configurator config;

	/**
	 * @param password, common name, email
	 */
	
	public Generator(String pass, String common, String email, Configurator config){
		this.pass = pass;
		this.email = email;
		this.common = common;
		this.key = this.common+".key";
		this.request = "";
		this.config = config;
		
	}
	
	/**
	 * Generate the .key and .csr files.
	 * @return
	 */
	public String generateRequest(){
		
		//Check for openssl
		this.openssl = openSSLPath();
		if(this.openssl==null){
			return null;
		}
		
		//Try to create the config for openssl
		if (!this.generateConfigTemplate())
			return null;
		
		//Try to run the genrsa
		if (!this.genrsa())
			return null;
		
		//Try to run the req
		if (!this.req())
			return null;
		
		//Deleting the temporary config file used by openssl
		File confFile = new File(Generator.conf);
		if (!confFile.delete())
			System.err.println("Temporary config file ("+Generator.conf+") was not deleted. Please delete it manually.");
		
		return this.request;
	}
	
	public boolean moveKeyTo(File directory){
		File keyFile = new File(this.key);
		return keyFile.renameTo(new File(directory, keyFile.getName()));
	}
	
	/**
	 * Runs the "openssl genrsa ..." command
	 * @return
	 */
	private boolean genrsa(){
		try {
			String s;
		
			String[] cmd = {this.openssl,"genrsa", "-out", this.key,"-aes128",
					"-passout", "pass:"+this.pass, "2048"};
			Process p = Runtime.getRuntime()
			.exec(cmd);
			
			if (p.waitFor() != 0)
				return false;
			
	        BufferedReader stdErr = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));
	        
	        while ((s = stdErr.readLine()) != null){
            	System.err.println(s);
            }
	            
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Runs the "openssl req ..." command
	 * @return
	 */
	private boolean req(){
		try {
			String s;
			File confFile = new File(Generator.conf);
			
			String[] cmd = {this.openssl, "req", "-new", "-key", this.key,
					"-config", confFile.getAbsolutePath(), "-passin", "pass:"+this.pass};
		
			Process p = Runtime.getRuntime()
			.exec(cmd);
			
			
			
			BufferedReader stdOut = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));
	        
	        while((s=stdOut.readLine())!= null){
	        	this.request+=s+"\n";
	        }

	        BufferedReader stdErr = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));
	       
            if ((s = stdErr.readLine()) != null){
            	System.err.println(s);
            }
            
            if (p.waitFor() != 0)
				return false;
	            
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Generates the template that openssl will use to create the csr
	 * @return if the command was successful
	 */
	@SuppressWarnings("unused")
	private boolean generateConfigTemplate(){
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(Generator.conf);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		if (fw != null) {
			try {
				fw.write("[ req ]\n"+
							"default_bits           = " + this.config.getSettings("DEFAULT_BITS") + "\n"+
							"default_keyfile        = "+this.key+"\n"+
							"distinguished_name     = req_distinguished_name\n"+
							"prompt                 = no\n"+
							"[ req_distinguished_name ]\n"+
							"C                      = " + this.config.getSettings("C") + "\n"+
							"ST                     = " + this.config.getSettings("ST") + "\n"+
							"L                      = " + this.config.getSettings("L") + "\n"+
							"O                      = " + this.config.getSettings("O") + "\n"+
							"OU                     = " + this.config.getSettings("OU") + "\n"+
							"CN                     = "+this.common+"\n"+
							"emailAddress           = "+this.email+"\n");
				fw.close();
				return true;

			} catch (IOException io) {
				io.printStackTrace();
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks for openssl
	 * @return
	 */
	public static String openSSLPath(){
		
		String openssl = "openssl";
		if (System.getProperty("os.name").startsWith("Windows")){
		    String base = "C:\\";
		    File cDrive = new File(base);

		    final IOFileFilter fileFilter = new IOFileFilter() {
			    @Override
			    public boolean accept(File file) {
				return file.getName().toLowerCase().contains(openssl+".exe");
			    }
			    @Override
			    public boolean accept(File dir, String name) {
				return name.toLowerCase().contains(openssl+".exe");
			    }
			};

		    final IOFileFilter dirFilter = new IOFileFilter() {
			    @Override
			    public boolean accept(File file) {
				String nameLower = file.getName().toLowerCase();
				return nameLower.contains("program files") || nameLower.contains("openvpn") || nameLower.contains("bin");
			    }
			    @Override
			    public boolean accept(File dir, String name) {
				String nameLower = name.toLowerCase();
				return nameLower.contains("program files") || nameLower.contains("openvpn") || nameLower.contains("bin");
			    }
			};

		    Iterator iter =  FileUtils.iterateFiles(cDrive, fileFilter, dirFilter);

		    if(!iter.hasNext()) {
			return null;
		    }

		}
		
		Process p;
		try{
			p = Runtime.getRuntime().exec(openssl+" version");
			
			if (p.waitFor() == 0)
				return openssl;
			else
				return null;
			
		} catch (Exception e){
			return null;
		}
		
	}
}
