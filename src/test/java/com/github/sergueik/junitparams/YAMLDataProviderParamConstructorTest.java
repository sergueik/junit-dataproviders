package com.github.sergueik.junitparams;
/**
 * Copyright 2018 - 2019 Serguei Kouzmine
 */

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class YAMLDataProviderParamConstructorTest extends DataTest {

	private static DataSourceSingleton dataSource = DataSourceSingleton
			.getInstance();
	// YAML loading dataSource methods are not yet supporting schema when
	// parameters are in arrays of hashes hashed by test group
	// nor strongly typed arrays of parameter objects
	private static String dataFile = "src/test/resources/param_arrays.yaml";

	@Parameters
	public static Collection<Object[]> data() {
		dataSource.setDataFile(dataFile);
		dataSource.setDataFormat("YAML");
		return dataSource.getdata();
	}

	private String rowNum;
	private String keyword;
	private String count;

	// string parameter constructor injection
	// NOTE: with JSON the column order is not predictable and
	// is better be enforced through an extra property
	public YAMLDataProviderParamConstructorTest(String rowNum, String keyword,
			String count) {
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
