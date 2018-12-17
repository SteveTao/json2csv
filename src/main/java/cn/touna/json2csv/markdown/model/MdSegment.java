package cn.touna.json2csv.markdown.model;

public class MdSegment extends MdContent {

    private String language;

    MdSegment(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
