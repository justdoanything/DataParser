package org.dataparser.test;

import org.dataparser.parser.builder.AttributeToFileBuilder;
import org.dataparser.parser.impl.AttributeToFile;
import org.dataparser.parser.template.ExcelFileTemplate;

public class AttributeToFileTest {

  public static void main(String[] args) throws Exception {
    AttributeToFile test = new AttributeToFileBuilder("").writeFilePath("z").build();
  }
}
