package cn.touna.json2csv.model;


import cn.touna.json2csv.json.ItemFilter;
import cn.touna.json2csv.json.JsonFilterExpression;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Json2CsvMapping {

    private String table;
    private JsonFilterExpression filter;
    private ItemFilter itemFilter;
    private String comment;
    private String root;
    private Set<String> optionalRoots;
    /**
     * key = column, value = encrypt_method_name
     */
    private Map<String, String> encryptColumns;

    public ItemFilter getItemFilter() {
        return itemFilter;
    }

    public void setItemFilter(ItemFilter itemFilter) {
        this.itemFilter = itemFilter;
    }

    private List<Json2CsvMappingField> fields;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public JsonFilterExpression getFilter() {
        return filter;
    }

    public void setFilter(JsonFilterExpression filter) {
        this.filter = filter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Map<String, String> getEncryptColumns() {
        return encryptColumns;
    }

    public void setEncryptColumns(Map<String, String> encryptColumns) {
        this.encryptColumns = encryptColumns;
    }

    public List<Json2CsvMappingField> getFields() {
        return fields;
    }

    public void setFields(List<Json2CsvMappingField> fields) {
        this.fields = fields;
    }

    public Set<String> getOptionalRoots() {
        return optionalRoots;
    }

    public void setOptionalRoots(Set<String> optionalRoots) {
        this.optionalRoots = optionalRoots;
    }


    @Override
    public String toString() {
        return "Json2CsvMapping{" +
                "table='" + table + '\'' +
                ", filter=" + filter +
                ", comment='" + comment + '\'' +
                ", root='" + root + '\'' +
                ", encryptColumns=" + encryptColumns +
                ", fields=" + fields +
                '}';
    }
}
