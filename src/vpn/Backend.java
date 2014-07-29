/**
 * This class represents the backend. It connects to the
 * vpnmanagement server and sends JSON back and forth.
 */

package vpn;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import org.apache.commons.codec.binary.*;

public class Backend {

	private String user;
	private String pass;
	private String file;
	private Configurator config;

	/**
	 * The constructor needs the user's username and password
	 * 
	 * @param username
	 * @param password
	 */
	public Backend(String username, String password, Configurator config) {
		this.user = username;
		this.pass = password;
		this.file = "settings.zip";
		this.config = config;
	}

	/**
	 * This method sends the CSR request to the server in JSON form.
	 * 
	 * @param csr
	 * @return
	 */
	public String sendCSR(String csr) {

		String urlString = this.config.getSettings("API_BASE_URL") + "/api/post_csr";

		String response = this.send(urlString, "csr", csr);

		if (response == null)
			return "The connection to the server failed." +
					"\nCheck your username and password and make" +
					"\nsure you're connected to the Internet.";

		// Try to parse the response
		try {
			JSONObject r = new JSONObject(response);

			if (r.getBoolean("success")) {
				return null;
			} else {
				return r.getString("message");
			}

		} catch (Exception e) {
			return "JSON Exception:\n" + e.getMessage();
		}
	}

	/**
	 * This method sends the password to the server in JSON.
	 * 
	 * @param password
	 * @return
	 */
	public String sendPassword(String password) {

		String response = this.send(
				this.config.getSettings("API_BASE_URL") + "/api/post_verification",
				"password", password);

		if (response == null)
			return "The connection to the server failed.";

		// Try to parse the response
		try {
			JSONObject r = new JSONObject(response);

			if (r.getBoolean("success")) {
				config.setStatus("Downloading settings.");
				return this.downloadZip(r.getString("zip_url"));
			} else if (r.has("correct_password")
					&& !r.getBoolean("correct_password")) {
				return "Wrong password.";
			} else {
				return r.getString("message");
			}

		} catch (Exception e) {
			return "JSON Exception:\n" + e.getMessage();
		}
	}

	/**
	 * This method returns the File object that is the .zip file with the
	 * configuration files.
	 * 
	 * @return
	 */
	public File getZip() {

		return new File(this.file);
	}

	/**
	 * This method downloads the zip file from the specified url.
	 * @param urlString
	 * @return
	 */
	private String downloadZip(String urlString) {

		OutputStream os = null;
		InputStream is = null;
		try {

			URL url = new URL(urlString);

			// Encode the user name and password
			String encoding = new String(Base64
							.encodeBase64(StringUtils
									.getBytesUtf8(this.user + ":" + this.pass)));

			// Open connection to server
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			
			connection.setRequestProperty("Authorization", "Basic " + encoding);

			int size = 1024;

			byte[] buf;
			int ByteRead, ByteWritten = 0;
			
			os = new BufferedOutputStream(new FileOutputStream(this.file));
			is = connection.getInputStream();
			
			buf = new byte[size];
			while ((ByteRead = is.read(buf)) != -1) {
				os.write(buf, 0, ByteRead);
				ByteWritten += ByteRead;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "Could not download settings.";
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
				return "Could not close the connection.";
			}
		}

		return null;
	}

	/**
	 * This is a helper method that takes care of esablishing the connection and
	 * POSTing the data. It returns the server response.
	 * 
	 * @param urlString
	 * @param fieldname
	 * @param value
	 * @return
	 */
	private String send(String urlString, String fieldname, String value) {

		// The field and value is encoded for the request
		String postParameters = "";
		try {
			postParameters = URLEncoder.encode(fieldname, "UTF-8") + "="
					+ URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
		String response = "";
		try {

			URL url = new URL(urlString);
			this.config.setStatus("Trying to encode authentication.");
			// Encode the user name and password
			String encoding = new String(Base64
							.encodeBase64(StringUtils
									.getBytesUtf8(this.user + ":" + this.pass)));
			
			this.config.setStatus("Encoded authentication");

			// Open connection to server
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			this.config.setStatus("Opened connection.");
			// Set some connection values
			connection.setConnectTimeout(10000);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(postParameters.getBytes().length));
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + encoding);

			// Send request
			DataOutputStream os = new DataOutputStream(
					connection.getOutputStream());
			os.writeBytes(postParameters);
			os.flush();
			os.close();
			this.config.setStatus("Connection open.");
			// Read the response
			InputStream content = (InputStream) connection.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					content));
			String line;
			while ((line = in.readLine()) != null) {
				response += line;
			}
			this.config.setStatus("Connection closed.");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return response;
	}

}
