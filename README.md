### Info

This is a port of the [TestNG data providers](https://github.com/sergueik/testng-dataproviders) to [JUnitParams](https://github.com/Pragmatists/JUnitParams) - TestNg-style `JUnitParamsRunner`,`ParametersProvider` and `BufferedReaderDataMapper` classes.

### Usage

* Create the Excel 2003, Excel 2007 or Open Office Spreadsheet with test parameters.
* Annotate the test methods in the following way:
```java
@Test
@ExcelParameters(filepath = "classpath:data_2007.xlsx", sheetName = "", type = "Excel 2007")
public void loadParamsFromEmbeddedExcel2007(String keyword, double count) {
	assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
	assertThat((int) count).isGreaterThan(0);
}
```
or 
```java
@Test
@ExcelParameters(filepath = "file:src/test/resources/data_2003.xls", sheetName = "", type = "Excel 2003")
public void loadParamsFromFileExcel2003(String keyword, double count) {
	assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
	assertThat((int) count).isGreaterThan(0);
}
```

You will need to provide your own implementation of 
```java
public static class ExcelParametersProvider implements ParametersProvider<ExcelParameters>
```
Note: the generic column type support is a W.I.P.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
