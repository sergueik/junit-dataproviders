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
 * Sample parameterized JUnit test scenarios annotated for
 * ExcelParametersProvider JUnitparams data provider plugin and JSON mapper
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

@RunWith(JUnitParamsRunner.class)
public class ExcelParamsTest extends DataTest {

	private static Map<String, String> env = System.getenv();

	// Detect Travis build
	private static final boolean isCIBuild = (env.containsKey("TRAVIS") && env.get("TRAVIS").equals("true")) ? true
			: false;

	@Test
	@ExcelParameters(filepath = "classpath:data_2007.xlsx", sheetName = "", type = "Excel 2007", debug = true)
	public void loadParamsFromEmbeddedExcel2007(double rowNum, String keyword, double count) {
		dataTest(keyword, count);
	}

	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data_2007.xlsx", sheetName = "", type = "Excel 2007")
	public void loadParamsFromFileExcel2007(double rowNum, String keyword, double count) {
		try {
			dataTest(keyword, count);
		} catch (IllegalStateException e) {
			System.err.println(String.format("keyword: %s , cound : %d ", keyword, count));
		}
	}

	// TODO: Allow columns specification interface
	// e.g. the rowNum column is present in the spreadsheet
	// but not needed for the test
	@Test
	@ExcelParameters(filepath = "classpath:data_2003.xls", sheetName = "", type = "Excel 2003", debug = true)
	public void loadParamsFromEmbeddedExcel2003(double rowNum, String keyword, double count) {
		dataTest(keyword, count);
	}

	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data_2003.xls", sheetName = "", type = "Excel 2003", debug = true)
	public void loadParamsFromFileExcel2003(double rowNum, String keyword, double count) {
		dataTest(keyword, count);
	}
}
