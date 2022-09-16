package prj.yong.converter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeConverter {
	
	/*
	 * VO -> Map
	 */
	public static Map<String, Object> convertObjectToMap(Object obj) throws Exception {
		if(obj == null) {
			return Collections.emptyMap();
		}
		
		Map<String, Object> convertMap = new HashMap<>();
		Field[] fileds = obj.getClass().getDeclaredFields();
		for(Field field : fileds) {
			field.setAccessible(true);
			convertMap.put(field.getName(), field.get(obj));
		}
		return convertMap;
	}
	
	/*
	 * Map -> VO
	 */
	public static <T> T convertMapToObject(Map<String, Object> map, Class<T> type) throws Exception {
		if(type == null || map == null || map.isEmpty()) {
			throw new NullPointerException("Parameter must be not null");
		}
		
		T instance = type.getConstructor().newInstance();

		for(Map.Entry<String, Object> entrySet : map.entrySet()) {
			Field[] fields = type.getDeclaredFields();
			
			for(Field field : fields) {
				field.setAccessible(true);
				
				String fieldName = field.getName();
				
				boolean isSameType = entrySet.getValue().getClass().equals(field.getType());
				boolean isSameName = entrySet.getKey().equals(fieldName);
				
				if(isSameType && isSameName) {
					field.set(instance, map.get(fieldName));
				}
			}
		}
		return instance;
	}
	
	/*
	 * List<VO> -> Map<String, Object>
	 */
	public static List<Map<String, Object>> convertListObjectToListMap(List<?> list) throws Exception {
		if(list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<Map<String, Object>> convertList = new ArrayList<>();
		
		for(Object obj : list) {
			convertList.add(convertObjectToMap(obj));
		}
		return convertList;
	}
	
	/*
	 * Map<String, Object> -> List<VO>
	 */
	public static <T> List<T> convertListMapToListObject(List<Map<String, Object>> list, Class<T> type) throws Exception {
		if(type == null || list == null || list.isEmpty()) {
			throw new NullPointerException("Parameter must be not null");
		}
		
		List<T> convertList = new ArrayList<>();
		
		for(Map<String, Object> map : list) {
			convertList.add(convertMapToObject(map, type));
		}
		return convertList;
	}
}
