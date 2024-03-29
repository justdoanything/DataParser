DataParser
===
## Project Information
This program parses simple data to make your job easier.\
You can run this after importing with jar file or refer to the code as it is.

It will be registered in maven repository.

---

## Logs
I studied [java design pattern](https://github.com/justdoanything/self-study/blob/main/WIS/03%20ApplicationModernization.md#5%EF%B8%8F⃣-java-design-pattern) and tried to apply into this project.

First of all, I need to divide which field and method should be common for all classes. I figured out it by input, output type.\
After that, I thought about how to implement these common fields/methods.

- Builder Pattern
  - There are many fields in each class, and they are different.
  - This pattern makes it easier to create classes.
  - Required fields are defined in constructor.
- Template Pattern
  - All classes have similar fields and methods.
  - Common fields and methods are separated into template class, and what they do is written in each class.
- Factory Pattern
  - To create a lot of classes into one class. 

---

## What Classes Do 
- ### TypeConverter.java
  - This class converts an object that has setter/getter to map (POJO) and an object to map.
  - You can use methods as below. All methods are static.
    - `Map<String, Object> convertObjectToMap(Object obj);`
    - `<T> T convertMapToObject(Map<String, Object> map, Class<T> type);`
    - `List<Map<String, Object>> convertListObjectToListMap(List<?> list);`
    - `<T> List<T> convertListMapToListObject(List<Map<String, Object>> list, Class<T> type);`

- ### AttributeToFile.java
  - You can use methods as below. 
    - `String parse();`
    - `void addCodeMap(String name, String code, String value);`
    - You can make this class by static mathod or builder class.
  - This class reads a file and makes table structure data like Excel.
    - ###### 🔰 Input
      Name | Attribute Name | Attribute Value
      ---|---|---
      TV | Size | 65 inch
      TV | Company | LG
      TV | Quality | HIGH
      Audio | Size | 32
      Audio | Company | Apple
      Audio | Channel	| Dual
    - ###### 🔰 Output
      Name | Size	| Company | Quality	| Channel
      ---|---|---|---|---
      TV | 65 inch | LG | HIGH |
      Audio | 32	| Apple	| | Dual
 
- ### FileToInsertQuery.java
  - You can use methods as below.
    - `String parse();`
    - `void addCodeMap(String name, String code, String value);`
    - You can make this class by static mathod or builder class.
  - This class reads a file and makes insert query format like below.
    - ###### 🔰 Input
      Name | Size | Company | Quality | Channel
      ---|---|---|---|---
       TV | 65 inch | LG | HIGH |
       Audio | 32	| Apple	| | Dual
    - ###### 🔰 Output
      1. Bulk Insert Type
      ```sql
      INSERT INTO test_table ( Name, Size, Company, Quality, Channel ) VALUES
      ('TV', '65 inch', 'LG', 'HIGH' null)
      ,('Audio', '32', 'Apple', null, 'Dual'); 
      ```
      2. Non-Bulk Insert Type
      ```sql
      INSERT INTO test_table ( Name, Size, Company, Quality, Channel ) VALUES ('TV', '65 inch', 'LG', 'HIGH' null);
      INSERT INTO test_table ( Name, Size, Company, Quality, Channel ) VALUES ('Audio', '32', 'Apple', null, 'Dual');
      ```

- ### ObjectToInsertQuery.java
  This class creates a bulk insert query using an object with local variables.  This is a old way to convert a value object to insert query but you can use if you like old fashion :P
  - ###### 🔰 Input
	```java
	// input parameter is List<sampleVO> of size 3
	public class sampleVO {
		private String name;
		private String age;
		private String country;
	}
	```
  - ###### 🔰 Output
  	```sql
	INSERT INTO test_table (name, age, country) VALUES
	('Kally', '20', 'Korea'),
	('John', '30', 'US'),
	('Dany', '40', 'France');
	```

---

## How To Use Classes

- ### AttributeToFile.java
  ```java
  public class AttributeToFileTest {
    public static void main(String[] args) {
        try {
            AttributeToFile atf = AttributeToFile.builder("src/test/resources/question/ATF")
                    .splitter("|")
                    .startWithLine(0)
                    .isWriteFile(true)
                    .isOpenFile(true)
                    .writeFilePath("src/test/resources/question/ATF_2023.txt")
                    .isGetString(true)
                    .build();
            atf.addCodeMap("name", "code", "value");
            System.out.println(atf.parse());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
  }
  ```
  ```java
  public class AttributeToFileTest {
    public static void main(String[] args) {
        try {
            AttributeToFile atfBuilder = new AttributeToFileBuilder("src/test/resources/question/ATF.xlsx")
                    .splitter("\t")
                    .startWithLine(0)
                    .isWriteFile(true)
                    .isOpenFile(false)
                    .writeFilePath("src/test/resources/question/ATF_2023.xlsx")
                    .isGetString(true)
                    .build();
            atfBuilder.addCodeMap("name", "code", "value");
            System.out.println(atfBuilder.parse());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
  }

  ```

- ### FileToInsertQuery.java
  ```java
  public class FileToInsertQueryTest {
    public static void main(String[] args) {
      try {
        FileToInsertQuery ftiq = FileToInsertQuery.builder("src/test/resources/question/FTIQ", "TEMP_TABLE")
                .splitter("|")
                .startWithLine(0)
                .isWriteFile(true)
                .isOpenFile(true)
                .writeFilePath("src/test/resources/question/FTIQ_2023")
                .isGetString(true)
                .isBulkInsert(true)
                .bulkInsertCnt(100)
                .build();
        System.out.println(ftiq.parse());
      }catch (Exception e) {
        System.out.println("Error : " + e.getMessage());
      }
    }
  }
  ```
  ```java
  public class FileToInsertQueryTest {
    public static void main(String[] args) {
      try {
        FileToInsertQuery ftiqBuilder = new FileToInsertQueryBuilder("src/test/resources/question/FTIQ.xlsx", "TEMP_TABLE")
                .splitter("\t")
                .startWithLine(0)
                .isWriteFile(true)
                .isOpenFile(false)
                .writeFilePath("src/test/resources/question/FTIQ_2023.xlsx")
                .isGetString(true)
                .isBulkInsert(false)
                .build();
        System.out.println(ftiqBuilder.parse());
      }catch (Exception e) {
        System.out.println("Error : " + e.getMessage());
      }
    }
  }
  ```

---

## Unit From
https://github.com/justdoanything/dataparser_old
