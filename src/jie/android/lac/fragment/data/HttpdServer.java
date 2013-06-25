package jie.android.lac.fragment.data;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jie.android.lac.fragment.HttpdFragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class HttpdServer extends NanoHTTPD {
	
	private static final String Tag = HttpdServer.class.getSimpleName();
	
	protected abstract class Request {
		
		protected String uri = null;
		
		public Request(String uri) {
			this.uri = uri;
		}
		
		public final String getUri() {
			return uri;
		}
		
		public abstract Response onRequest(Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) throws IOException;
		
		protected InputStream getUriStream(final String file) throws IOException {
			return new FileInputStream(HttpdServer.getRoot() + file);
		}
	}
	
	private class BootRequest extends Request {
		
		public BootRequest() {
			super("");
		}

		@Override
		public Response onRequest(Method method, Map<String, String> header,
				Map<String, String> parms, Map<String, String> files)
				throws IOException {
			return new Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, getUriStream("index.html"));
		}		
	}
	
	public class FileRequest extends Request {

		public FileRequest(String uri) {
			super(uri);
		}

		@Override
		public Response onRequest(Method method, Map<String, String> header,
				Map<String, String> parms, Map<String, String> files)
				throws IOException {
			return new Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, getUriStream(uri));			
		}		
	}
	
	public class ImportFileDone extends Request {

		public ImportFileDone() {
			super("import_file_done.html");
		}

		@Override
		public Response onRequest(Method method, Map<String, String> header,
				Map<String, String> parms, Map<String, String> files)
				throws IOException {
			
			String ifile = parms.get("import");
			if(ifile == null || ifile.equals("")) {
				return new Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, getUriStream("err_missparameter.html"));
			}
			boolean oflag = !(parms.get("overwrite") == null || parms.get("overwrite").equals(""));
			
			String lfile = files.get("import");
			if(lfile == null || lfile.equals("")) {
				return new Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, getUriStream("err_missparameter.html"));
			}
			
			String path = Environment.getExternalStorageDirectory() + "/tmp/lac";
			File tgt = new File(path);
			if (!tgt.exists()) {
				if (!tgt.mkdirs()) {
					Log.d(HttpdServer.Tag, "request - importfiledone - temporary directory failed : " + tgt.getAbsolutePath());
				}
			}
		    String dst = tgt.getAbsolutePath() + File.separator + "import.db";			
			
		    try {
		    	copyCacheFile(lfile, dst);
		    } catch (IOException e) {
		    	Log.d(HttpdServer.Tag, "request - importfiledone failed : - " + dst);
		    }
			
			Log.d(HttpdServer.Tag, "request - importfiledone - " + dst);
			Message msg = Message.obtain(HttpdServer.handler, HttpdFragment.MSG_IMPORT_DATABASE);
			msg.getData().putString("localfile", dst);
			
			HttpdServer.handler.sendMessage(msg);
			
//			File f = new File(lfile);
//			FileInputStream fi = new FileInputStream(f);
//			int b = 0;
//			while ((b = fi.read()) != -1) {
//				Log.d("====", "b = " + b);
//			}
//			
			return new Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, getUriStream(uri));			
		}

		private boolean copyCacheFile(String lfile, String dst) throws IOException {
		    InputStream in = null;		    
		    OutputStream out = null;
			try {
				in = new FileInputStream(lfile);				
				out = new FileOutputStream(dst);
				
			    byte[] buf = new byte[1024];
			    int len = -1;
			    while ((len = in.read(buf)) > 0) {
			        out.write(buf, 0, len);
			    }
			} finally {
			    in.close();
			    out.close();				
			}
			
			return true;
		}
		
	}
	//
	protected static String root = null;
	protected static Handler handler = null;
	
	protected HashMap<String, Request> mapRequest = new HashMap<String, Request>();
	
	
	
	public HttpdServer(int port, final String wwwroot, Handler handler) throws IOException {
		super(port);
		
		HttpdServer.root = wwwroot + File.separator;
		HttpdServer.handler = handler;
		
		initRequest();
	}
	
	public static String getRoot() {
		return root;
	}

	protected void initRequest() {
		Request req = new BootRequest();
		mapRequest.put(req.getUri(), req);
		
		req = new FileRequest("favicon.ico");
		mapRequest.put(req.getUri(), req);
		
		req = new FileRequest("err_missparameter.html");
		mapRequest.put(req.getUri(), req);		
		
		req = new FileRequest("import_file.html");
		mapRequest.put(req.getUri(), req);

		req = new ImportFileDone();
		mapRequest.put(req.getUri(), req);
		
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
		
		Log.d(Tag, "httpd - method:" + method.toString() + " uri:" + uri);
		
		Request req = mapRequest.get(uri);
		try {
//			if (method == NanoHTTPD.Method.GET)
			if (req != null) {
				return req.onRequest(method, header, parms, files);
			}
		} catch (IOException e) {
			return badRequest(uri, method, header, parms, files);
		}
		
		return unsupportedRequest(uri, method, header, parms, files);
	}

}
