package prj.yong.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {

	/**
	 * Check the file is existed or not
	 * @param readFileExtension
	 * @throws IOException
	 */
	public static boolean isFileExist(String filePath) throws IOException {
		// Checking a file is existed
		File file = new File(filePath);
		if(!file.exists()) {
			throw new FileNotFoundException("There is no file in " + filePath); 
		}
		return true;
	}
}
