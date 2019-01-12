package cn.touna.json2csv.utils;

/**
 * 将驼峰式改成小写下划线，例如getBookByID会转为get_book_by_id
 */
public class LittleUnderlineNameStrategy implements NameStrategy {

    public String getName(String src) {
        char[] ch = src.toCharArray();
        boolean preIsUpperCase = false;
        char[] out = new char[(int) (ch.length * 1.5)];
        int offset = 0;
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] >= 'A' && ch[i] <= 'Z') {
                //增加l1wwdcn_TNumsCon_cf
                if (!preIsUpperCase && i > 0 && ch[i - 1] != '_') {
                    out[offset++] = '_';
                }
                out[offset++] = (char) (ch[i] - 'A' + 'a');
                preIsUpperCase = true;
            } else {
                preIsUpperCase = false;
                out[offset++] = ch[i];
            }
        }
        return new String(out, 0, offset);
    }

    public static void main(String[] args) {
        LittleUnderlineNameStrategy littleUnderLineNameStrategy = new LittleUnderlineNameStrategy();
        System.out.println(littleUnderLineNameStrategy.getName("getNameByID"));
        System.out.println(littleUnderLineNameStrategy.getName("nDnD"));
        System.out.println(littleUnderLineNameStrategy.getName("DDDD"));
        System.out.println(littleUnderLineNameStrategy.getName("l1wwdcn_TNumsCon_cf"));
    }
}
