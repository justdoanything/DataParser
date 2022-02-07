DataParser
===
## Project Info
This program parses simple data to make your job easier.\
You can run this after importing with jar file or refer to the code as it is.

It will be registered in maven dependency.

## Unit From
https://github.com/justdoanything/dataparser_old

## Sample Type Of Classes 
- #### TypeConverter.java
  This class converts an object that has setter/getter to map.
  - (1) Map → Object (VO)
  - (2) Object (VO) → Map

---
- #### AttributeToFile.java
  This class reads a file and makes table structure data like Excel.
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
---
- #### FileToInsertQuery.java
  This class reads a file and makes insert query format like below.
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
---
- #### JsonToFile.java
  - ###### 🔰 Input
  - ###### 🔰 Output
---
- #### FileToJson.java
  - ###### 🔰 Input
  - ###### 🔰 Output
---
- #### CheckingJson.java
  - ###### 🔰 Input
  - ###### 🔰 Output
---
- #### ObjectToInsertQuery.java
  This class creates a bulk insert query using an object with local variables.  This is a old way to convert a value object to insert query but you can use if you like old fashion :P
  - ###### 🔰 Input
	```java
	// input parameter is List<sampleVO> of size 3
	public class sampleVO {
		private String value1;
		private String value2;
		private String value3;
	}
	```
  - ###### 🔰 Output
  	```sql
	INSERT INTO test_table (value1, value2, value3) VALUES
	('test1', 'test2', '111'),
	('test1', 'test2', '111'),
	('test1', 'test2', '111');
	```
