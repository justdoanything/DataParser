package org.dataparser.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;

import org.dataparser.msg.MsgCode;

public class FileUtil {

	/**
	 * Check the file is existed or not
	 * @param readFileExtension
	 * @throws IOException
	 */
	public static boolean isFileExist(String filePath) throws FileNotFoundException {
		// Checking a file is existed
		File file = new File(filePath);
		if(!file.exists()) {
			throw new FileNotFoundException("There is no file in " + filePath); 
		}
		return true;
	}

	public static String getFileExtention(String filePath) throws FileSystemException {
		File file = new File(filePath);
		String fileName = file.getName();
		String fileExtention = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : MsgCode.MSG_CODE_STRING_BLANK;
		
		return fileExtention;
	}
}
