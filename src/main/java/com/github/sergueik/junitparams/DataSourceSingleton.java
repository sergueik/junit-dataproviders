package com.github.sergueik.junitparams;
/**
 * Copyright 2018 Serguei Kouzmine
 */

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import org.junit.Assert;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Class with an (optional) Singleton constructor for loading the data 
 * from a JSON (poi and OpenDoc also possible) data file  
 * through a Juit4 @Parameter annotation into test class parameterized constructor 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DataSourceSingleton {

	private String dataFile = "";
	private String filePath = null;
	private static String encoding = "UTF-8";
	private String dataKey = "datakey";
	private List<String> columns = Arrays
			.asList(new String[] { "row", "keyword", "count" });
	private boolean debug = true;

	// currently unused
	// private String defaultKey = "row";

	// DataSource does not have to be a singleton
	private static DataSourceSingleton instance = new DataSourceSingleton();

	private DataSourceSingleton() {
	}

	public static DataSourceSingleton getInstance() {
		return instance;
	}

	// De-serialize from the JSON file under caller provided path into a row set
	// of String or strongly-typed (?)
	// for injection into the test class instance via @Parameters annotated
	// constructor
	public Collection<Object[]> getdata() {

		try {
			// NOTE: temporarily store a close replica of JSONMapper class method
			return Arrays.asList(createDataFromJSON());
		} catch (JSONException e) {
			if (debug) {
				System.err.println("Failed to load data from datafile: " + dataFile);
			}
			return new ArrayList<Object[]>();
		}
	}

	public void setDataFile(String value) {
		this.dataFile = value;
		filePath = String.format("%s/%s", System.getProperty("user.dir"),
				this.dataFile);
	}

	public String getDataKey() {
		return this.dataKey;
	}

	public void setDataKey(String value) {
		this.dataKey = value;
	}

	public void setColumns(List<String> value) {
		this.columns = value;
	}

	public void setDebug(boolean value) {
		this.debug = value;
	}

	public Object[][] createDataFromJSON() throws org.json.JSONException {

		if (debug) {
			System.err.println(
					"File path: " + filePath + "\n" + "Data key: " + dataKey + "\n"
							+ "Data Columns: " + Arrays.deepToString(columns.toArray()));
		}

		JSONObject obj = new JSONObject();
		List<Object[]> testData = new ArrayList<>();
		List<Object> testDataRow = new LinkedList<>();
		List<String> hashes = new ArrayList<>();

		JSONArray rows = new JSONArray();

		// NOTE: some code here is JSON-20080701 legacy
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(filePath));
			obj = new JSONObject(new String(encoded, Charset.forName(encoding)));
		} catch (org.json.JSONException e) {
			System.err.println("Exception (ignored) : " + e.toString());
		} catch (IOException e) {
			System.err.println("Exception (ignored) : " + e.toString());
		}

		Assert.assertTrue("Verifying presence of " + dataKey, obj.has(dataKey));
		String dataString = null;
		try {
			dataString = obj.getString(dataKey);
			if (debug) {
				System.err.println(
						"Loaded data as string for key: " + dataKey + " as " + dataString);
			}
		} catch (org.json.JSONException e) {
			// JSON-20170516 and later
			// org.json.JSONException: JSONObject["datakey"] not a string.
			System.err.println("Exception (ignored) : " + e.toString());
		}

		if (dataString != null) {
			try {
				rows = new JSONArray(dataString);
			} catch (org.json.JSONException e) {
				System.err.println("Exception (ignored) : " + e.toString());
			}
		} else {
			try {
				rows = obj.getJSONArray(dataKey);
				if (debug) {
					System.err.println("Loaded data rows for key: " + dataKey + " "
							+ rows.length() + " rows.");
				}
			} catch (org.json.JSONException e) {
				System.err.println("Exception (ignored) : " + e.toString());
			}
		}
		for (int i = 0; i < rows.length(); i++) {
			try {
				String entry = rows.getString(i);
				hashes.add(entry);
			} catch (org.json.JSONException e) {
				System.err.println("Exception (ignored) : " + e.toString());
			}
		}
		Assert.assertTrue("Verified the data is not empty", hashes.size() > 0);

		String firstRow = hashes.get(0);

		// NOTE: apparently after invoking org.json.JSON library the order of keys
		// inside the firstRow can be non-deterministic
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
			// actualColumns is ignored
			for (String column : columns) {
				testDataRow.add(entryObj.get(column).toString());
			}
			testData.add(testDataRow.toArray());

			/*
			@SuppressWarnings("unchecked")
			Iterator<String> entryKeyIterator = entryObj.keys();
			
			while (entryKeyIterator.hasNext()) {
				String entryKey = entryKeyIterator.next();
				String entryData = entryObj.get(entryKey).toString();
				// System.err.println(entryKey + " = " + entryData);
				switch (entryKey) {
				case "keyword":
					search_keyword = entryData;
					break;
				case "count":
					expected_count = Double.valueOf(entryData);
					break;
				}
			}
			testData.add(new Object[] { search_keyword, expected_count });
			*/
		}

		Object[][] testDataArray = new Object[testData.size()][];
		testData.toArray(testDataArray);
		return testDataArray;
	}
}
