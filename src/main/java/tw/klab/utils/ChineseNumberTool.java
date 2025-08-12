package tw.klab.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 將中文數字轉為阿拉伯數字，或是整數型態的工具。
 * @author Kyle
 * @version 1
 */
public class ChineseNumberTool {

    // 支援到「億」單位並允許可選的負號（- 或「負」）
    private static final String regexStr = "([-負]?十?(?:[零一二兩三四五六七八九][十百千萬億]?億?萬?)+點?[零一二三四五六七八九]*)";
    private static final Pattern pattern = Pattern.compile(regexStr);
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
     * 支援到億的單位並可處理負數。
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
            if (arabicOpt.isPresent() && nums.length >= 2) {
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
            if (c == '負') {
                sb.append('-');
                continue;
            }
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
        boolean negative = false;
        if (str.startsWith("-") || str.startsWith("負")) {
            negative = true;
            str = str.substring(1);
        }

        int result = 0;   // 最終結果
        int section = 0;  // 當前節（萬、億之前的部分）
        int number = 0;   // 當前數字

        for (char c : str.toCharArray()) {
            int digit = change(c);
            if (digit >= 0) {
                number = digit;
                continue;
            }
            switch (c) {
                case '十':
                    if (number == 0) {
                        number = 1;
                    }
                    section += number * 10;
                    number = 0;
                    break;
                case '百':
                    if (number == 0) {
                        number = 1;
                    }
                    section += number * 100;
                    number = 0;
                    break;
                case '千':
                    if (number == 0) {
                        number = 1;
                    }
                    section += number * 1000;
                    number = 0;
                    break;
                case '萬':
                    section += number;
                    result += section * 10000;
                    section = 0;
                    number = 0;
                    break;
                case '億':
                    section += number;
                    result += section * 100000000;
                    section = 0;
                    number = 0;
                    break;
                case '零':
                    // 忽略
                    break;
                default:
                    return Optional.empty();
            }
        }

        section += number;
        result += section;

        return Optional.of(negative ? -result : result);
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
