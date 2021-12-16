package prj.yong.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import msg.MsgCode;

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class ObjectToInsertQuery {

	/******************************************************
	 * 
	 * This class creates an bulk insert query using an Object with local variables.
	 * This is useful for converting VO Object and DTO Object to insert query.
	 * 
	 * public class sampleVO {
	 * 	private String value1;
	 * 	private String value2;
	 *  private int value3;
	 * }
	 * 
	 * input parameter is List<sampleVO> of size 3
	 *  
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * 
	 * INSERT INTO {tableName} (value1, value2, value3) VALUES 
	 * ('test1', 'test2', '111'),
	 * ('test1', 'test2', '111'),
	 * ('test1', 'test2', '111');
	 *
	 ******************************************************/
	
	/**
	 * Initial Values
	 */
	private int bulkInsertCnt = 100;
	private String tableName = MsgCode.MSG_CODE_STRING_BLANK;
	private Map<String, String> addtionalFieldMap = new HashMap<>();
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> getQuery(Object obj, JSONArray contentList) throws Exception {
		List<String> resultList = this.writeBulkInsertQuery(obj,contentList,bulkInsertCnt);
		return resultList;
	}
	
	/**
	 * Add addtional field to AddtionalFieldMap
	 * @param name
	 * @param code
	 * @param value
	 */
	public void addAddtionalFieldMap(String key, String value) {
		addtionalFieldMap.put(key, value);
	}
	
	/**
	 * 
	 * @param vo
	 * @param additional
	 * @param contentList
	 * @param bulkInsertCnt
	 * @return
	 * @throws Exception
	 */
	private List<String> writeBulkInsertQuery(Object vo, JSONArray contentList, int bulkInsertCnt) throws Exception {
		String bulkInsertQuery = "";
		List<String> bulkInsertQueryList = new ArrayList<>();

		for (int index = 0; index < contentList.length(); index++) {
			JSONObject json = contentList.getJSONObject(index);

			// Put additional key/value to JSONObject
			if(addtionalFieldMap != null) {
				for(Object key : addtionalFieldMap.keySet()) {
					json.put(key.toString(), addtionalFieldMap.get(key).toString());
				}
			}
			
			// JSONObject to VO
			Gson gson = new Gson();
			vo = gson.fromJson(json.toString(), vo.getClass());
			bulkInsertQuery += this.getAllValues(vo);
			
			if (index > 0 && (index+1) % bulkInsertCnt == 0) {
				bulkInsertQueryList.add(bulkInsertQuery);
				bulkInsertQuery = "";
			} else {
				if (index != contentList.length() - 1)
					bulkInsertQuery += ", ";
				else{
					bulkInsertQueryList.add(bulkInsertQuery);
				}
			}
		}

		return bulkInsertQueryList;
	}
	
	private String getAllValues(Object vo) {
    	String toString = "(";

    	int length = vo.getClass().getDeclaredFields().length;
		for(int index = 0; index < length; index++) {	
			Field field = vo.getClass().getDeclaredFields()[index];
			field.setAccessible(true);
			Object value;
			try {
				value = field.get(vo);
				if(index != length - 1)
					toString += value == null ? value + "," : "'" + value.toString().replace("'", "").replace("',","") + "',";
				else
					toString += value == null ? value 		: "'" + value.toString().replace("'", "").replace("'","") + "'";
				
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		toString += ")";
		return toString;
    }
}
