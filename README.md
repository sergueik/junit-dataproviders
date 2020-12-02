###  JUnit-DataProviders

[![BuildStatus](https://travis-ci.org/sergueik/junit-dataproviders.svg?branch=master)](https://travis-ci.org/sergueik/junit-dataproviders)

This project exercises following data providers with
[JUnitParams Junit plugin](https://github.com/Pragmatists/JUnitParams)
and core Junit 4+ `Parameterized` [test runner class](https://junit.org/junit4/javadoc/latest/org/junit/runners/Parameterized.html):

  * Excel 2003 OLE documents - a.k.a. [Horrible SpreadSheet Format](http://shanmugavelc.blogspot.com/2011/08/apache-poi-read-excel-for-use-of.html) `org.apache.poi.hssf.usermodel.*`
  * Excel 2007 OOXML (.xlsx) [XML SpreadSheet Format](http://howtodoinjava.com/2013/06/19/readingwriting-excel-files-in-java-poi-tutorial/) `org.apache.poi.xssf.usermodel.*`
  * OpenOffice SpreadSheet (.ods) [Open Document Format for Office Applications](http://www.jopendocument.org/docs/) `org.jopendocument.dom.*`
  * JSON via [org.json](https://stleary.github.io/JSON-java/) or [com.google.gson](https://www.javadoc.io/doc/com.google.code.gson/gson) (*work in progress*) package
  * YAML via [snakeyaml](https://github.com/asomov/snakeyaml)
  * [Google sheet](https://www.google.com/sheets/about/) (experimental).

The providers can be integrated with Junit 5 tests via adapter (see below).

Unlike core Data Providers in Junit (5?) and TestNg this provider class allows
flexible uniform data file path modification at runtime through
environment setting which is useful e.g. for enabling one to exercize different test configurations
for  __DEV__ / __TEST__ / __UAT__ environments without modifying or recompiling the test suite java code.
The technical  details in __Extra Features__ section below.

### Usage with JUnitParams

* Create the __Excel 2003__, __Excel 2007__ or __Open Office__ spreadsheet with some test-specific parameters e.g.

| ROWNUM |    SEARCH   | COUNT |
|--------|-------------|-------|
| 1      | __junit__   | 100   |
| 2      | __testng__  | 30    |

or a JSON file with the following structure:
```javascript
[{
    "keyword": "junit",
    "count": 101.0
  }, {
    "keyword": "testng",
    "count": 31.0
  }]
```
or
```
{
  "some test": [{
    "keyword": "junit",
    "count": 101.0
  }, {
    "keyword": "testng",
    "count": 31.0
  }],
  "another test": [{
    "parameter": "value",

  }],
}
```
* Annotate the test methods inteneded to get parameterized, in the following way:
```java
@Test
@ExcelParameters(filepath = "classpath:data_2007.xlsx", sheetName = "", type = "Excel 2007")
public void loadParamsFromEmbeddedExcel2007(double rowNum, String keyword, double count) {
  // test code, e.g. confirm the parameters are passed
	assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
	assertThat((int) count).isGreaterThan(0);
}
```
or
```java
@Test
@ExcelParameters(filepath = "file:src/test/resources/data_2003.xls", sheetName = "", type = "Excel 2003")
public void loadParamsFromFileExcel2003(double rownum, String keyword, double count) {
  // test code, e.g. confirm the parameters are passed
	assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
	assertThat((int) count).isGreaterThan(0);
}
```
or
```java
@Test
@ExcelParameters(filepath = "file:${USERPROFILE}/Desktop/data.ods", sheetName = "", type = "OpenOffice Spreadsheet")
public void loadParamsFromFileOpenOfficeSpreadsheel(double rowNum,
    String keyword, double count) {
  // test code, e.g. confirm the parameters are passed
  assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
  assertThat((int) count).isGreaterThan(0);
}
```
or
```java
	private final String jsonDataPath = "file:c:/ProgramData/Temp/data.json";
	@Test
	@FileParameters(value = jsonDataPath, mapper = JSONMapper.class)
	public void loadParamsFromJSONFile(String strCount,
			String strKeyword) {
    // actual test code
		dataTest(strCount, strKeyword);
	}
```
or
```java
	private final static String testDataPath = "file:c:/Users/${env:USERNAME}/Documents/data.ods";
	@Test
	@ExcelParameters(filepath = testDataPath, sheetName = "", type = "OpenOffice Spreadsheet", debug = true)
	public void loadParamsFromFileOpenOfficeSpreadsheetUsingVariable(
			double rowNum, String keyword, double count) {
		dataTest(keyword, count);
	}
```
The `ExcelParametersProvider`-annotated class will read all columns from the __Excel 2007__, __Excel 2003__ or __Open Office__ spreadhsheet
from the file system using relative (to the project directory) or absolute path when `filepath` is defined with a `file:` prefix .
The known system environment settings are being interpolated:
`file:c:/Users/${env:USERNAME}/Documents/data.json`
`file:${USERPROFILE}`
for Excel and Opend Office but not yet for the JSON mapper (*work in progress*)

When test data file is placed outside the project directory, it is often desired to have it in 
`Desktop`, `Downloads` or some other directory of the current user.

The environment variables being OS specific and annotation spec enforcing that every annotation parameter is 
a constant expressions makes it a little bit challenging, preventing one from using class variables or static methods - e.g. code like below:
@Test
@ExcelParameters(filepath = String.format(
    "file:${USERPROFILE}%sDesktop%sdata.ods",
    File.separator), sheetName = "", type = "Spreadsheet", debug = true)
```
will fail with
```
The value for annotation attribute ExcelParameters.filepath must be a constant expression
```

Therefore __JUnit-DataProviders__ internally converts
between `${USERPRFILE}` and `${HOME}` eliminating the need to tweak the path expressions
like `filepath = "file:${USERPROFILE}/Desktop/data.ods"` or `filepath = "file:${HOMEDIR}/Desktop/data.ods" - both would work across OS.


or from inside the jar when `filepath` is defined with a `classpath:` prefix and executes the test for every row of data.
The test developer is responsible for matching the test method argument types and the column data types.

Setting the `debug` flag with `@ExcelParameters` attribute would enable debug messages during the data loading:
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

NOTE: attributes for column selection and forcing every column type to primitive `String` type
are *work in progress*.

### Maven Central

The snapshot versions are deployed to [https://oss.sonatype.org/content/repositories/snapshots/com/github/sergueik/junitparams/junit_params/](https://oss.sonatype.org/content/repositories/snapshots/com/github/sergueik/junitparams/junit_params/)
Release versions status: [pending](https://issues.sonatype.org/browse/OSSRH-36771?page=com.atlassian.jira.plugin.system.issuetabpanels:all-tabpanel).

To use the snapshot version, add the following to `pom.xml`:
```xml
<dependency>
  <groupId>com.github.sergueik.junitparams</groupId>
  <artifactId>junit_dataproviders</artifactId>
  <version>0.0.15-SNAPSHOT</version>
</dependency>

<repositories>
  <repository>
    <id>ossrh</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```
or, for earlier versions of the jar,
```xml
<dependency>
  <groupId>com.github.sergueik.junitparams</groupId>
  <artifactId>junit_params</artifactId>
  <version>0.0.15-SNAPSHOT</version>
</dependency>

<repositories>
  <repository>
    <id>ossrh</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```
### Extra Features for JUnitParams Junit plugin

This data provider overcomes the known difficulty of core TestNG or Junit parameter annotations: developer is
[not allowed to redefine](https://stackoverflow.com/questions/16509065/get-rid-of-the-value-for-annotation-attribute-must-be-a-constant-expression-me)
the dataprovider attributes like in particular the data source path:

```java
public static final String dataPath = "file:src/test/resources/data.json";
  @Test
  @ExcelParameters(filepath = dataPath)
  public void test( double rowNum, String keyword, double count) {
   // actual code ot the  test
  }
```
In the above, one is only allowed to initialize the `testDataPath` to a `String`(or `int`) primitive type, in particular, one can not set
it differently for Jenkins / Travis automated build environment and IDE like below:
```java

private static final String jsonDataPath = (env.containsKey("TRAVIS")
    && env.get("TRAVIS").equals("true")) ?
    ? "file:src/test/resources/data.json"
    : "file:c:/ProgramData/Temp/data.json";
@Test
@FileParameters(value = jsonDataPath, mapper = JSONMapper.class)
public void loadParamsFromJSONFile(String strCount, String strKeyword) {
  dataTest(strCount, strKeyword);
}
```
or
```java
private static final boolean isCIBuild = (env.containsKey("TRAVIS")
    && env.get("TRAVIS").equals("true")) ? true : false;

@Test
@ExcelParameters(filepath = isCIBuild ? "file:src/test/resources/data.ods"
    : "file:${USERPROFILE}/Desktop/data.ods", sheetName = "", type = "OpenOffice Spreadsheet", debug = true)
public void loadParamsFromFileOpenOfficeSpreadsheet(double rowNum,
    String keyword, double count) {
  dataTest(keyword, count);
}
```
would fail to compile:
```sh
Compilation failure: The value for annotation attribute must be a constant expression
```
Even declaring the same (pseudo-const) data as a `static final String` in a separate class:

```java
public class ParamData {
  public final static String dataPath = "file:src/test/resources/data.json";
}
```
and assigning the result to the variable in the main test class,
```java
public class FileParamsTest {

  private final static String dataPath = ParamData.dataPath;
```
will lead the the same error convinsincg one it likely not doable.
This limitation is not observed with the core Junit `Parameterized` test runner class. Porting all data file kinds to use with this provideris
a work in progress, currently only the JSON provider is converted.

However it is quite easy to implement this functionality in the data provider class `ExcelParametersProvider` itself by adding an extra class variable named e.g. `testEnvironment` that would receive its value from e.g. the environment variable named `TEST_ENVIRONMENT` and, when non-blank, would override the data file paths which were specified through the `file://` protocol prefix
and which therefore referred to the system file paths (not to data embedded inside the jar):
After this is done the original test data provider annotation
```java
  @Test
  @ExcelParameters(filepath = "file:src/test/resources/data.xlsx")
  public void test(double rowNum, String keyword, double count) {
    try {
    dataTest(keyword, count);
    } catch (IllegalStateException e) {
    System.err
    .println(String.format("keyword: %s , cound : %d ", keyword, count));
    }
  }
```
combined with environment `TEST_ENVIRONMENT` set to e.g. `dev` will make dataprovider read the test data from `src/test/resources/dev/data.xlsx` rather then `src/test/resources/data.xlsx`.

It is implemented directly in the `ExcelParametersProvider` provider in a very basic fashion as shown below:

```java
public class ExcelParametersProvider implements ParametersProvider<ExcelParameters> {

  private final static String testEnvironment = (System.getenv("TEST_ENVIRONMENT") != null) ? System.getenv("TEST_ENVIRONMENT") : "";
```

and

```java
  public void initialize(ExcelParameters parametersAnnotation, FrameworkMethod frameworkMethod) {
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
      String updatedFilename = filename.replaceAll("^(.*)/([^/]+)$", String.format("$1/%s/$2", testEnvironment));
      filename = updatedFilename;
    }
    // ... rest of initialization
  }
```

Running the test in debug mode
```cmd
copy src\test\resources\data.* src\test\resources\dev\
set  TEST_ENVIRONMENT=dev
mvn test
```
works just as expected - in the example the processing of the Open Office data file `data.ods` and Excel 2003 data file `data_2003.xls` driven tests is shown:

```cmd
Amending the src/test/resources/data.ods with dev => src/test/resources/dev/data.ods
Reading Open Office Spreadsheet: Employee Data
```
```sh
export TEST_ENVIRONMENT=test
mkdir -p src/test/resources/$TEST_ENVIRONMENT
cp src/test/resources/data* src/test/resources/$TEST_ENVIRONMENT
mvn test
Amending the src/test/resources/data_2003.xls with test => src/test/resources/test/data_2003.xls
createDataFromExcel2003: Reading Excel 2003 sheet: Employee Data
```

One can easily tweak this behavior further: e.g. turn the name `TEST_ENVIRONMENT` of the key envirnment variable into a separate parameter or define environment specifics via property file (this is work in progress). Similar changes are available for [testNg-DataProviders](https://github.com/sergueik/testng-dataproviders).

### Usage with JUnit Parameterized runnner

.

Instance Constructor and Class propetry injection annotations for test
parameterization are basically supported by
[Junit 4 onward](https://github.com/junit-team/junit4/wiki/parameterized-tests) via an `org.junit.runners.Parameterized` class.
However the core JUnit wiki does not mention storing test data in external data file which is entirely possible with core JUnit Parameterized tests:

instead of hard coding the data in the test class
```java
@RunWith(Parameterized.class)
public class StraightParameterizedConstructorTest extends DataTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { 1.0, "junit", 204 }});
	}

	private double rowNum;
	private String keyword;
	private int count;

	// constructor injection
	public StraightParameterizedConstructorTest(double rowNum, String keyword, int count) {
		this.rowNum = rowNum;
		this.keyword = keyword;
		this.count = count;
	}
```
one can define a singleton class based on one of the classes currently available in `com.github.sergueik.junitparams`:
```
public class DataSource {

	private static DataSource instance = new DataSource();

	private DataSource() {
	}

	public static DataSource getInstance() {
		return instance;
	}

	// De-serialize the rowset of String data parameters from the JSON file from
	// the provided path property
	// for later become injected in the test via @Parameters collection
	public Collection<Object[]> getdata() {

		try {
			// temporarily store a replica of code from JSONMapper class
			return Arrays.asList(createDataFromJSON());
		} catch (JSONException e) {
			if (debug) {
				System.err.println("Failed to load data from datafile: " + dataFile);
			}
			return new ArrayList<Object[]>();
		}
	}

	public Object[][] createDataFromJSON {
  // read and parse JSON
  }
	public Object[][] createDataFromYAML {
  // read and parse YAML
  }
```
and then set the instance of `DataSource` class within the `Test` class with path to the data,
optionally with other paratemetes like column selection:

```java
@RunWith(Parameterized.class)
public class DataProviderClassParameterizedPropertiesInjectionTest extends DataTest {

	private static DataSource dataSource = DataSource.getInstance();
	private static String dataFile = "src/test/resources/data.json";

	@Parameters
	public static Collection<Object[]> data() {
		dataSource.setDataFile(dataFile);
		return dataSource.getdata();
	}

  private String rowNum;
	private String keyword;
	private String count;

	public DataProviderClassParameterizedConstructorTest(String rowNum,
			String keyword, String count) {
		this.rowNum = rowNum;
		this.keyword = keyword;
		this.count = count;
	}

	@Test
	public void parameterizedTest() {
		try {
			dataTest(count, keyword);
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("keyword: %s , count : %d ", keyword, count));
		}
	}
```

The only downside is that, at least with JSON and YAML data files, the only supported `@parameter` data type
is the `String` primitive type.

The other minor known issue when loading from JSON the column order is not fully predictable and so is
better be enforced through an extra property (that is *work in progress*, addressed already for YAML).

### Note

This project and the [testNg-DataProviders](https://github.com/sergueik/testng-dataproviders) -
have large code overlap for processing spreadsheets, evolve in parallel and only differ in low level
test methdod annotation details.

Note: the [JUnitParams](https://github.com/Pragmatists/JUnitParams) project seems to have been dormant for quite some time,
but the PR is in the works.

### Apache POI compatibility

  * The default version of the supported Apache POI is __3.17__.
  * Project can be compiled with latest release of Apache POI __4.1.0__ with the help of profile `poi410`.
  * Older versions of the package require minor code refactoring. Note that you may also have to clear the other versions of poi and poi-ooxml jars from maven cache '~/.m2/repository'
  * Creating branches and tags is a work in progress.


### Filtering Data Rows for JUnitParams

In addition to using *every row* of spreadsheet as test parameter one may create a designated column which value
would be indicating to use or skip that row of data, like:



| ROWNUM |    SEARCH    | COUNT |ENABLED
|--------|--------------|-------|-------
| 1      | __junit__    | 100   | 1
| 2      | __testng__   | 30    | 1
| 3      | __spock__    | 20    | 0
| 4      | __mockito__  | 41    | 1

and annotate the method like

```java
@Test
@ExcelParameters(filepath = "file:src/test/resources/filtered_data.ods",
sheetName = "Filtered Employee Data", type = "OpenOffice Spreadsheet",
debug = true, controlColumn = "ENABLED", withValue = "1")
public void loadParamsFilteredByColumn(
    double rowNum, String keyword, double count) {
  dataTest(keyword, count);
}
```


with this data setting only rows 1,2 and 4 from the data extract above would be used as `loadParamsFilteredByColumn` test method parameters.
The control column itself is not passed to the subject test method.
Currently this functionality is implemented for __OpenOffice__ spreadsheet only,  in the __junit__ data provider.
Remaining format and testng provider data filtering is a work in progress.
This feature of storing more then one set of tests in one spreadsheet and picking the ones which column is set to a specified value
 has been inspired by some python [post](https://docs.pytest.org/en/latest/fixture.html#parametrizing-fixtures)
and the [forum (in Russian)(http://software-testing.ru/forum/index.php?/topic/37870-kastomizatciia-parametrizatcii-v-pytest/).

### Junit 5 Adapter

To use the Excel and other data providers with Jnit5 `@ParameterizedTest` one can embed an [adapter](https://www.baeldung.com/java-adapter-pattern) 
into the `@MethodSource` method (for simlicity the needed arguments made class-level static)

```java

private final static String filepath = "classpath:data2_2007.xlsx";
private final static String sheetName = "";
private final static String type = "Excel 2007";
private final static boolean debug = false;
private final static String controlColumn = "";
private final static String withValue = "";
private static final ExcelParameters parametersAnnotation = new ExcelParameters() {
	@Override
	public String filepath() {
		return filepath;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		// NOTE: the method needed for the interface is Junit 4 legacy:
		// Returns the annotation type of this annotation.
		return null;
	}

	@Override
	public String sheetName() {
		return sheetName;
	}

	@Override
	public String type() {
		return type;
	}

	@Override
	public boolean loadEmptyColumns() {
		return false;
	}

	@Override
	public boolean debug() {
		return debug;
	}

	@Override
	public String controlColumn() {
		return controlColumn;
	}

	@Override
	public String withValue() {
		return withValue;
	}
};

// adapter
private static Stream<Object> testData() {
	ExcelParametersProvider provider = new ExcelParametersProvider();

	try {
		Class<?> _class = Class.forName("com.github.sergueik.junit5params.CurrentMethodDataTest");
		Method _method = _class.getMethod("dummy", String.class);
		FrameworkMethod _frameworkMedhod = new FrameworkMethod(_method);
		provider.initialize(parametersAnnotation, _frameworkMedhod);
	} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
		System.err.println("Exception (ignored): " + e.getMessage());
		// e.printStackTrace();
	} catch (java.lang.NullPointerException e) {
		// for unsatisfied Excel Parameter properties
		e.printStackTrace();
	}
	Object[] parameters = provider.getParameters();
	if (debug) {
		System.err.println(String.format("Received %d parameters", parameters.length));
	}
	if (debug) {
		for (int cnt = 0; cnt != parameters.length; cnt++) {
			Object[] row = (Object[]) parameters[cnt];
			System.err.println(String.format("parameter # %d: %s", cnt, String.valueOf(row[0])));
		}
	}
	return Stream.of(parameters);

}
```
NOTE: the `frameworkMethod` argument  in `initialize` 
seems to be a legacy
 - represents a method on a test class to be invoked
  at the appropriate point in test execution.
		Such methods are usually annotated via
		`@Test`, `@Before`, `@After`, `@BeforeClass`, `@AfterClass`, etc.
Therefore add a dummy public method just for the adapter needs

```java
static public void dummy(String data) {

}

```
then inject paramers as [usual](https://www.baeldung.com/parameterized-tests-junit-5):
```java
@ParameterizedTest
@MethodSource("testData")
public void test(Object param) {
	// TODO: debug being called
	assertThat(param, notNullValue());
	System.err.println("Parameter: " + param.toString());
}
```

This will produce:
```sh
Parameter: junit
Parameter: testng
Parameter: spock
```

NOTE: for this test only, leave just ine column of data in Excel file. Converion of multi parameter annotations is a work in proggess.
### Work in Progress

  * rename `JSONMapper` that is implementing a somewhat limited [DataMapper](http://javadox.com/pl.pragmatists/JUnitParams/1.0.4/junitparams/mappers/DataMapper.html)
  interface to `JSONFileParameters` and construct the new class`JSONParameterProvider` which would fully implement `ParametersProvider<JSONParameters>` all the way for feature parity with the `ExcelParametersProvider`.
  * produce Javadoc
  * fix legacy JSON code
  * add more YAML data providers


### See Also

  * [Introduction to JUnitParams](https://www.baeldung.com/junit-params)
  * Using Excel,Open Office,JSON as [testNG data providers](https://github.com/sergueik/testng-dataproviders)
  * [testng dataProviders](http://testng.org/doc/documentation-main.html#parameters-dataproviders)
  * [TNG/junit-dataprovider](https://github.com/TNG/junit-dataprovider) - a different TestNG-like dataprovider runner for JUnit and Allure.
  * [OpenOffice SpreadSheet example1](http://www.programcreek.com/java-api-examples/index.php?api=org.jopendocument.dom.spreadsheet.Sheet), [example 2](http://half-wit4u.blogspot.com/2011/05/read-openoffice-spreadsheet-ods.html)
  * [Pragmatists/JunitParams](https://github.com/Pragmatists/JUnitParams)
  * [junit contribution: test "assumes" annotation to build inter test dependencies](https://github.com/junit-team/junit.contrib/tree/master/assumes)
  * [XLS Test - Excel testing library](https://github.com/codeborne/xls-test)
  * [Selenium data driven testing with Excel](https://www.swtestacademy.com/data-driven-excel-selenium/)
  * [using google spreadsheet with java](https://www.baeldung.com/google-sheets-java-client)
  * [JUnit 4 Wiki about Parameterization of test classes](https://github.com/junit-team/junit4/wiki/parameterized-tests)
  * [JUnit - Parameterized Test intro ](https://www.tutorialspoint.com/junit/junit_parameterized_test.htm).
  * [Junit test runners summary](https://github.com/junit-team/junit4/wiki/test-runners)
  * [YamlDataProviderImpl](http://paypal.github.io/SeLion/html/java-docs/1.0.0/dataproviders-apis/index.html?com/paypal/selion/platform/dataprovider/impl/YamlDataProviderImpl.html) YAML data provider in `com.paypal.selion.platform.dataprovider`
  * [Examples of Fillo SQL DSL](https://github.com/GladsonAntony/Fillo-Examples)  for Excel spreadsheet-backed Select and Update operations.
  * [JUnit4, JUnit5, TestNG comparison](https://www.baeldung.com/junit-vs-testng), covers parameteterized tests
  * JUnit5 test selector annotation [article](https://habr.com/ru/post/464881/)(in Russian) and the [repository](https://github.com/bvn13/JavaLessons/tree/master/springboot2-junit5-skiptest)
  * [JUnit5 test execution order support](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order) and [discussion](https://automated-testing.info/t/junit5-zapusk-serii-testov-strogo-posledovatelno-ili-v-polzovatelskom-poryadke-varianty/18609)(in Russian)
  * [Jackcess](https://jackcess.sourceforge.io) java library for working with MS Access databases
  * [memory-optimizied](https://github.com/alibaba/easyexcel) rewrite of Apache poi
  * [guide](https://www.baeldung.com/parameterized-tests-junit-5) to Junit 5 `@ParameterizedTest`
  * general [user guide](https://junit.org/junit5/docs/current/user-guide/)

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
