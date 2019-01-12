package cn.touna.json2csv.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimestampFunc  extends JSONPathFunc  {

    private static final Logger logger = LoggerFactory.getLogger(TimestampFunc.class);

    public TimestampFunc(){
        super("timestamp(pattern)",PatternUtil.getFuncPattern("timestamp",1));
    }

    private Map<String, SimpleDateFormat> sdfMap = new HashMap<>();

    @Override
    public Object func(Object value, List<String> params) {
        if(value == null)
            return null;

        if(value instanceof Date){
            return ((Date)value).getTime();
        }

        String str = String.valueOf(value);
        String pattern = params.get(0);
        SimpleDateFormat sdf  = null;
        if(sdfMap.containsKey(pattern)){
            sdf = sdfMap.get(pattern);
        }else{
            sdf = new SimpleDateFormat(pattern);
            sdfMap.put(pattern,sdf);
        }

        try {
            Date date = sdf.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
