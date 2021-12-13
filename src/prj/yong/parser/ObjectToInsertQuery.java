package prj.yong.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class ObjectToInsertQuery {

	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> parse() throws Exception {

		this.printBulkInsertQuery(null,null,null,0);
		return null;
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
	private List<String> printBulkInsertQuery(Object vo, Map additional, JSONArray contentList, int bulkInsertCnt) throws Exception {
		String bulkInsertQuery = "";
		List<String> bulkInsertQueryList = new ArrayList<>();

		for (int index = 0; index < contentList.length(); index++) {
			JSONObject json = contentList.getJSONObject(index);

			// Put additional key/value to JSONObject
			if(additional != null) {
				for(Object key : additional.keySet()) {
					json.put(key.toString(), additional.get(key).toString());
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
					toString += value == null ? value + "," : "'" + value.toString().replace("',","") + "',";
				else
					toString += value == null ? value 		: "'" + value.toString().replace("'","") + "'";
				
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		toString += ")";
		return toString;
    }
}