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
public class FilteredFileParamsTest extends DataTest {

	/*
	   FilteredFileParamsTest.com.github.sergueik.junitparams.FilteredFileParamsTest: IllegalState
	 */

	// TODO:  separate test for currently failing with an NPE 
	// Reading Open Office named Spreadsheet: Filtered Employee Data
	// java.lang.NullPointerException
	
	@Test
	@ExcelParameters(filepath = "file:src/test/resources/filtered_data.ods", sheetName = "Filtered Employee Data", type = "OpenOffice Spreadsheet", debug = true, controlColumn = "ENABLED", withValue = "1")
	public void loadParamsFromFileOpenOfficeSpreadsheetFilteredByColumn(
			double rowNum, String keyword, double count) {
		dataTest(keyword, count);
	}

}
