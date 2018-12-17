package cn.touna.json2csv.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateFormatFunc  extends JSONPathFunc {

    private static final Logger logger = LoggerFactory.getLogger(DateFormatFunc.class);

    public DateFormatFunc(){
        super("dateFormat(formateStr)",PatternUtil.getFuncPattern("dateFormat",1));
    }

    private Map<String,SimpleDateFormat> sdfMap = new HashMap<>();

    @Override
    public Object func(Object value, List<String> params) {

        Date date = null;
        if(value instanceof Date){
            date = (Date)value;
        }else{
            String str = String.valueOf(value);
            if(StringUtils.isEmpty(str)){
                return null;
            }
            try{
                long l = Long.valueOf(str);
                if(l == 0)
                    return null;
                date = new Date(l);
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }

        }

        String pattern = params.get(0);
        SimpleDateFormat sdf = null;
        if(!sdfMap.containsKey(pattern)){
            sdf = new SimpleDateFormat(pattern);
            sdfMap.put(pattern,sdf);
        }else{
            sdf = sdfMap.get(pattern);
        }
        return sdf.format(date);
    }

    public static void main(String[] args) {
        DateFormatFunc func = new DateFormatFunc();
        Object value = func.func(new Date(), Arrays.asList("yyyy-MM-dd HH:mm:ss"));
        System.out.println(value);
    }
}
