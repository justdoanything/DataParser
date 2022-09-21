package org.dataparser.parser;

import java.util.List;

import org.json.JSONArray;

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
public interface ObjectToInsertQueryInterface {
  public void addAddtionalFieldMap(String key, String value);
  public List<String> getQuery(List<Object> listObj) throws Exception;
  public List<String> getQuery(Object obj, JSONArray jsonArray) throws Exception;
}
