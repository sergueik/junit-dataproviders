package com.github.sergueik.junitparams;
/**
 * Copyright 2018 - 2019 Serguei Kouzmine
 */

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import com.github.sergueik.junitparams.DataSourceStatic;

/* Test class parameter injection that is supported by Junit 4
 * out of the box
 * https://github.com/junit-team/junit4/wiki/parameterized-tests
 * but fed from the file via DataSource class
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

@RunWith(Parameterized.class)
public class JSONDataProviderParamPropertiesInjectTest extends DataTest {

	private static String dataFile = "src/test/resources/param_array.json";

	@Parameters
	public static Collection<Object[]> data() {
		DataSourceStatic.setDataFormat("JSON");
		
		DataSourceStatic.setDataFilePath(
				String.format("%s/%s", System.getProperty("user.dir"), dataFile));
		// This input illustrates how the data fields can be read into the object
		// array in a different order than the
		// natural order in the YAML data file
		List<String> columns = new ArrayList<>();
		for (String column : new String[] { "keyword", "count", "row" }) {
			columns.add(column);
		}
		DataSourceStatic.setColumns(columns);
		return DataSourceStatic.getdata();
	}

	// NOTE: the first property annotation is value (0) is default
	// NOTE: not strongly typed
	@Parameter
	public String keyword;
	@Parameter(1)
	public String count;
	@Parameter(2)
	public String rowNum;

	@Test
	public void parameterizedTest() {
		try {
			dataTest(count, keyword);
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("keyword: %s , count : %d ", keyword, count));
		}
	}
}
