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
		this.request = this.common+".csr";
		
	}
	
	public String generateRequest(){
		
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
		
		return request;
	}
	
	private boolean genrsa(){
		try {
			String s;
		
			Process p = Runtime.getRuntime()
			.exec("openssl genrsa -out "+this.key+" -aes128 "+
					"-passout pass:"+this.pass+" 2048");

	        BufferedReader stdErr = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));
	        
	        BufferedReader stdIn = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));
	        
	        while((s=stdIn.readLine())!= null){
	        	System.out.println(s);
	        }
	       
            while ((s = stdErr.readLine()) != null){
            	System.err.println(s);
            }
	            
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean req(){
		try {
			String s;
		
			Process p = Runtime.getRuntime()
			.exec("openssl req -new -key "+this.key+" -out "+this.request+" "+
					"-config "+Generator.conf+" -passin pass:"+this.pass);

	        BufferedReader stdErr = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));
	       
            if ((s = stdErr.readLine()) != null){
            	System.err.println(s);
            	return false;
            }
	            
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
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

}
