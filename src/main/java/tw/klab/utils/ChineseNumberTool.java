package tw.klab.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 將中文數字轉為阿拉伯數字，或是整數型態的工具。
 * @author Kyle
 * @version 1
 */
public class ChineseNumberTool {

    private static final String regexStr = "(十?(?:[零一二兩三四五六七八九][十百千萬]?萬?)+點?[零一二三四五六七八九]*)";
    private static final Pattern pattern = Pattern.compile(regexStr);
    private static final Map<Character, Character> nextUnit = Map.of(
        '萬', '千',
        '千', '百',
        '百', '十');

    private static final List<Character> chineseNumerals = List.of(
        '零', '一', '二', '三', '四', '五', '六', '七', '八', '九');

    /**
     * 找出字串中所有口語表達的中文數字。例如輸入：
     * <code>"序號十八號，身高一百零五點七二公分，重量三千兩百五十七點三九公斤，身價五千一百萬。"}</code>，
     * 會返回<code>["十八", "一百零五點七二", "三千兩百五十七點三九", "五千一百萬"]</code>。
     * @param str
     * @return
     */
    public static List<String> find(String str) {
        var mtc = pattern.matcher(str);
        var rst = new ArrayList<String>();
        while (mtc.find()) {
            rst.add(mtc.group());
        }
        return rst;
    }

    /**
     * 將字串中口語表達的中文數字全部轉為阿拉伯數字。例如輸入：
     * <code>"序號十八號，身高一百零五點七二公分，重量三千兩百五十七點三九公斤，身價五千一百萬"</code>，
     * 將會返回<code>"序號18號，身高105.72公分，重量3257.39公斤，身價51000000"</code>。
     * 最高只支援到千萬，無法處理億的單位。
     * <br><br>
     * 如果不需要處理口語表達，只需要一對一的將中文數字轉為阿拉伯數字，可以使用 {@link #chineseCharToArabic(String)}。
     * @param str
     * @return
     */
    public static String parse(String str) {
        var mtc = pattern.matcher(str);
        var sb = new StringBuilder();
        while (mtc.find()) {
            var word = mtc.group();
            var nums = word.split("點");
            var arabicOpt = chineseNumeralToArabic(nums[0]);
            if (nums.length >= 2) {
                var dot = chineseCharToArabic(nums[1]);
                if (dot.length() > 0) {
                    var t = String.format("%d.%s", arabicOpt.get(), dot);
                    mtc.appendReplacement(sb, t);
                    continue;
                }
            }
            mtc.appendReplacement(sb, arabicOpt.map(v -> v.toString()).orElse(word));
        }
        mtc.appendTail(sb);
        return sb.toString();
    }

    /**
     * 中文字元轉為阿拉伯字元，例如輸入 "你好九五二七"，返回 "你好9527"。
     * @param str 中文數字的字串
     * @return 阿拉伯數字的字串
     */
    public static String chineseCharToArabic(String str) {
        var sb = new StringBuilder();
        for (var c : str.toCharArray()) {
            var i = chineseNumerals.indexOf(c);
            if (i >= 0) {
                sb.append(i);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 口語表達的中文數字轉為整數型態，例如 "一百零五" 轉為 "105"。
     * 如果輸入 "體重一百零五" 會無法轉換，返回 {@link Optional#empty()}。
     * 需要上述的轉換方式可以使用 {@link #parse(String)}，還可處理到小數點。
     * @param str 口語表達的中文數字，不能包含其他內容
     * @return 如果轉換成功，返回阿拉伯數字
     */
    public static Optional<Integer> chineseNumeralToArabic(String str) {
        if (str.startsWith("十")) {
            str = "一" + str;
        }
        var num = 0;
        var reg = -1;
        char lastUnit = 0;
        for (char c : str.toCharArray()) {
            int tmp = change(c);
            if (tmp == 0) {
                lastUnit = 0;
                continue;
            } else if (tmp >= 0 && reg < 0) {
                reg = tmp;
            } else if (reg >= 0) {
                int acc = times(c, reg);
                if (acc == -1) {
                    return Optional.empty();
                }
                lastUnit = c;
                num += acc;
                reg = -1;
            } else if (c == '萬' || c == '億') {
                num = times(c, num);
            } else {
                return Optional.empty();
            }
        }
        if (reg > 0) {
            if (lastUnit != 0 && nextUnit.containsKey(lastUnit)) {
                reg = times(nextUnit.get(lastUnit), reg);
            }
            num += reg;
        }
        return Optional.of(num);
    }

    private static int times(char c, int num) {
        switch (c) {
            case '十':
                return num * 10;
            case '百':
                return num * 100;
            case '千':
                return num * 1000;
            case '萬':
                return num * 10000;
            case '億':
                return num * 100000000;
            default:
                return -1;
        }
    }

    private static int change(char c) {
        switch (c) {
            case '零':
                return 0;
            case '一':
            case '壹':
                return 1;
            case '二':
            case '貳':
            case '兩':
                return 2;
            case '三':
            case '參':
                return 3;
            case '四':
            case '肆':
                return 4;
            case '五':
            case '伍':
                return 5;
            case '六':
            case '陸':
                return 6;
            case '七':
            case '柒':
                return 7;
            case '八':
            case '捌':
                return 8;
            case '九':
            case '玖':
                return 9;
            default:
                return -1;
        }
    }
}
