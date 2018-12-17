package cn.touna.json2csv.markdown.model;

import java.util.LinkedList;
import java.util.List;

public class MdElement {

    private List<MdElement> childs = new LinkedList<>();
    private MdElement parent;

    public void setChilds(List<MdElement> childs) {
        this.childs = childs;
    }

    public List<MdElement> getChilds() {
        return childs;
    }

    public void addChild(MdElement element) {
        element.parent = element;
        childs.add(element);
    }

    public MdElement getParent() {
        return parent;
    }

    public String getElementName() {
        return getClass().getSimpleName();
    }
}
