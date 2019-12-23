package com.github.sergueik.junitparams;
/**
 *	 Copyright 2017-2019 Serguei Kouzmine
 */

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.CoreMatchers.is;
// import static org.hamcrest.core.Is.is;

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
public class OpenOfficeParamsTest extends DataTest {

	private static Map<String, String> env = System.getenv();

	// Detect Travis build
	private static final boolean isCIBuild = (env.containsKey("TRAVIS")
			&& env.get("TRAVIS").equals("true")) ? true : false;

	// private static final String testDataPath =
	// "file:c:/Users/${env:USERNAME}/Documents/data.ods";
	private static final String testDataPath = "file:src/test/resources/data.ods";

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
	@ExcelParameters(filepath = "file:${USERPROFILE}\\Desktop\\data.ods", sheetName = "", type = "OpenOffice Spreadsheet", debug = true)
	public void loadParamsFromFileOpenOfficeSpreadsheetDesktop(double rowNum,
			String keyword, double count) {
		dataTest(keyword, count);
	}

	@Test
	@ExcelParameters(filepath = "file:${HOME}/Desktop/data.ods", sheetName = "", type = "OpenOffice Spreadsheet", debug = true)
	public void loadParamsFromFileOpenOfficeSpreadsheet2Desktop(double rowNum,
			String keyword, double count) {
		dataTest(keyword, count);
	}

}
