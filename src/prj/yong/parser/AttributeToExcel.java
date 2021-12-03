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

import prj.yong.common.MsgCode;
import prj.yong.util.DateUtil;

public class AttributeToExcel {

	private final int arraySize = 200000000;
	private String filePath = "C:\\";
	private String spliter = "\t";
	private String fileExtension = "txt";
	private boolean isFileOpen = false;
	
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSpliter() {
		return spliter;
	}

	public void setSpliter(String spliter) {
		this.spliter = spliter;
	}

	public boolean isFileOpen() {
		return isFileOpen;
	}

	public void setFileOpen(boolean isFileOpen) {
		this.isFileOpen = isFileOpen;
	}
	
	public AttributeToExcel() {
		// default is C:\
		// default is false
		// default is "\t"
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
	
	public void execute() throws Exception {
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(MsgCode.MSG_VALUE_DATE_FORMAT);
			sdf.format(new Date());
			String writeFilePath = filePath + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0);
			
			br = new BufferedReader(new FileReader(filePath));
			bw = new BufferedWriter(new FileWriter(writeFilePath));
			
			String line;
			String[] arr = new String[this.arraySize];
			
			int index = 0;
			Map<String, Map<String, String>> map = new HashMap<>();
			
			while((line = br.readLine()) != null) {
				String[] lineArr = line.split(this.spliter);
				
				if(map.containsKey(lineArr[0])) {
					map.get(lineArr[0]).put(lineArr[1],  lineArr.length == 2 ? " " : lineArr[2]);
				} else {
					map.put(lineArr[0], new HashMap<String, String>());
					map.get(lineArr[0]).put(lineArr[1], lineArr.length == 2 ? " " : lineArr[2]);
				}
			}
			
			List<String> parentKeyList = new ArrayList<>();
			for(String key : map.keySet()) {
				if(!parentKeyList.contains(key))
					parentKeyList.add(key);
			}
			
			bw.write("\t");
			List<String> keyList = new ArrayList<>();
			for(String sku : parentKeyList) {
				for(String key : (map.get(sku)).keySet()) {
					if(!keyList.contains(key)) {
						keyList.add(key);
						bw.write(key);
						bw.write("\t");
					}
				}
			}
			bw.write("\n");
			
			for(String key : parentKeyList) {
				bw.write(key);
				bw.write("\t");
				
				for(String key2 : keyList) {
					if((map.get(key)).containsKey(key2)) {
						bw.write(map.get(key).get(key2));
						bw.write("\t");
					} else {
						bw.write("");
						bw.write("\t");
					}
				}
				bw.write("\n");
			}
			
		}catch (Exception e) {

		}finally {
			if(br != null) try { br.close(); } catch(IOException e) {}
			if(bw != null) try { br.close(); } catch(IOException e) {}
		}
	}
}
