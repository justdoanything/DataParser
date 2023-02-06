package org.dataparser.test;

import java.util.ArrayList;
import java.util.List;

import org.dataparser.parser.ObjectToInsertQuery;

public class ObjectToInsertQueryTest {

  public static void main(String[] args) throws Exception {
    SampleVO sample1 = new SampleVO("value1", "value2", 1);
    SampleVO sample2 = new SampleVO("value3", "value4", 2);
    SampleVO sample3 = new SampleVO("value5", "value6", 3);

    List<Object> listObj = new ArrayList<>();
    listObj.add(sample1);
    listObj.add(sample2);
    listObj.add(sample3);

    ObjectToInsertQuery otiq = ObjectToInsertQuery.builder()
                                                    .isWriteFile(false)
                                                    .writeFilePath("")
                                                    .tableName("temp")
                                                    .isGetString(true)
                                                    .build();

    otiq.getQuery(listObj).stream().forEach(System.out::println);
  }
}
