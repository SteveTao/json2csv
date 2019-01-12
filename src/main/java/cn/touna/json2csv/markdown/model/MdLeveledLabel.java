package cn.touna.json2csv.markdown.model;

public class MdLeveledLabel extends MdElement {
    private int level;
    private String title;

    MdLeveledLabel(int level, String title) {
        this.level = level;
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
