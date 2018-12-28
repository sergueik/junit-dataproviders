package com.github.sergueik.junitparams;

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
 * Skeleton class for feeding the data 
 * for a "@Parameter" - annotated 
 * Juit4 test from the file
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DataSource {

	private String dataFile = "";
	private String defaultKey = "row";

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

	public String getDefaultKey() {
		return this.defaultKey;
	}

	public void setDefaultKey(String value) {
		this.defaultKey = value;
	}

	private String filePath = null;
	private static String encoding = "UTF-8";
	private String dataKey = "datakey";
	private List<String> columns = Arrays
			.asList(new String[] { "row", "keyword", "count" });
	private boolean debug = true;

	public void setDebug(boolean value) {
		this.debug = value;
	}

	// TODO: debug if the DataSource has to be a singleton or else
	private static DataSource instance = new DataSource();

	private DataSource() {
	}

	public static DataSource getInstance() {
		return instance;
	}

	// De-serialize the @Parameters collection from the JSON path passed
	// see also
	// https://stackoverflow.com/questions/3763937/gson-and-deserializing-an-array-of-objects-with-arrays-in-it
	// https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects
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

	// NOTE: not currently used.
	public String getScriptContent(String resourceFileName) {
		try {
			if (debug) {
				System.err
						.println("Script contents: " + getResourceURI(resourceFileName));
			}
			final InputStream stream = getResourceStream(resourceFileName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getResourceStream(String resourceFilePath) {
		return this.getClass().getClassLoader()
				.getResourceAsStream(resourceFilePath);
	}

	public String getResourcePath(String resourceFileName) {
		return String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), resourceFileName);
	}

	// NOTE: getResourceURI may not work well with
	// standalone or a web-hosted application
	public String getResourceURI(String resourceFileName) {
		try {
			URI uri = this.getClass().getClassLoader().getResource(resourceFileName)
					.toURI();
			if (debug) {
				System.err.println("Resource URI: " + uri.toString());
			}
			return uri.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	// different method name
	public Object[][] createDataFromJSON() throws org.json.JSONException {

		if (debug) {
			System.err.println("file path: " + filePath);
			System.err.println("data key: " + dataKey);
			System.err.println("columns: " + Arrays.deepToString(columns.toArray()));
		}

		JSONObject obj = new JSONObject();
		List<Object[]> testData = new ArrayList<>();
		List<Object> testDataRow = new LinkedList<>();
		List<String> hashes = new ArrayList<>();

		JSONArray rows = new JSONArray();

		try {
			byte[] encoded = Files.readAllBytes(Paths.get(filePath));
			obj = new JSONObject(new String(encoded, Charset.forName("UTF-8")));
		} catch (org.json.JSONException e) {
			System.err.println("Exception (ignord) : " + e.toString());
		} catch (IOException e) {
			System.err.println("Exception (ignord) : " + e.toString());
		}

		if (debug) {
			System.err.println("Verifying presence of " + dataKey);
		}
		Assert.assertTrue(obj.has(dataKey));
		String dataString = null;
		try {
			dataString = obj.getString(dataKey);
			if (debug) {
				System.err
						.println("Loaded data for key: " + dataKey + " as " + dataString);
			}
		} catch (org.json.JSONException e) {
			// org.json.JSONException: JSONObject["datakey"] not a string.
			System.err.println("Exception (ignord) : " + e.toString());
		}

		// possible org.json.JSONException
		if (dataString != null) {
			try {
				rows = new JSONArray(dataString);
			} catch (org.json.JSONException e) {
				System.err.println("Exception (ignord) : " + e.toString());
			}
		} else {
			try {
				rows = obj.getJSONArray(dataKey);
				if (debug) {
					System.err.println("Loaded data rows for key: " + dataKey + " "
							+ rows.length() + " rows.");
				}
			} catch (org.json.JSONException e) {
				System.err.println("Exception (ignord) : " + e.toString());
			}
		}
		for (int i = 0; i < rows.length(); i++) {
			try {
				String entry = rows.getString(i);
				hashes.add(entry);
			} catch (org.json.JSONException e) {
				System.err.println("Exception (ignord) : " + e.toString());
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
