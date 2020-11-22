package com.jiahao.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MD2FileUtil {

	public static ByteArrayOutputStream inputStream2ByteArrayOutputStream(InputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[1024];
    	int len;
    	while ((len = is.read(buffer)) > -1 ) {
    		baos.write(buffer, 0, len);
    	}
    	baos.flush();	
        return baos;
	}
	
}
