package com.github.sergueik.junitparams;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

import junitparams.JUnitParamsRunner;
import junitparams.custom.ParametersProvider;

/**
 * ExcelParametersProvider junitparams data providers for Excel and OpenOffice spreadsheet content 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
@RunWith(JUnitParamsRunner.class)
public class ExcelParametersProvider
		implements ParametersProvider<ExcelParameters> {

	private Utils utils = Utils.getInstance();
	private String filepath;
	private String filename;
	private String protocol;
	private String type;
	private String sheetName;
	private String columnNames = "*";
	private boolean loadEmptyColumns = false;
	private boolean debug = false;

	// TODO: pass flag to skip / collect the first row through ExcelParameters
	// interface annotation - may be an overkill
	// private static Boolean skipFirstRow = false;

	// passed via ExcelParameters
	// public void setDebug(boolean debug) {
	// this.debug = debug;
	// }

	@Override
	public void initialize(ExcelParameters parametersAnnotation,
			FrameworkMethod frameworkMethod) {
		filepath = parametersAnnotation.filepath();
		type = parametersAnnotation.type();
		sheetName = parametersAnnotation.sheetName();
		protocol = filepath.substring(0, filepath.indexOf(':'));
		filename = filepath.substring(filepath.indexOf(':') + 1);
		debug = parametersAnnotation.debug();
		loadEmptyColumns = parametersAnnotation.loadEmptyColumns();
		utils.setDebug(debug);
		utils.setSheetName(sheetName);
		utils.setColumnNames(columnNames);
		utils.setLoadEmptyColumns(loadEmptyColumns);
	}

	@Override
	public Object[] getParameters() {
		return paramsFromFile();
	}

	private Object[] map(InputStream inputStream) {
		switch (type) {
		case "Excel 2007":
			return createDataFromExcel2007(inputStream);
		case "Excel 2003":
			return createDataFromExcel2003(inputStream);
		case "OpenOffice Spreadsheet":
			return createDataFromOpenOfficeSpreadsheet(inputStream);
		default:
			throw new RuntimeException("wrong format");
		}
	}

	public InputStream createProperReader() throws IOException {

		// System.err.println("createProperReader: " + filepath);
		if (filepath.indexOf(':') < 0) {
			return new FileInputStream(filepath);
		}

		if ("classpath".equals(protocol)) {
			return getClass().getClassLoader().getResourceAsStream(filename);
		} else if ("file".equals(protocol)) {
			return new FileInputStream(filename);
		}

		throw new IllegalArgumentException(
				"Unknown file access protocol. Only 'file' and 'classpath' are supported!");
	}

	private Object[] paramsFromFile() {
		try {
			InputStream inputStream = createProperReader();
			try {
				return map(inputStream);
			} finally {
				inputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Could not successfully read parameters from file: " + filepath, e);
		}
	}

	private Object[] createDataFromOpenOfficeSpreadsheet(
			InputStream inputStream) {
		List<Object[]> result = utils
				.createDataFromOpenOfficeSpreadsheet(inputStream);
		return result.toArray();
	}

	private Object[] createDataFromExcel2003(InputStream inputStream) {
		List<Object[]> result = utils.createDataFromExcel2003(inputStream);
		return result.toArray();
	}

	private Object[] createDataFromExcel2007(InputStream inputStream) {
		List<Object[]> result = utils.createDataFromExcel2007(inputStream);
		if (debug) {
			int cnt = 0;
			for (Object[] row : result) {
				System.err
						.println(String.format("Row[%d]: %s", cnt, Arrays.toString(row)));
				cnt++;
			}
		}
		return result.toArray();
	}
}
