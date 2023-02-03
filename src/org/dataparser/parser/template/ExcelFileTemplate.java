package org.dataparser.parser.template;

public class ExcelFileTemplate extends CommonTemplate {
    
    public ExcelFileTemplate(FileTemplateBuilder builder) {
        this.readFilePath = builder.readFilePath;
        this.writeFilePath = builder.writeFilePath;
        this.isWriteFile = builder.isWriteFile;
        this.isOpenFile = builder.isOpenFile;
        this.isGetString = builder.isGetString;
    }

    public static class FileTemplateBuilder extends CommonTemplate {
        public FileTemplateBuilder(String readFilePath) {
            this.readFilePath = readFilePath;
        }

        public FileTemplateBuilder writeFilePath(String writeFilePath) {
            this.writeFilePath = writeFilePath;
            return this;
        }

        public FileTemplateBuilder isWriteFile(boolean isWriteFile) {
            this.isWriteFile = isWriteFile;
            return this;
        }

        public FileTemplateBuilder isOpenFile(boolean isOpenFile) {
            this.isOpenFile = isOpenFile;
            return this;
        }

        public ExcelFileTemplate build() {
            return new ExcelFileTemplate(this);
        }
    }
}
