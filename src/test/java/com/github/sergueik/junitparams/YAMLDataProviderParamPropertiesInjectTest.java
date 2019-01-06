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
 * but fed from the YAML file DataSource class
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

@RunWith(Parameterized.class)
public class YAMLDataProviderParamPropertiesInjectTest extends DataTest {

	private static String dataFile = "src/test/resources/param_arrays.yaml";

	@Parameters
	public static Collection<Object[]> data() {
		DataSourceStatic.setDataFormat("YAML");
		DataSourceStatic.setDebug(true);
		// NOTE: With static class one has to reset the columns
		// otherwise different tests would interfere.
		// This problem would be manifest by the Assertion Error
		// caught by the example test below
		List<String> columns = new ArrayList<>();
		for (String column : new String[] { "row", "keyword", "count" }) {
			columns.add(column);
		}
		DataSourceStatic.setColumns(columns);

		DataSourceStatic.setDataFilePath(
				String.format("%s/%s", System.getProperty("user.dir"), dataFile));
		return DataSourceStatic.getdata();
	}

	// NOTE: the first property annotation is value (0) is default
	// NOTE: not strongly typed
	@Parameter
	public String rowNum;
	@Parameter(1)
	public String keyword;
	@Parameter(2)
	public String count;

	@Test
	public void parameterizedTest() {
		try {
			dataTest(count, keyword);
		} catch (AssertionError e) {
			System.err.println(
					String.format("Assertion Error with keyword: %s , count : %s ",
							keyword.toString(), count.toString()));
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("Illegal State with keyword: %s , count : %s ",
							keyword.toString(), count.toString()));
		}
	}
}
