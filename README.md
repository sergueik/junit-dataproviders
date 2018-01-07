###  JUnit-DataProviders [![BuildStatus](https://travis-ci.org/sergueik/junit-dataproviders.svg?branch=master)](https://https://travis-ci.org/sergueik/junit-dataproviders)

This project exercises the following data providers with [JUnitParams](https://github.com/Pragmatists/JUnitParams):

  * Excel 2003 OLE documents - a.k.a. Horrible SpreadSheet Format [org.apache.poi.hssf.usermodel.*)](http://shanmugavelc.blogspot.com/2011/08/apache-poi-read-excel-for-use-of.html)
  * Excel 2007 OOXML (.xlsx) - a.k.a. XML SpreadSheet Format [org.apache.poi.xssf.usermodel.*](http://howtodoinjava.com/2013/06/19/readingwriting-excel-files-in-java-poi-tutorial/)
  * OpenOffice SpreadSheet (.ods) [example1](http://www.programcreek.com/java-api-examples/index.php?api=org.jopendocument.dom.spreadsheet.Sheet), [example 2](http://half-wit4u.blogspot.com/2011/05/read-openoffice-spreadsheet-ods.html)
  * Custom JSON [org.json.JSON](http://www.docjar.com/docs/api/org/json/JSONObject.html)

### Usage

* Create the Excel 2003, Excel 2007 or Open Office Spreadsheet with test parameters e.g.

| ROWNUM |  SEARCH | COUNT |
|--------|---------|-------|
| 1      | junit   | 100   |

or a json file with the following structure:
```javascript
{
    "test": [{
        "keyword": "junit",
        "count": 101.0
    }, {
        "keyword": "testng",
        "count": 31.0
    }, {
        "keyword": "spock",
        "count": 11.0
    }],
    "other_test": [{
        "keyword": "not used",
        "count": 1.0
    }]
}

```
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

### Maven Central

The snapshot versions are deployed to [https://oss.sonatype.org/content/repositories/snapshots/com/github/sergueik/junitparams/junit_params/](https://oss.sonatype.org/content/repositories/snapshots/com/github/sergueik/junitparams/junit_params/)
Release versions status: [pending](https://issues.sonatype.org/browse/OSSRH-36771?page=com.atlassian.jira.plugin.system.issuetabpanels:all-tabpanel).

To use the snapshot version, add the following to `pom.xml`:
```xml
<dependency>
  <groupId>com.github.sergueik.jprotractor</groupId>
  <artifactId>junit_params</artifactId>
  <version>0.0.7-SNAPSHOT</version>
</dependency>

<repositories>
  <repository>
    <id>ossrh</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```

### See Also

 * Using Excel/Open Office / JSON as [testNG data providers](https://github.com/sergueik/testng-dataproviders)
 * [testng dataProviders](http://testng.org/doc/documentation-main.html#parameters-dataproviders)

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
