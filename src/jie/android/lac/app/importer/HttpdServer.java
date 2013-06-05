package jie.android.lac.app.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

public class HttpdServer extends NanoHTTPD {

	Context context = null;
	Handler handler = null;
	
	public HttpdServer(Context context, Handler handler, int port) throws IOException {
		super(port, null);
		this.context = context;
		this.handler = handler;
	}

	@Override
	public Response serve(String uri, String method, Properties header,	Properties parms, Properties files) {

		displayDebugInfo(uri, method, header, parms, files);	

		if(uri.length() > 0 && uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		
		try {
			if(method.equals("GET")) {
				if(uri.equals(""))
				{
					uri = "index.html";
				}
				if(uri.equals("index.html") || uri.equals("index_eng.html")) {
					return requestRoot(uri, method, header, parms, files);
				}
			}
			else if(method.equals("POST")) {
				if(uri.equals("import_file.html") || uri.equals("import_file_eng.html")) {
					return requestImportFile(uri, method, header, parms, files);
				}
				else if(uri.equals("import_file_done.html") || uri.equals("import_file_done_eng.html")) {
					return requestImportFileDone(uri, method, header, parms, files);
				}				
				else if(uri.equals("input_data.html") || uri.equals("input_data_eng.html")) {
					return requestInputData(uri, method, header, parms, files);
				}
				else if(uri.equals("input_data_done.html") || uri.equals("input_data_done_eng.html")) {
					return requestInputDataDone(uri, method, header, parms, files);
				}			
			}
		} catch (IOException ex) {
			return badRequest();
		}		
		return unsupportedRequest(uri, method, header, parms, files);
	}

	@SuppressWarnings("unused")
	private void displayDebugInfo(String uri, String method, Properties header,	Properties parms, Properties files) {
		myOut.println( method + " '" + uri + "' " );
		
		Enumeration e = header.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			myOut.println( "  HDR: '" + value + "' = '" +
								header.getProperty( value ) + "'" );
		}
		e = parms.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			myOut.println( "  PRM: '" + value + "' = '" +
								parms.getProperty( value ) + "'" );
		}
		e = files.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			myOut.println( "  FLE: '" + value + "' = '" +
								files.getProperty( value ) + "'" );
		}		
	}

	private Response unsupportedRequest(String uri, String method, Properties header, Properties parms, Properties files) {
		
		return new Response(HTTP_NOTFOUND, MIME_PLAINTEXT,	"Error 404, file not found. -- codejie");
	}
	
	private Response missInputParameter(String uri, String method, Properties header, Properties parms, Properties files) throws IOException {
		if(uri.indexOf("_eng") == -1) {
			InputStream stream = context.getAssets().open("err_missparameter.html");
			return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, stream);
		}
		else {
			InputStream stream = context.getAssets().open("err_missparameter_eng.html");
			return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, stream);			
		}
	}

	private Response requestInputDataDone(String uri, String method, Properties header, Properties parms, Properties files) throws IOException {

		Message msg = Message.obtain(handler, HttpdActivity.MSG_INPUT_DATA);

		String str = parms.getProperty("dict");
		if(str == null || str.equals("")) {
			msg.getData().putString("dict", "Default Dictionary");	
		}
		else {
			msg.getData().putString("dict", str);
		}	
	
		str = parms.getProperty("word");
		if(str == null || str.equals(""))
			return missInputParameter(uri, method, header, parms, header);
		msg.getData().putString("word", str);
		
		str = parms.getProperty("symbol");
		if(str == null || str.equals("")) {
			msg.getData().putString("symbol", "");	
		}
		else {
			msg.getData().putString("symbol", str);
		}

		str = parms.getProperty("category1");
		if(str == null || str.equals("")) {
			msg.getData().putString("category1", "");	
		}
		else {
			msg.getData().putString("category1", str);
		}
		
		str = parms.getProperty("meaning1");
		if(str == null || str.equals(""))
			return missInputParameter(uri, method, header, parms, header);
		msg.getData().putString("meaning1", str);

		str = parms.getProperty("meaning2");
		if(str != null && !str.equals("")) {
			msg.getData().putString("meaning2", str);
			
			str = parms.getProperty("category2");
			if(str == null || str.equals("")) {
				msg.getData().putString("category2", "");	
			}
			else {
				msg.getData().putString("category2", str);
			}
		}
		
		str = parms.getProperty("meaning3");
		if(str != null && !str.equals("")) {
			msg.getData().putString("meaning3", str);
			
			str = parms.getProperty("category3");
			if(str == null || str.equals("")) {
				msg.getData().putString("category3", "");	
			}
			else {
				msg.getData().putString("category3", str);
			}
		}
		
		handler.sendMessage(msg);
		
		InputStream stream = context.getAssets().open(uri);//"input_data_done.html");
		return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, stream);		
	}

	private Response requestInputData(String uri, String method, Properties header, Properties parms, Properties files) throws IOException {
		InputStream stream = context.getAssets().open(uri);//"input_data.html");
		return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, stream);
	}

	private Response requestImportFileDone(String uri, String method, Properties header, Properties parms, Properties files) throws IOException {

		String ifile = parms.getProperty("import");
		if(ifile == null || ifile.equals("")) {
			return missInputParameter(uri, method, header, parms, header);
		}
		boolean oflag = !(parms.getProperty("overwrite") == null || parms.getProperty("overwrite").equals(""));
		
		String lfile = files.getProperty("import");
		if(lfile == null || lfile.equals("")) {
			return missInputParameter(uri, method, header, parms, header);
		}
		
		Message msg = Message.obtain(handler, HttpdActivity.MSG_IMPORT_FILE);
		msg.getData().putString("file", ifile);
		msg.getData().putString("local", lfile);
		msg.getData().putBoolean("overwrite", oflag);

		handler.sendMessage(msg);
		
		InputStream stream = context.getAssets().open(uri);//"import_file_done.html");
		return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, stream);
	}

	private Response requestImportFile(String uri, String method, Properties header, Properties parms, Properties files) throws IOException {
		InputStream stream = context.getAssets().open(uri);//"import_file.html");
		return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, stream);
	}

	private Response requestRoot(String uri, String method, Properties header, Properties parms, Properties files) throws IOException {
		InputStream stream = context.getAssets().open(uri);//"index.html");
		return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, stream);
	}

	private Response badRequest() {
		return new Response(HTTP_INTERNALERROR, MIME_PLAINTEXT,	"500 Internal Server Error");	
	}

}
