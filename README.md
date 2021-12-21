Unit from
https://github.com/justdoanything/WatchDB
https://github.com/justdoanything/dataparser
https://github.com/justdoanything/EnglishWordExam

```
1️⃣ TypeConverter
  (1) Map → Object (VO)
  (2) Object (VO) → Map
```

```
2️⃣ AttributeToFile
	/******************************************************
	 * 
	 * This class read a file and makes table structure data like Excel.
	 * 
	 * [ Input ]
	 * Name  | Attribute Name | Attribute Value
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV	 | Size		  | 65 inch
	 * TV	 | Company	  | LG
	 * TV	 | Quality	  | HIGH
	 * Audio | Size		  | 32
	 * Audio | Company	  | Apple
	 * Audio | Channel	  | Dual
	 * 
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * 
	 * [ Output ]
	 * Name	  | Size	| Company	| Quality	| Channel
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV	  | 65 inch	| LG		| HIGH		|
	 * Audio  | 32		| Apple		|		| Dual
	 *
	 ******************************************************/	
```

```
3️⃣ FileToInsertQuery
	/******************************************************
	 * 
	 * This class read a file and makes insert query format like below.
	 * 
	 * [ Input ]
	 * Name	  | Size	| Company	| Quality	| Channel
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV     | 65 inch	| LG		| HIGH		|
	 * Audio  | 32		| Apple		|		| Dual
	 *
	 * 
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * 
	 * [ Output ]
	 * 1. Bulk Insert Type
	 * INSERT INTO {tableName} ( Name, Size, Company, Quality, Channel )
	 * VALUES
	 * ('TV', '65 inch', 'LG', 'HIGH' null)
	 * ,('Audio', '32', 'Apple', null, 'Dual');
	 * 
	 * 2. Non-Bulk Insert Type
	 * INSERT INTO {tableName} ( Name, Size, Company, Quality, Channel ) VALUES ('TV', '65 inch', 'LG', 'HIGH' null);
	 * INSERT INTO {tableName} ( Name, Size, Company, Quality, Channel ) VALUES ('Audio', '32', 'Apple', null, 'Dual');; 
	 *
	 ******************************************************/
```
```
4️⃣ JsonToFile
```
```
5️⃣ FileToJson
```
```
6️⃣ CheckingJson
```
```
7️⃣ ObjectToInsertQuery
	/******************************************************
	 * 
	 * This class creates an bulk insert query using an Object with local variables.
	 * This is useful for converting VO Object and DTO Object to insert query.
	 * 
	 * public class sampleVO {
	 * 	private String value1;
	 * 	private String value2;
	 *  	private int value3;
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
```