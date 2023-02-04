package org.dataparser.parser.impl;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import lombok.*;
import org.dataparser.msg.MsgCode;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonToFile {
	/**
	 *
	 * @return
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 * @throws Exception
	 * @throws NullPointerException
	 * @throws FileNotFoundException
	 * @throws FileSystemException
	 */
	public String parse() throws StringIndexOutOfBoundsException, DateTimeParseException, Exception, NullPointerException, FileNotFoundException, FileSystemException {

		this.validRequiredValues();

		return "";
	}

	/**
	 *
	 * @throws Exception
	 * @throws NullPointerException
	 * @throws FileNotFoundException
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 * @throws FileSystemException
	 */
	private void validRequiredValues() throws Exception, NullPointerException, FileNotFoundException, StringIndexOutOfBoundsException, DateTimeParseException, FileSystemException {
		// if(!FileUtil.isFileExist(this.readFilePath))
		// 	throw new FileNotFoundException("There is no file in " + this.readFilePath);

		// if(this.isWriteFile && (this.writeFilePath == null || this.writeFilePath.length() == 0))
		// 	this.writeFilePath = FileUtil.setDefaultWriteFilePath(this.readFilePath);

		// if(this.readFilePath == null || (this.isWriteFile && this.writeFilePath == null) || this.spliter == null || this.tableName == null)
		// 	throw new NullPointerException("A required value has an exception : all of values cannot be null");

		// if(this.tableName.length() < 1)
		// 	throw new NullPointerException("A required value has an exception : tableName must be set.");

		// if(!this.isWriteFile && !this.isGetString)
		// 	throw new Exception("A required value has an exception : Either isWriteFile or isGetString must be true.");

		// if(!this.isWriteFile && this.isOpenFile)
		// 	throw new Exception("A required value has an exception : isOpenFile must be false if isWriteFile is true.");

		// if(FileUtil.getFileExtension(this.readFilePath).equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV) && !this.spliter.equals(","))
		// 	throw new Exception("A required value has an exception : csv file must be ','.");
	}

	/**
	 *
	 * @param filePath
	 * @param autoFileOpen
	 * @return
	 * @throws IOException
	 */
	public String makeExcelData(String filePath, boolean autoFileOpen) throws IOException {

		StringBuilder resultString = new StringBuilder();
		readFilePath = filePath;
		writeFilePath = readFilePath.replace(".txt", "_excel.txt");

		try (
			BufferedReader br = new BufferedReader(new FileReader(readFilePath));
			BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath));
		) {
			String line;
			JSONArray dataList = new JSONArray();
			while ((line = br.readLine()) != null) {
				dataList.put(line);
			}

			ArrayList<String> keyList = new ArrayList<>();
			for (Object data : dataList) {
				JSONObject json = new JSONObject(data.toString());
				for (String key : json.keySet()) {
					if (!keyList.contains(key))
						keyList.add(key);
				}
			}

			for (int i = 0; i < keyList.size(); i++) {
				String key = keyList.get(i);
				System.out.print(key);
				bw.write(key);

				if (i != keyList.size() - 1) {
					System.out.print("\t");
					bw.write("\t");
				}
			}
			System.out.println();
			bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);

			for (Object data : dataList) {
				JSONObject json = new JSONObject(data.toString());
				for (int i = 0; i < keyList.size(); i++) {
					String key = keyList.get(i);
					if (json.isNull(key)) {
						System.out.print("");
						bw.write("");
					} else {
						System.out.print(json.get(key));
						bw.write(json.getString(key));
					}
					if (i != keyList.size() - 1) {
						System.out.print("\t");
						bw.write("\t");
					}
				}
				System.out.println();
				bw.write("\n");
			}

			bw.close();

			if (autoFileOpen)
				Desktop.getDesktop().edit(new File(writeFilePath));

				return writeFilePath;
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		} catch (IOException e) {
			throw new IOException(e);
		}
	}
}
