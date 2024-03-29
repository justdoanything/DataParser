package data;

import data.exception.ParseException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeConverter {

    public static Map<String, Object> convertObjectToMap(Object obj) {
        try {
            if (obj == null) {
                return Collections.emptyMap();
            }

            Map<String, Object> convertMap = new HashMap<>();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                convertMap.put(field.getName(), field.get(obj));
            }
            return convertMap;
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    public static <T> T convertMapToObject(Map<String, Object> map, Class<T> type) {
        try {
            if (type == null || map == null || map.isEmpty()) {
                throw new NullPointerException("Parameter must be not null");
            }

            T instance = type.getConstructor().newInstance();

            for (Map.Entry<String, Object> entrySet : map.entrySet()) {
                Field[] fields = type.getDeclaredFields();

                for (Field field : fields) {
                    field.setAccessible(true);

                    String fieldName = field.getName();

                    boolean isSameType = entrySet.getValue().getClass().equals(field.getType());
                    boolean isSameName = entrySet.getKey().equals(fieldName);

                    if (isSameType && isSameName) {
                        field.set(instance, map.get(fieldName));
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    public static List<Map<String, Object>> convertListObjectToListMap(List<?> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> convertList = new ArrayList<>();

        for (Object obj : list) {
            convertList.add(convertObjectToMap(obj));
        }
        return convertList;
    }

    public static <T> List<T> convertListMapToListObject(List<Map<String, Object>> list, Class<T> type) {
        if (type == null || list == null || list.isEmpty()) {
            throw new NullPointerException("Parameter must be not null");
        }

        List<T> convertList = new ArrayList<>();

        for (Map<String, Object> map : list) {
            convertList.add(convertMapToObject(map, type));
        }
        return convertList;
    }
}
