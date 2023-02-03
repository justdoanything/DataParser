package org.dataparser.parser.template;

public class TextFileTemplate extends CommonTemplate {
    private String splitter = "\t";
    private int startWithLine = 0;

    public TextFileTemplate(FileTemplateBuilder builder) {
        this.readFilePath = builder.readFilePath;
        this.writeFilePath = builder.writeFilePath;
        this.isWriteFile = builder.isWriteFile;
        this.isOpenFile = builder.isOpenFile;
        this.isGetString = builder.isGetString;
        this.splitter = builder.splitter;
        this.startWithLine = builder.startWithLine;
    }

    public static class FileTemplateBuilder extends CommonTemplate {
        private String splitter;
        private int startWithLine;

        public FileTemplateBuilder(String readFilePath) {
            this.readFilePath = readFilePath;
        }

        public FileTemplateBuilder splitter(String  splitter) {
            this.splitter = splitter;
            return this;
        }

        public FileTemplateBuilder startWithLine(int startWithLine) {
            this.startWithLine = startWithLine;
            return this;
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

        public TextFileTemplate build() {
            return new TextFileTemplate(this);
        }
    }
}
