package jie.android.lac.fragment.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import android.net.Uri;


public class HttpdServer extends NanoHTTPD {

	protected abstract class Request {
		
		private String uri = null;
		
		public Request(String uri) {
			this.uri = uri;
		}
		
		public final String gerUri() {
			return uri;
		}
		
		public abstract Response onRequest(String method, Properties header, Properties parms, Properties files);
		
		protected InputStream getUriStream() {
			
		}
	}
	
	private class BootRequest extends Request {

		public BootRequest(String uri) {
			super(uri);
		}
		
	}
	
	protected HashMap<String, Request> mapRequest = new HashMap<String, Request>();
	
	
	public HttpdServer(int port, File wwwroot) throws IOException {
		super(port, wwwroot);
		// TODO Auto-generated constructor stub
	}
	
	protected void initRequest() {
		
	}

}
