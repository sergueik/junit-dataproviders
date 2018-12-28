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

/* Constructor injection Test class parameter 
 * injection supported by Junit 4 
 * https://github.com/junit-team/junit4/wiki/parameterized-tests
 * out of the box
 * but fed from the file via DataSource class
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

@RunWith(Parameterized.class)
public class DataProviderClassParameterizedConstructorTest extends DataTest {

	private static DataSource dataSource = DataSource.getInstance();
	private static String dataFile = "src/test/resources/data2.json";

	@Parameters
	public static Collection<Object[]> data() {
		dataSource.setDataFile(dataFile);
		return dataSource.getdata();
	}

	private String rowNum;
	private String keyword;
	private String count;

	// string parameter constructor injection
	// NOTE: with JSON the column order is not predictable and
	// is better be enforced through an extra property
	public DataProviderClassParameterizedConstructorTest(String rowNum,
			String keyword, String count) {
		this.rowNum = rowNum;
		this.keyword = keyword;
		this.count = count;
	}

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
