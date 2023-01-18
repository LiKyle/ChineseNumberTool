# 中文數字轉換工具
將中文數字轉為阿拉伯數字，或是整數型態的工具。目前可以處理小數點，但只能處理到千萬，不能處理到億，也不能處理負數。

# 範例

```java
ChineseNumberTool.parse("序號十八號，身高一百零五點七二公分，重量三千兩百五十七點三九公斤，身價五千一百萬。")
// "序號18號，身高105.72公分，重量3257.39公斤，身價51000000。"

ChineseNumberTool.find("序號十八號，身高一百零五點七二公分，重量三千兩百五十七點三九公斤，身價五千一百萬。")
// [十八, 一百零五點七二, 三千兩百五十七點三九, 五千一百萬]

ChineseNumberTool.chineseNumeralToArabic("一百零五").orElse(-1)
// 105

ChineseNumberTool.chineseCharToArabic("你好九五二七")
// "你好9527"
```
