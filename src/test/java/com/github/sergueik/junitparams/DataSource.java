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
	// TODO: debug if DataSource has to be a singleton or else
	private static DataSource instance = new DataSource();

	private DataSource() {
	}

	public static DataSource getInstance() {
		return instance;
	}

	public Collection<Object[]> getdata() {
		// call but not use
		/*
			String payload = "";
			assertFalse(payload.isEmpty());
			Map<String, String> details = new HashMap<>();
			readData(payload, Optional.of(details));
		*/
		try {
			return Arrays.asList(createDataFromJSON());
		} catch (JSONException e) {

			// return new ArrayList<Object[]>();
			return Arrays.asList(new Object[][] { { 1.0, "junit", 204 },
					{ 2.0, "testng", 51 }, { 3.0, "spock", 28 } });
		}
		// ExcelParametersProvider and JSON
	}

	// NOTE: put inside "WEB-INF/classes" for web hosted app
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

	public String readData(Optional<Map<String, String>> parameters) {
		return readData(null, parameters);
	}

	private final String defaultKey = "rowNum";

	// Deserialize the hashmap from the JSON
	// see also
	// https://stackoverflow.com/questions/3763937/gson-and-deserializing-an-array-of-objects-with-arrays-in-it
	// https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects
	public String readData(String payload,
			Optional<Map<String, String>> parameters) {

		Map<String, String> collector = (parameters.isPresent()) ? parameters.get()
				: new HashMap<>();

		String data = (payload == null)
				? "{ \"rowNum\": \"1.0\", \"keyword\": \"testng\", \"count\": \"100\" }"
				: payload;
		try {
			JSONObject elementObj = new JSONObject(data);
			@SuppressWarnings("unchecked")
			Iterator<String> propIterator = elementObj.keys();
			while (propIterator.hasNext()) {
				String propertyKey = propIterator.next();
				String propertyVal = elementObj.getString(propertyKey);
				// logger.info(propertyKey + ": " + propertyVal);
				if (debug) {
					System.err.println("readData: " + propertyKey + ": " + propertyVal);
				}
				collector.put(propertyKey, propertyVal);
			}
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e.toString());
			return null;
		}
		return collector.get(defaultKey);
	}

	// NOTE: getResourceURI may not work with standalone or web hosted
	// application
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

	private static String filePath = String.format("%s/%s",
			System.getProperty("user.dir"), "src/test/resources/data2.json");
	private static String encoding = "UTF-8";
	private static String dataKey = "datakey";
	private static List<String> columns = Arrays
			.asList(new String[] { "row", "keyword", "count" });
	private static boolean debug = true;

	public void setDebug(boolean value) {
		debug = value;
	}

	// different method name
	public static Object[][] createDataFromJSON() throws org.json.JSONException {

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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (debug) {
			System.err.println("Verifying presence of " + dataKey);
		}
		Assert.assertTrue(obj.has(dataKey));
		String dataString = obj.getString(dataKey);

		// possible org.json.JSONException
		try {
			rows = new JSONArray(dataString);
		} catch (org.json.JSONException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < rows.length(); i++) {
			String entry = rows.getString(i);
			hashes.add(entry);
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
