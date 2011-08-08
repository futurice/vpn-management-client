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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Generator {
	
	String pass;
	String common;
	String email;
	String key;
	String request;
	
	static String conf = "temp.conf";
	
	/**
	 * @param password, common name, email
	 */
	
	public Generator(String pass, String common, String email){
		this.pass = pass;
		this.email = email;
		this.common = common;
		this.key = this.common+".key";
		this.request = "";
		
	}
	
	/**
	 * Generate the .key and .csr files.
	 * @return
	 */
	public String generateRequest(){
		
		//Check for openssl
		if(!this.hasOpenSSL()){
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
	
	/**
	 * Runs the "openssl genrsa ..." command
	 * @return
	 */
	private boolean genrsa(){
		try {
			String s;
		
			Process p = Runtime.getRuntime()
			.exec("openssl genrsa -out "+this.key+" -aes128 "+
					"-passout pass:"+this.pass+" 2048");
			
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
		
			Process p = Runtime.getRuntime()
			.exec("openssl req -new -key "+this.key+" "+
					"-config "+Generator.conf+" -passin pass:"+this.pass);
			
			if (p.waitFor() != 0)
				return false;
			
			BufferedReader stdOut = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));
	        
	        while((s=stdOut.readLine())!= null){
	        	this.request+=s+"\n";
	        }

	        BufferedReader stdErr = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));
	       
            if ((s = stdErr.readLine()) != null){
            	System.err.println(s);
            	return false;
            }
	            
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
							"default_bits           = 2048\n"+
							"default_keyfile        = "+this.key+"\n"+
							"distinguished_name     = req_distinguished_name\n"+
							"prompt                 = no\n"+
							"[ req_distinguished_name ]\n"+
							"C                      = FI\n"+
							"ST                     = Uusimaa\n"+
							"L                      = Helsinki\n"+
							"O                      = Futurice Oy\n"+
							"OU                     = OpenVPN Machines\n"+
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
	public boolean hasOpenSSL(){
		Process p;
		try{
			p = Runtime.getRuntime().exec("openssl version");
			
			if (p.waitFor() == 0)
				return true;
			else
				return false;
			
		} catch (Exception e){
			return false;
		}
		
	}
}
