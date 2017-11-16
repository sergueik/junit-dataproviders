### Info

This is a port of the [TestNG data providers](https://github.com/sergueik/testng-dataproviders) to [JUnitParams](https://github.com/Pragmatists/JUnitParams) - TestNg-style `JUnitParamsRunner`,`ParametersProvider` and `BufferedReaderDataMapper` classes.

### Usage

* Create the Excel 2003, Excel 2007 or Open Office Spreadsheet with test parameters.
* Annotate the test methods in the following way:
```java
@Test
@ExcelParameters(filepath = "classpath:data_2007.xlsx", sheetName = "", type = "Excel 2007")
public void loadParamsFromEmbeddedExcel2007(double rowNum, String keyword, double count) {
	assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
	assertThat((int) count).isGreaterThan(0);
}
```
or 
```java
@Test
@ExcelParameters(filepath = "file:src/test/resources/data_2003.xls", sheetName = "", type = "Excel 2003")
public void loadParamsFromFileExcel2003(double rownum, String keyword, double count) {
	assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
	assertThat((int) count).isGreaterThan(0);
}
```
or 
```java
@Test
@ExcelParameters(filepath = "file:src/test/resources/data.ods", sheetName = "", type = "OpenOffice Spreadsheet")
public void loadParamsFromFileOpenOfficeSpreadsheel(double rowNum,
    String keyword, double count) {
  assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
  assertThat((int) count).isGreaterThan(0);
}

```
The provided implementation of 
```java
public static class ExcelParametersProvider implements ParametersProvider<ExcelParameters>
```
reads all columns from the Excel 2007, Excel 2003 or Open Office spreadhsheet and executes the test for every row of data. 
The test developer is responsible for matching the test method argument types and the column data types.
NOTE: attributes for sheet and column selection and for converting every column type to `String` is a W.I.P.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
