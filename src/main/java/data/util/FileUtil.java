package data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.time.format.DateTimeParseException;

import static data.constant.FileConstant.FILE_EXTENSION_BLANK;

public class FileUtil {
    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static String getFileExtension(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        String fileExtension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : FILE_EXTENSION_BLANK;
        return fileExtension;
    }

    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    public static String getFilePath(String filePath) {
        File file = new File(filePath);
        return file.getPath();
    }

    public static String setDefaultWriteFilePath(String readFilePath) throws StringIndexOutOfBoundsException, DateTimeParseException, FileSystemException {
        //if do not set writeFilePath, this should be readFilePath_{dateformat}
        String writeFilePath = "";
        if (writeFilePath == null || writeFilePath.equals(FILE_EXTENSION_BLANK)) {
            String readFileName = FileUtil.getFileName(readFilePath);
            String writeFileName = readFileName + "_" + DateUtil.getDate("yyyyMMddHHmmss", 0);

            if (!FileUtil.getFileExtension(readFilePath).equals(""))
                writeFileName += "." + FileUtil.getFileExtension(readFilePath);

            writeFilePath = FileUtil.getFilePath(readFilePath).replace(readFileName, writeFileName);
        }
        return writeFilePath;
    }

    public static boolean compareFile(String filePath1, String filePath2) throws IOException {
        try (
                FileInputStream fis1 = new FileInputStream(filePath1);
                FileInputStream fis2 = new FileInputStream(filePath2);) {
            int readData1 = 0;
            int readData2 = 0;
            while (true) {
                readData1 = fis1.read();
                readData2 = fis2.read();
                if (readData1 != readData2 || (readData1 != -1 && readData2 == -1)) {
                    return false;
                }
                if (readData1 == -1) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
