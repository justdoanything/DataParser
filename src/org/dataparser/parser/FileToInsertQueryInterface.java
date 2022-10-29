package org.dataparser.parser;

import java.io.IOException;
import java.time.format.DateTimeParseException;



/******************************************************
	 * 
	 * This class read a file and makes insert query format like below.
	 * 
	 * [ Input ]
	 * Name	 | Size		| Company	| Quality	| Channel
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV	 | 65 inch	| LG		| HIGH		|
	 * Audio | 32		| Apple		|			| Dual
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
public interface FileToInsertQueryInterface {
	public String parse() throws Exception, NullPointerException, StringIndexOutOfBoundsException, DateTimeParseException, IOException;
  
}
