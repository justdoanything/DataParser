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

import javax.print.DocFlavor.STRING;

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
	private String filePath = MsgCode.MSG_CODE_FILE_PATH;
	private String spliter = MsgCode.MSG_CODE_FILE_SPLITER;
	private String fileExtension = MsgCode.MSG_CODE_FILE_EXTENSION_TEXT;
	private boolean isFileOpen = false;
	private Map<String, Map<String, String>> codeMap = new HashMap<>();
	
	/**
	 * Class Constructor
	 */
	public AttributeToExcel() {
	}
	
	public AttributeToExcel(String filePath) {
		this.filePath = filePath;
	}
	
	public AttributeToExcel(String filePath, String spliter) {
		this.filePath = filePath;
		this.spliter = spliter;
	}
	
	public AttributeToExcel(String filePath, String spliter, boolean isFileOpen) {
		this.filePath = filePath;
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
			SimpleDateFormat sdf = new SimpleDateFormat(MsgCode.MSG_VALUE_DATE_FORMAT);
			sdf.format(new Date());
			String writeFilePath = filePath.replace(MsgCode.MSG_CODE_FILE_EXTENSION_TEXT, "") + "_" + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0) + MsgCode.MSG_CODE_FILE_EXTENSION_TEXT;
			
			br = new BufferedReader(new FileReader(filePath));
			bw = new BufferedWriter(new FileWriter(writeFilePath));
			System.out.println(filePath);
			System.out.println(writeFilePath);
			String line;
			
			// 
			while((line = br.readLine()) != null) {
				String[] lineArray = line.split(this.spliter);
				
				if(!resultMap.containsKey(lineArray[0])) {
					resultMap.put(lineArray[0], new HashMap<String, String>());
				}

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
					}
				}
			}
			bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);
			
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
				}
				bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);
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
		
//		lineArray[2] = codeMap.get(attributeName).get(attributeValue) == null ? MsgCode.MSG_CODE_STRING_NULL : codeMap.get(attributeName).get(attributeValue);
		lineArray[2] = codeMap.get(attributeName).containsValue(attributeValue) ? MsgCode.MSG_CODE_STRING_NULL : codeMap.get(attributeName).get(attributeValue);
		
//		if(lineArr[1].equals("Model Type")) {
//    		switch(lineArr[2]) {
//        		case "54" : lineArr[2] = "G"; break;
//        		case "57" : lineArr[2] = "A"; break;
//        		case "60" : lineArr[2] = "C"; break;
//        		case "63" : lineArr[2] = "D"; break;
//        		default :   lineArr[2] = "null";
//    		}
//    	} else if (lineArr[1].equals("Required Visit")) {
//    		switch(lineArr[2]) {
//        		case "66" : lineArr[2] = "N"; break;
//        		case "69" : lineArr[2] = "M"; break;
//        		case "72" : lineArr[2] = "O"; break;
//        		default :   lineArr[2] = "null";
//    		}
//    	} else if (lineArr[1].equals("FO Update Status")){
//    		if(lineArr[2].equals("1")) lineArr[2] = "Enable";
//    		else lineArr[2] = "disable";
//    	} else if (lineArr[1].equals("Is SET")){
//    		if(lineArr[2].equals("1")) lineArr[2] = "Enable";
//    		else lineArr[2] = "disable";
//    	} else if (lineArr[1].equals("Is Salable")){
//    		switch(lineArr[2]) {
//        		case "75" : lineArr[2] = "N"; break;
//        		case "78" : lineArr[2] = "Y"; break;
//        		default :   lineArr[2] = "null";
//    		}
//    	} else if (lineArr[1].equals("Is Update")){
//    		if(lineArr[2].equals("1")) lineArr[2] = "Enable";
//    		else lineArr[2] = "disable";
//    	} else if (lineArr[1].equals("Delivery Type")){
//    		switch(lineArr[2]) {
//        		case "39" : lineArr[2] = "H"; break;
//        		case "42" : lineArr[2] = "P"; break;
//        		default :   lineArr[2] = "null";
//    		}
//    	} else if (lineArr[1].equals("Enable RMA")){
//    		if(lineArr[2].equals("1")) lineArr[2] = "Enable";
//    		else lineArr[2] = "disable";
//    	} else if (lineArr[1].equals("Display Status")){
//    		switch(lineArr[2]) {
//        		case "45" : lineArr[2] = "ACTIVE"; break;
//        		case "48" : lineArr[2] = "SUSPENED"; break;
//        		case "51" : lineArr[2] = "DISCONTINUED"; break;
//        		default :   lineArr[2] = "null";
//    		}
//    	} else if (lineArr[1].equals("Install Type")){
//    		switch(lineArr[2]) {
//	    		case "81" : lineArr[2] = "A"; break;
//	    		case "84" : lineArr[2] = "R"; break;
//	    		case "87" : lineArr[2] = "Z"; break;
//	    		case "90" : lineArr[2] = "Y"; break;
//	    		default :   lineArr[2] = "null";
//			}
//    	}
	}
	
	public static void main(String[] args) {
		AttributeToExcel ate = new AttributeToExcel("C:\\Users\\82736\\Desktop\\attr.txt");
		try {
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "57", "A");
			ate.addCodeValue("Model Type", "60", "C");
			ate.addCodeValue("Model Type", "63", "D");
			ate.addCodeValue("Model Type", "54", "G");
			
			ate.addCodeValue("Required Visit", "66", "N");
			ate.addCodeValue("Required Visit", "69", "M");
			ate.addCodeValue("Required Visit", "72", "O");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			ate.addCodeValue("Model Type", "54", "G");
			
			ate.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
