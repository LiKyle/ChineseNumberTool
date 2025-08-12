package tw.klab.utils;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ChineseNumberToolTest {

    @Test
    void parseHandlesYiAndNegative() {
        String input = "身價-一億零五萬";
        String expected = "身價-100050000";
        assertEquals(expected, ChineseNumberTool.parse(input));
    }

    @Test
    void parseHandlesYiAndChineseNegative() {
        String input = "身價負一億零五萬";
        String expected = "身價-100050000";
        assertEquals(expected, ChineseNumberTool.parse(input));
    }

    @Test
    void parseHandlesDecimal() {
        String input = "利潤-三點五";
        String expected = "利潤-3.5";
        assertEquals(expected, ChineseNumberTool.parse(input));
    }

    @Test
    void parseHandlesChineseNegativeDecimal() {
        String input = "利潤負三點五";
        String expected = "利潤-3.5";
        assertEquals(expected, ChineseNumberTool.parse(input));
    }

    @Test
    void parseHandlesLargeUnitsWithoutRemultiplying() {
        String input = "五億七千萬零七十";
        String expected = "570000070";
        assertEquals(expected, ChineseNumberTool.parse(input));
    }

    @Test
    void chineseNumeralToArabicReturnsNegative() {
        Optional<Integer> value = ChineseNumberTool.chineseNumeralToArabic("-一億零三萬");
        assertTrue(value.isPresent());
        assertEquals(-100030000, value.get().intValue());
    }

    @Test
    void chineseNumeralToArabicReturnsChineseNegative() {
        Optional<Integer> value = ChineseNumberTool.chineseNumeralToArabic("負一億零三萬");
        assertTrue(value.isPresent());
        assertEquals(-100030000, value.get().intValue());
    }

    @Test
    void chineseCharToArabicHandlesChineseNegative() {
        assertEquals("-5", ChineseNumberTool.chineseCharToArabic("負五"));
    }
}
