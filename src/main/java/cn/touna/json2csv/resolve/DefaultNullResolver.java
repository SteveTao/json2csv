package cn.touna.json2csv.resolve;

public class DefaultNullResolver implements NullResolver {

    @Override
    public boolean isNull(String value) {
        if (value == null || "null".equals(value) || "[]".equals(value) || "{}".equals(value))
            return true;
        return false;
    }
}
