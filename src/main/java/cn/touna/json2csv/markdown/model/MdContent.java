package cn.touna.json2csv.markdown.model;

public class MdContent extends MdElement {

    private StringBuilder sb = new StringBuilder();

    public String getContent() {
        return sb.toString();
    }

    public void appendLine(String value) {
        sb.append(value).append("\r\n");
    }
}
