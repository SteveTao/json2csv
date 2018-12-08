package cn.touna.json2csv.resolve;

import java.util.HashMap;
import java.util.Map;

public class ResolverConfig {

    public static final String DEFAULT_NULL_STRING = "\\N";
    public static final String DEFAULT_CELL_SEPARATOR = "\t";
    public static final String ROW_SEPARATOR = "\n";
    private NullResolver nullResolver;
    private String nullString;
    private Map<String, EncryptHandlerModel> encryptHandlerModelMap ;
    private TypeConverter typeConverter;
    private String cellSeparator;

    public ResolverConfig() {
        nullResolver = new DefaultNullResolver();
        nullString = DEFAULT_NULL_STRING;
        cellSeparator = DEFAULT_CELL_SEPARATOR;
        encryptHandlerModelMap = new HashMap<>();
    }



    public NullResolver getNullResolver() {
        return nullResolver;
    }

    public void setNullResolver(NullResolver nullResolver) {
        this.nullResolver = nullResolver;
    }

    public String getNullString() {
        return nullString;
    }

    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    public String getCellSeparator() {
        return cellSeparator;
    }

    public void setCellSeparator(String cellSeparator) {
        this.cellSeparator = cellSeparator;
    }

    public void addEncryptHandler(String encryptName, EncryptHandler handler, Object attach) {
        EncryptHandlerModel model = new EncryptHandlerModel();
        model.attach = attach;
        model.handler = handler;
        encryptHandlerModelMap.put(encryptName,model);
    }

    public EncryptHandlerModel getEncryptHandlerModel(String encryptName) {
        return encryptHandlerModelMap.get(encryptName);
    }

    static class EncryptHandlerModel {
        private Object attach;
        private EncryptHandler handler;

        public Object getAttach() {
            return attach;
        }

        public EncryptHandler getHandler() {
            return handler;
        }
    }

}
