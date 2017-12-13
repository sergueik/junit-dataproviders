package com.github.sergueik.junitparams;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.custom.CustomParameters;
import junitparams.custom.ParametersProvider;
import junitparams.custom.FileParametersProvider;
import junitparams.mappers.CsvWithHeaderMapper;
import junitparams.mappers.DataMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

//OLE2 Office Documents
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.Row;
// conflicts with org.jopendocument.dom.spreadsheet.Cell;
// import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.usermodel.CellType;

//Office 2007+ XML
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Open Office Spreadsheet
import org.jopendocument.dom.ODDocument;
import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.ODValueType;

import org.jopendocument.dom.spreadsheet.Cell;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 * Selected test scenarios annotated for ExcelParametersProvider junitparams data provider and JSON mapper
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

@RunWith(JUnitParamsRunner.class)
public class FileParamsTest {

	// @Ignore
	@Test
	@ExcelParameters(filepath = "classpath:data_2007.xlsx", sheetName = "", type = "Excel 2007")
	public void loadParamsFromEmbeddedExcel2007(double rowNum, String keyword,
			double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		 */
	}

	// @Ignore
	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data_2007.xlsx", sheetName = "", type = "Excel 2007")
	public void loadParamsFromFileExcel2007(double rowNum, String keyword,
			double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		 */
	}

	// @Ignore
	// TODO: fields?
	// the rowNum column is not used in the test but present in the spreadsheet
	@Test
	@ExcelParameters(filepath = "classpath:data_2003.xls", sheetName = "", type = "Excel 2003")
	public void loadParamsFromEmbeddedExcel2003(double rowNum, String keyword,
			double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		 */
	}

	// @Ignore
	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data_2003.xls", sheetName = "", type = "Excel 2003")
	public void loadParamsFromFileExcel2003(double rowNum, String keyword,
			double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		 */
	}

	// @Ignore
	@Test
	@ExcelParameters(filepath = "classpath:data.ods", sheetName = "", type = "OpenOffice Spreadsheet")
	public void loadParamsFromEmbeddedOpenOfficeSpreadsheel(double rowNum,
			String keyword, double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
	}

	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data.ods", sheetName = "", type = "OpenOffice Spreadsheet")
	public void loadParamsFromFileOpenOfficeSpreadsheel(double rowNum,
			String keyword, double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("OpenOffice Spreadsheet: Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
						*/
	}

	@Test
	@FileParameters(value = "classpath:data.json", mapper = JSONMapper.class)
	public void loadParamsFromJSONEmbedded(String strCount, String strKeyword) {

		assertThat(strKeyword, notNullValue());
		assertTrue(strKeyword.matches("(?:junit|testng|spock)"));
		double count = Double.valueOf(strCount);
		assertThat((int) count).isGreaterThan(0);
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						strKeyword, (int) count));

	}

	// @Ignore
	@Test
	@FileParameters(value = "file:src/test/resources/data.json", mapper = JSONMapper.class)
	public void loadParamsFromJSONFile(String strCount, String strKeyword) {
		assertThat(strKeyword, notNullValue());
		assertTrue(strKeyword.matches("(?:junit|testng|spock)"));
		double count = Double.valueOf(strCount);
		assertThat((int) count).isGreaterThan(0);
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						strKeyword, (int) count));
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

	@Ignore
	@Test
	@Parameters(source = ParamSetProvider.class)
	public void paramSetFromClassTest(String str, Integer val) {
		assertThat(str).isEqualTo("a");
		assertThat(val).isEqualTo(1);
	}

	public static class ParamSetProvider {
		public static Object[] provideParamSet() {
			return new Object[] { "a", 1 };
		}
	}

	@Ignore
	@Test
	@CustomParameters(provider = MethodNameReader.class)
	public void getDataMethodName(String name) throws Exception {
		assertThat(name).isEqualTo("getDataMethodName");
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
