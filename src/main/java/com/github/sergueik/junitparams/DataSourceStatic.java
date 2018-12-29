package com.github.sergueik.junitparams;
/**
 * Copyright 2018 Serguei Kouzmine
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

/**
 * Class with static methods for loading data from the file  
 * through a Juit4 @Parameter annotation into test class public properties
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DataSourceStatic {

	private static String dataFilePath = null;
	private static String defaultColumn = "row";
	// can be used for ordering the data in rowset
	private static String skipRowColumn = "skip";
	// can be used for filtering the data in json
	private static boolean debug = true;
	// reserved
	private static String encoding = "UTF-8";
	// TODO: provide nulls or empty strings for missing input
	private static List<String> columns = Arrays
			.asList(new String[] { "row", "keyword", "count" });

	public static void setDataFilePath(String value) {
		dataFilePath = value;
	}

	// NOTE: with gson one will either annotated all differences
	// or (default) to name serializable properties in Java
	// source the same name as JSON keys

	public static String getdefaultColumn() {
		return defaultColumn;
	}

	public static void setDefaultColumn(String value) {
		defaultColumn = value;
	}

	public static void setSkipRowColumn(String value) {
		skipRowColumn = value;
	}

	public static void setColumns(List<String> value) {
		columns = value;
	}

	public void setDebug(boolean value) {
		debug = value;
	}

	// De-serialize the JSON file into a rowset of String data parameters
	public static Collection<Object[]> getdata() {

		try {
			return Arrays.asList(createDataFromJSON());
		} catch (JSONException e) {
			if (debug) {
				System.err
						.println("Failed to load data from datafile: " + dataFilePath);
			}
			return new ArrayList<Object[]>();
		}
	}

	public static Object[][] createDataFromJSON() throws org.json.JSONException {

		if (debug) {
			System.err.println("Data file path: " + dataFilePath + "\n"
					+ "Data columns: " + (Arrays.deepToString(columns.toArray())) + "\n"
					+ "Default column: " + defaultColumn + "\n" + "Skip row column: "
					+ skipRowColumn);
		}

		List<Object[]> testData = new ArrayList<>();
		List<Object> testDataRow = new LinkedList<>();
		List<String> hashes = new ArrayList<>();

		JSONArray rows = new JSONArray();

		try {
			byte[] encoded = Files.readAllBytes(Paths.get(dataFilePath));
			rows = new JSONArray(new String(encoded, Charset.forName(encoding)));
		} catch (org.json.JSONException e) {
			System.err.println("Exception (ignored) : " + e.toString());
		} catch (IOException e) {
			System.err.println("Exception (ignored) : " + e.toString());
		}

		for (int i = 0; i < rows.length(); i++) {
			try {
				String entry = rows.getString(i);
				hashes.add(entry);
			} catch (org.json.JSONException e) {
				System.err.println("Exception (ignored) : " + e.toString());
			}
		}
		Assert.assertTrue(hashes.size() > 0);

		String firstRow = hashes.get(0);

		// NOTE: apparently after invoking org.json.JSON library the order of keys
		// inside the firstRow will be non-deterministic
		// https://stackoverflow.com/questions/4515676/keep-the-order-of-the-json-keys-during-json-conversion-to-csv
		firstRow = firstRow.replaceAll("\n", " ").substring(1,
				firstRow.length() - 1);
		if (debug)
			System.err.println("1st row: " + firstRow);

		List<String> actualColumns = new ArrayList<>();
		String[] pairs = firstRow.split(",");

		for (String pair : pairs) {
			String[] values = pair.split(":");

			String column = values[0].substring(1, values[0].length() - 1).trim();
			if (debug) {
				System.err.println("column: " + column);
			}
			actualColumns.add(column);
		}

		for (String entry : hashes) {
			JSONObject entryObj = new JSONObject();
			testDataRow = new LinkedList<>();
			try {
				entryObj = new JSONObject(entry);
			} catch (org.json.JSONException e) {
				e.printStackTrace();
			}

			if (skipRowColumn != null && entryObj.has(skipRowColumn)
					&& Boolean.parseBoolean(entryObj.get(skipRowColumn).toString())) {
				if (debug) {
					System.err.println("Will skip loading row: " + entry.toString());
				}
				continue;
			}

			// actualColumns is ignored
			for (String column : columns) {
				testDataRow.add(entryObj.get(column).toString());
			}
			testData.add(testDataRow.toArray());

		}

		Object[][] testDataArray = new Object[testData.size()][];
		testData.toArray(testDataArray);
		return testDataArray;
	}
}
