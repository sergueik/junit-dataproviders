package com.github.sergueik.junitparams;
/**
 * Copyright 2018 - 2019 Serguei Kouzmine
 */

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/* Test class parameter injection that is supported by Junit 4
 * out of the box
 * https://github.com/junit-team/junit4/wiki/parameterized-tests
 * but fed from the file via DataSource class
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

@RunWith(Parameterized.class)
public class DataProviderClassParameterizedPropertiesInjectionTest
		extends DataTest {

	private static DataSource dataSource = DataSource.getInstance();
	private static String dataFile = "src/test/resources/data2.json";

	@Parameters
	public static Collection<Object[]> data() {
		dataSource.setDataFile(dataFile);
		return dataSource.getdata();
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
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("keyword: %s , count : %d ", keyword, count));
		}
	}
}
