package com.github.sergueik.junitparams;
/**
 *	 Copyright 2017-2019 Serguei Kouzmine
 */

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.custom.CustomParameters;
import junitparams.custom.ParametersProvider;
import junitparams.mappers.CsvWithHeaderMapper;

/**
 * Sample parameterized JUnit test scenarios annotated for ExcelParametersProvider 
 * JUnitparams data provider plugin and JSON mapper
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

@RunWith(JUnitParamsRunner.class)
public class FileParamsTest extends DataTest {

	// NOTE: one is not allowed to define annotation parameter dynamically:
	// Compilation failure:
	// [ERROR] FileParamsTest.java:
	// element value must be a constant expression

	// private final String jsonDataPath = ParamDataUtils.param();
	// private final static String testDataPath = ParamDataUtils.testDataPath;

	private static Map<String, String> env = System.getenv();

	// Detect Travis build
	private static final boolean isCIBuild = (env.containsKey("TRAVIS")
			&& env.get("TRAVIS").equals("true")) ? true : false;

	// NOTE: The value for every annotation attribute must be a constant
	// expression. One cannot evaluate it conditionally.
	// The following commented code will fail to compile
	/*
	private static final String jsonDataPath = isCIBuild
			? "file:src/test/resources/data.json"
			: "file:c:/ProgramData/Temp/data.json";
	*/
	// See the README.md for more detais and workaround

	// private static final String jsonDataPath =
	// "file:c:/ProgramData/Temp/data.json";
	private static final String jsonDataPath = "file:src/test/resources/data.json";
	// private static final String testDataPath =
	// "file:c:/Users/${env:USERNAME}/Documents/data.ods";

	private static final String testDataPath = "file:src/test/resources/data.ods";

	@Test
	@ExcelParameters(filepath = "classpath:data_2007.xlsx", sheetName = "", type = "Excel 2007", debug = true)
	public void loadParamsFromEmbeddedExcel2007(double rowNum, String keyword,
			double count) {
		dataTest(keyword, count);
	}

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

	// TODO: Allow columns specification interface
	// e.g. the rowNum column is present in the spreadsheet
	// but not needed for the test
	@Test
	@ExcelParameters(filepath = "classpath:data_2003.xls", sheetName = "", type = "Excel 2003", debug = true)
	public void loadParamsFromEmbeddedExcel2003(double rowNum, String keyword,
			double count) {
		dataTest(keyword, count);
	}

	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data_2003.xls", sheetName = "", type = "Excel 2003", debug = true)
	public void loadParamsFromFileExcel2003(double rowNum, String keyword,
			double count) {
		dataTest(keyword, count);
	}

	@Test
	@ExcelParameters(filepath = "classpath:data.ods", sheetName = "", type = "OpenOffice Spreadsheet")
	public void loadParamsFromEmbeddedOpenOfficeSpreadsheet(double rowNum,
			String keyword, double count) {
		dataTest(keyword, count);
	}

	@Test
	@ExcelParameters(filepath = testDataPath, sheetName = "", type = "OpenOffice Spreadsheet", debug = true)
	public void loadParamsFromFileOpenOfficeSpreadsheetUsingVariable(
			double rowNum, String keyword, double count) {
		dataTest(keyword, count);
	}

	@Test
	// NOTE: cannot conditionally evaluate the annotation attribute
	// @ExcelParameters(filepath = "file:${USERPROFILE}/Desktop/data.ods",
	// sheetName = "", type = "OpenOffice Spreadsheet", debug = true)
	@ExcelParameters(filepath = "file:src/test/resources/data.ods", sheetName = "", type = "OpenOffice Spreadsheet", debug = true)
	public void loadParamsFromFileOpenOfficeSpreadsheet(double rowNum,
			String keyword, double count) {
		dataTest(keyword, count);
	}

	@Test
	@FileParameters(value = "classpath:data.json", mapper = JSONMapper.class)
	public void loadParamsFromJSONEmbedded(String strCount, String keyword) {
		dataTest(strCount, keyword);
	}

	@Test
	@FileParameters(value = jsonDataPath, mapper = JSONMapper.class)
	public void loadParamsFromJSONFile(String strCount, String strKeyword) {
		dataTest(strCount, strKeyword);
	}

	@Test
	@FileParameters(value = "file:src/test/resources/data.json", mapper = JSONMapper.class)
	public void loadParamsFromJSONFileFromStaticValue(String strCount,
			String strKeyword) {
		dataTest(strCount, strKeyword);
	}

	public static class Person {

		private String name;
		private int age;

		public Person(Integer age) {
			this.age = age;
		}

		public Person(String name, Integer age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public boolean isAdult() {
			return age >= 18;
		}

		public int getAge() {
			return age;
		}

		@Override
		public String toString() {
			return "Person of age: " + age;
		}
	}

	public static class PersonMapper extends CsvWithHeaderMapper {
		@Override
		public Object[] map(Reader reader) {
			Object[] map = super.map(reader);
			List<Object[]> result = new LinkedList<>();
			for (Object lineObj : map) {
				String line = (String) lineObj;
				result.add(new Object[] { line.substring(2),
						Integer.parseInt(line.substring(0, 1)) });
			}
			return result.toArray();
		}
	}

	@Test
	@Parameters(source = ParamSetProvider.class)
	public void paramSetFromClassTest(String str, Integer val) {
		assertThat(str, is(equalTo("a")));
		assertThat(val, is(equalTo(1)));
	}

	public static class ParamSetProvider {
		public static Object[] provideParamSet() {
			return new Object[] { "a", 1 };
		}
	}

	@Test
	@CustomParameters(provider = MethodNameReader.class)
	public void getDataMethodName(String name) throws Exception {
		assertThat(name, equalTo("getDataMethodName"));
	}

	public static class MethodNameReader
			implements ParametersProvider<CustomParameters> {
		private FrameworkMethod frameworkMethod;

		@Override
		public void initialize(CustomParameters parametersAnnotation,
				FrameworkMethod frameworkMethod) {
			this.frameworkMethod = frameworkMethod;
		}

		@Override
		public Object[] getParameters() {
			return new Object[] { frameworkMethod.getName() };
		}
	}

}
