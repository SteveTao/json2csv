package cn.touna.json2csv.json;


import java.util.List;

public abstract class JSONPathFunc {

    private String name;
    private String pattern;

    public JSONPathFunc(String name, String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public abstract Object func(Object value, List<String> params);
}
