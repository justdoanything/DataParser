package org.dataparser.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.time.format.DateTimeParseException;

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
	 * Get File Name Only
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		File file = new File(filePath);
		return file.getName();
	}

	/**
	 * Get File Path Only
	 * @param filePath
	 * @return
	 */
	public static String getFilePath(String filePath) {
		File file = new File(filePath);
		return file.getPath();
	}

	/**
	 * Set default writeFilePath if do not set manually
	 * @param readFileExtension
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 * @throws FileSystemException
	 */
	public static String setDefaultWriteFilePath(String readFilePath) throws StringIndexOutOfBoundsException, DateTimeParseException, FileSystemException {
		//if do not set writeFilePath, this should be readFilePath_{dateformat}
		String writeFilePath = "";
		if(writeFilePath == null || writeFilePath.equals(MsgCode.MSG_CODE_STRING_BLANK)) {
			String readFileName = FileUtil.getFileName(readFilePath);
			String writeFileName = readFileName + "_" + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0);
			
			if(!FileUtil.getFileExtension(readFilePath).equals(""))
				writeFileName += "." + FileUtil.getFileExtension(readFilePath);

			writeFilePath = FileUtil.getFilePath(readFilePath).replace(readFileName, writeFileName);
		}
		return writeFilePath;
	}

	/**
	 * 
	 * @param answerFilePath
	 * @param resultFilePath
	 * @return
	 * @throws IOException
	 */
	public static boolean compareFile(String filePath1, String filePath2) throws IOException{

		try(
				FileInputStream fis1 = new FileInputStream(filePath1);
				FileInputStream fis2 = new FileInputStream(filePath2);) {
      int readData1 = 0; 
			int readData2 = 0; 
      while(true){
        readData1 = fis1.read();
        readData2 = fis2.read();
        if(readData1!=readData2||(readData1!=-1&&readData2==-1)) {
          return false;
        }
        if(readData1==-1) {
          return true;
        }
      } 
    }catch (Exception e) { 
      e.printStackTrace();
    }
    return false;
  }
}
