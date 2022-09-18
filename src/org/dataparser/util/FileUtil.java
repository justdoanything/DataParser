package org.dataparser.util;

import java.io.File;
import java.io.IOException;

import org.dataparser.msg.MsgCode;

public class FileUtil {

	/**
	 * Check the file is existed or not
	 * @param readFileExtension
	 * @throws IOException
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * Get File Extension
	 * @param filePath
	 * @return
	 */
	public static String getFileExtension(String filePath) {
		File file = new File(filePath);
		String fileName = file.getName();
		String fileExtension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : MsgCode.MSG_CODE_STRING_BLANK;
		return fileExtension;
	}

	/**
	 * Get File Name
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		File file = new File(filePath);
		return file.getName();
	}

	public static String getFilePath(String filePath) {
		File file = new File(filePath);
		return file.getPath();
	}
}
