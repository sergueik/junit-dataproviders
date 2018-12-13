###  JUnit-DataProviders [![BuildStatus](https://travis-ci.org/sergueik/junit-dataproviders.svg?branch=master)](https://https://travis-ci.org/sergueik/junit-dataproviders)

This project exercises the following data providers with [JUnitParams](https://github.com/Pragmatists/JUnitParams):

  * Excel 2003 OLE documents - a.k.a. Horrible SpreadSheet Format [org.apache.poi.hssf.usermodel.*)](http://shanmugavelc.blogspot.com/2011/08/apache-poi-read-excel-for-use-of.html)
  * Excel 2007 OOXML (.xlsx) - a.k.a. XML SpreadSheet Format [org.apache.poi.xssf.usermodel.*](http://howtodoinjava.com/2013/06/19/readingwriting-excel-files-in-java-poi-tutorial/)
  * OpenOffice SpreadSheet (.ods) [example1](http://www.programcreek.com/java-api-examples/index.php?api=org.jopendocument.dom.spreadsheet.Sheet), [example 2](http://half-wit4u.blogspot.com/2011/05/read-openoffice-spreadsheet-ods.html)
  * Custom JSON [org.json.JSON](http://www.docjar.com/docs/api/org/json/JSONObject.html)

Note: Unlike core Data Providers in Junit (5?) and TestNg this provider features runtime-flexible data file paths enabling one running the jar
different inputs e.g. for __DEV__ / __TEST__ / __UAT__ environments. See details in __Extra Features__ section below

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
The `ExcelParametersProvider` class will read all columns from the Excel 2007, Excel 2003 or Open Office spreadhsheet and executes the test for every row of data.
The test developer is responsible for matching the test method argument types and the column data types.

To enable debug messages during the data loading, set the `debug` flag with `@ExcelParameters` attribute:
```java
@Test
@ExcelParameters(filepath = "classpath:data_2007.xlsx", sheetName = "", type = "Excel 2007", debug = true)
public void loadParamsFromEmbeddedExcel2007(double rowNum, String keyword,
    double count) {
  dataTest(keyword, count);
}
```

this will show the following:
```shell
0 = A ID
1 = B SEARCH
2 = C COUNT
Skipped the header
Cell Value: "1.0" class java.lang.Double
Cell Value: "junit" class java.lang.String
Cell Value: "104.0" class java.lang.Double
...
Loaded 3 rows
row 0 : [1.0, junit, 104.0]
...
```

NOTE: attributes for column selection and for converting every column type to `String` is *work in progress*.

### Maven Central

The snapshot versions are deployed to [https://oss.sonatype.org/content/repositories/snapshots/com/github/sergueik/junitparams/junit_params/](https://oss.sonatype.org/content/repositories/snapshots/com/github/sergueik/junitparams/junit_params/)
Release versions status: [pending](https://issues.sonatype.org/browse/OSSRH-36771?page=com.atlassian.jira.plugin.system.issuetabpanels:all-tabpanel).

To use the snapshot version, add the following to `pom.xml`:
```xml
<dependency>
  <groupId>com.github.sergueik.junitparams</groupId>
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

### Extra Features

Core TestNG or Junit parameter
annotations
[block](https://stackoverflow.com/questions/16509065/get-rid-of-the-value-for-annotation-attribute-must-be-a-constant-expression-me) one from redefining the dataprovider attributes like data source:

```java
public static final String testDataPath = "file:src/test/resources/data.json";
@Test
	@ExcelParameters(filepath = testDataPath, sheetName = "", type = "OpenOffice Spreadsheet", debug = true)
	public void loadParamsFromFileOpenOfficeSpreadsheetUsingVariable(
			double rowNum, String keyword, double count) {
		dataTest(keyword, count);
	}
```
In the above, the only allowed rhs for the `testDataPath` is `String` or `int` primitive type,
even declaring the same (pseudo-const) data in another class:

```java
public class ParamDataUtils {
	public final static String testDataPath = "file:src/test/resources/data.json";
}
```
```java
public class FileParamsTest {

	// private final String jsonDataPath = ParamDataUtils.param();
	// private final static String testDataPath = ParamDataUtils.testDataPath;
```
will fail to compile:
```sh
Compilation failure:
[ERROR] FileParamsTest.java: element value must be a constant expression
```
so it likely not doable.

To allow this flexibility the data provider class `ExcelParametersProvider` itself has added
support for the environment variable named `TEST_ENVIRONMENT` that, when set, makes
it amend the data filepaths that are specified through the `file://` protocol
and which therefore refer to the system files (not to data embedded in the  jar):
so setting the test data provider to
```java
@Test
	@ExcelParameters(filepath = "file:src/test/resources/data_2007.xlsx", sheetName = "", type = "Excel 2007")
	public void loadParamsFromFileExcel2007(double rowNum, String keyword,
			double count) {
		try {
			dataTest(keyword, count);
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("keyword: %s , cound : %d ", keyword, count));
		}
	}

```
and `TEST_ENVIRONMENT` to `dev` will make it read parameters of the test from `src/test/resources/dev`.

It is implemented directly in the `ExcelParametersProvider` provider in a very basic fashion like shown below:

```java
public class ExcelParametersProvider
		implements ParametersProvider<ExcelParameters> {

	private final static String testEnvironment = (System
			.getenv("TEST_ENVIRONMENT") != null) ? System.getenv("TEST_ENVIRONMENT")
					: "";
```

and take it into account to redefine the inputs during the initialization:

```java
public void initialize(ExcelParameters parametersAnnotation,
			FrameworkMethod frameworkMethod) {
		filepath = parametersAnnotation.filepath();
		type = parametersAnnotation.type();
		sheetName = parametersAnnotation.sheetName();
		protocol = filepath.substring(0, filepath.indexOf(':'));
		filename = filepath.substring(filepath.indexOf(':') + 1);
		debug = parametersAnnotation.debug();
		if (testEnvironment != null && testEnvironment != "") {
			if (protocol.matches("file")) {
				if (debug) {
					System.err.println(String.format("Amending the %s with %s", filename,
							testEnvironment));
				}
			}
			// Inject the directory into the file path
			String updatedFilename = filename.replaceAll("^(.*)/([^/]+)$",
					String.format("$1/%s/$2", testEnvironment));
			filename = updatedFilename;
		}
```

therefore the test
```cmd
copy src\test\resources\data.* src\test\resources\dev\
set  TEST_ENVIRONMENT=dev
mvn test
```
works as expected:

```cmd
Amending the src/test/resources/data.ods with dev
Reading Open Office Spreadsheet: Employee Data
Cell Value: "1.0" class java.lang.Double
Cell Value: "junit" class java.lang.String
Cell Value: "202.0" class java.lang.Double
Cell Value: "2.0" class java.lang.Double
```
One can easily make this behavior optional and move the definition of amendments into the property file (this is work in progress). Similar changes will be soon available to
[testNg-DataProviders](https://github.com/sergueik/testng-dataproviders).

### Note
This project and the [testNg-DataProviders](https://github.com/sergueik/testng-dataproviders) -
have large code overlap for processing spreadsheets and only differ in test methdod annotation details.

### TODO
Support `org.junit.runners.Parameterized` of [Junit](https://github.com/junit-team/junit4/blob/master/src/main/java/org/junit/runners/Parameterized.java). See intro [JUnit - Parameterized Test](https://www.tutorialspoint.com/junit/junit_parameterized_test.htm)

### Apache POI compatibility

  * The default version of the supported Apache POI is 3.17.
  * Project  can be compiled with release of Apache POI 4.0.0 with the help of profile `poi40`.
  * Older versions of the package require minor code refactoring. Note that you may also have to clear the other versions of poi and poi-ooxml jars from maven cache '~/.m2/repository'
  * Creating branches and tags is a work in progress.
### See Also

 * Using Excel,Open Office,JSON as [testNG data providers](https://github.com/sergueik/testng-dataproviders)
 * [testng dataProviders](http://testng.org/doc/documentation-main.html#parameters-dataproviders)
 * [TNG/junit-dataprovider](https://github.com/TNG/junit-dataprovider) - a different TestNG-like dataprovider runner for JUnit and Allure.
 * [Pragmatists/JunitParams](https://github.com/Pragmatists/JUnitParams)
 * [junit contribution: test "assumes" annotation to build inter test dependencies](https://github.com/junit-team/junit.contrib/tree/master/assumes)
 * [XLS Test - Excel testing library](https://github.com/codeborne/xls-test)
 * [Selenium data driven testing with Excel](https://www.swtestacademy.com/data-driven-excel-selenium/)
 * [using google spreadsheet with java](https://www.baeldung.com/google-sheets-java-client)

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
