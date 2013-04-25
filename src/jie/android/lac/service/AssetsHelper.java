package jie.android.lac.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class AssetsHelper {

	public static int UnzipTo(final InputStream input, final String outputPath) {
		
		File file = new File(outputPath);
		if(!file.exists()) {
			return -1;
		}
		
		ZipInputStream zipStream = new ZipInputStream(input);

		try {
			byte[] buf = new byte[64 * 1024];
			
			ZipEntry zipEntry = null;
			while((zipEntry = zipStream.getNextEntry())!= null) {
				file = new File(outputPath + File.separator + zipEntry.getName());
				Log.d("DDofLAC", "ZIP FILE = " + zipEntry.getName());				
				if(!zipEntry.isDirectory()) {
					if(!file.createNewFile())
						return -1;
					FileOutputStream output = new FileOutputStream(file);
					int size = -1;
					while((size = zipStream.read(buf)) > 0) {
						output.write(buf, 0, size);
					}
					output.close();
				} else {
					file.mkdir();
				}
			}
			
			zipStream.close();
			
		} catch (IOException e) {
			return -1;
		}
				
		return 0;
	}
	
}
