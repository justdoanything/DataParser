package prj.yong.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import msg.MsgCode;
import prj.yong.util.DateUtil;

@Getter
@Setter
public class AttributeToExcel {

	/******************************************************
	 * 
	 * This class makes Excel Chart format like below.
	 * 
	 * [ Input ]
	 * Name  | Attribute Name | Attribute Value
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV	 | Size			  | 65 inch
	 * TV	 | Company		  | LG
	 * TV	 | Quality		  | HIGH
	 * Audio | Size			  | 32
	 * Audio | Company		  | Apple
	 * Audio | Channel		  | Dual
	 * 
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * 
	 * [ Output]
	 * Name	 | Size		| Company	| Quality	| Channel
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV	 | 65 inch	| LG		| HIGH		|
	 * Autio | 32		| Apple		|			| Dual
	 *
	 ******************************************************/	
	
	/**
	 * Initial Values
	 */
	private final int arraySize = 200000000;
	private int startWithLine = 0;
	private String readfilePath = MsgCode.MSG_CODE_FILE_PATH;
	private String writeFilePath = MsgCode.MSG_CODE_STRING_BLANK;
	private String spliter = MsgCode.MSG_CODE_FILE_SPLITER;
	private String fileExtension = MsgCode.MSG_CODE_FILE_EXTENSION_TEXT;
	private boolean isFileOpen = false;
	private Map<String, Map<String, String>> codeMap = new HashMap<>();
	
	/**
	 * Class Constructor
	 */
	public AttributeToExcel() {
	}
	
	public AttributeToExcel(int startWithLine) {
		this.startWithLine = startWithLine;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath) {
		this.startWithLine = startWithLine;
		this.readfilePath = readfilePath;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath, String writeFilePath) {
		this.startWithLine = startWithLine;
		this.readfilePath = readfilePath;
		this.writeFilePath = writeFilePath;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath, String writeFilePath, String spliter) {
		this.startWithLine = startWithLine;
		this.readfilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath, String writeFilePath, String spliter, boolean isFileOpen) {
		this.startWithLine = startWithLine;
		this.readfilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isFileOpen = isFileOpen;
	}
	
	public void addCodeValue(String name, String code, String value) {
		if(!codeMap.containsKey(name)) {
			codeMap.put(name, new HashMap<String, String>());
		}
		codeMap.get(name).put(code, value);
	}
	
	/**
	 * Text File
	 * @throws Exception
	 */
	public void execute() throws Exception {
		BufferedReader br = null;
		BufferedWriter bw = null;
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		
		try {
			//if do not set writeFilePath, this should be readFilePath_{dateformat}
			SimpleDateFormat sdf = new SimpleDateFormat(MsgCode.MSG_VALUE_DATE_FORMAT);
			sdf.format(new Date());
			if(this.writeFilePath.equals(MsgCode.MSG_CODE_STRING_BLANK)) {
				this.writeFilePath = readfilePath.replace(MsgCode.MSG_CODE_FILE_EXTENSION_TEXT, "") + "_" + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0) + MsgCode.MSG_CODE_FILE_EXTENSION_TEXT;
			}
			
			// Set Reader and Writer
			br = new BufferedReader(new FileReader(readfilePath));
			bw = new BufferedWriter(new FileWriter(writeFilePath));
			
			// Put line to resultMap from file
			String line;
			while((line = br.readLine()) != null) {
				// Checking startWithLine
				if(this.startWithLine != 0) {
					this.startWithLine -= 1;
					continue;
				}
				
				String[] lineArray = line.split(this.spliter);
				if(!resultMap.containsKey(lineArray[0])) {
					resultMap.put(lineArray[0], new HashMap<String, String>());
				}

				// Change value if there is code in codeMap
				if(lineArray[2] != null && codeMap.containsKey(lineArray[1])) 
					this.changeCodeValue(lineArray);
				// Put blank " " if the attribute value is empty.
				resultMap.get(lineArray[0]).put(lineArray[1],  lineArray.length == 2 ? " " : lineArray[2]);
			}
			
			// Add name to namelist
			List<String> nameList = new ArrayList<>();
			for(String key : resultMap.keySet()) {
				if(!nameList.contains(key))
					nameList.add(key);
			}
			
			// Write attribute in first line
			bw.write(MsgCode.MSG_CODE_FIELD_NAME + this.spliter);
			List<String> attributeList = new ArrayList<>();
			for(String name : nameList) {
				for(String attribute : (resultMap.get(name)).keySet()) {
					if(!attributeList.contains(attribute)) {
						attributeList.add(attribute);
						bw.write(attribute);
						bw.write(this.spliter);
						bw.flush();
					}
				}
			}
			bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);
			bw.flush();
			
			// Write Attribute value as attribute and name
			for(String name : nameList) {
				bw.write(name);
				bw.write(this.spliter);
				
				for(String attribute : attributeList) {
					if((resultMap.get(name)).containsKey(attribute)) {
						bw.write(resultMap.get(name).get(attribute));
						bw.write(this.spliter);
					} else {
						bw.write(MsgCode.MSG_CODE_STRING_BLANK);
						bw.write(this.spliter);
					}
					bw.flush();
				}
				bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);
				bw.flush();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(br != null) try { br.close(); } catch(IOException e) {}
			if(bw != null) try { br.close(); } catch(IOException e) {}
		}
	}
	
	private void changeCodeValue(String[] lineArray) {
		String attributeName = lineArray[1];
		String attributeValue = lineArray[2];
		
		lineArray[2] = codeMap.get(attributeName).get(attributeValue) != null ? codeMap.get(attributeName).get(attributeValue) : MsgCode.MSG_CODE_STRING_NULL;
	}
}
