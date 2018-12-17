package cn.touna.json2csv.markdown.model;

public class MdDocument extends MdElement {

    public MdHeader buildHeader(int level, String title) {
        return new MdHeader(level, title);
    }

    public MdListItem buildListItem(int level, String title) {
        return new MdListItem(level, title);
    }

    public MdSegment buildSegment(String language) {
        return new MdSegment(language);
    }

    public MdText buildText() {
        return new MdText();
    }
}
