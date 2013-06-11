package jie.android.lac.fragment.data;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class HttpdServer extends NanoHTTPD {
	
	protected abstract class Request {
		
		private String uri = null;
		
		public Request(String uri) {
			this.uri = uri;
		}
		
		public final String gerUri() {
			return uri;
		}
		
		public abstract Response onRequest(Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) throws IOException;
		
		protected InputStream getUriStream(final String file) throws IOException {
			return new FileInputStream(HttpdServer.getRoot() + file);
		}
	}
	
	private class BootRequest extends Request {

		public BootRequest(String uri) {
			super(uri);
		}

		@Override
		public Response onRequest(Method method, Map<String, String> header,
				Map<String, String> parms, Map<String, String> files)
				throws IOException {
			return new Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, getUriStream("index.html"));
		}		
	}
	
	//
	protected static String root = null;
	
	protected HashMap<String, Request> mapRequest = new HashMap<String, Request>();
	
	
	
	public HttpdServer(int port, final String wwwroot) throws IOException {
		super(port);
		
		HttpdServer.root = wwwroot + File.separator;
		
		initRequest();
	}
	
	public static String getRoot() {
		return root;
	}

	protected void initRequest() {
		mapRequest.put("", new BootRequest("index.html"));
	}

	private Response unsupportedRequest(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
		return new Response(NanoHTTPD.Response.Status.NOT_FOUND, MIME_PLAINTEXT,	"Error 404, file not found. -- codejie");
	}

	private Response badRequest(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
		return new Response(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT,	"500 Internal Server Error");
	}

	@Override
	public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
		if (uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		
		Request req = mapRequest.get(uri);
		try {
			if (req != null) {
				return req.onRequest(method, header, parms, files);
			}
		} catch (IOException e) {
			return badRequest(uri, method, header, parms, files);
		}
		
		return unsupportedRequest(uri, method, header, parms, files);
	}

}
