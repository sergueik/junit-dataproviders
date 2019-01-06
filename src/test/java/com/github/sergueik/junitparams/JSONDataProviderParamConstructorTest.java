package com.github.sergueik.junitparams;
/**
 * Copyright 2018 - 2019 Serguei Kouzmine
 */

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/* JUnit Parameterized test class relying on
 * properties injection 
 * modified to use JSON file to load parameters (singleton class version)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com) 
 */

@RunWith(Parameterized.class)
public class JSONDataProviderParamConstructorTest extends DataTest {

	private static DataSourceSingleton dataSource = DataSourceSingleton
			.getInstance();
	private static String dataFile = "src/test/resources/hash_of_param_arrays.json";

	@Parameters
	public static Collection<Object[]> data() {
		dataSource.setDataFile(dataFile);
		dataSource.setDataFormat("JSON");
		return dataSource.getdata();
	}

	private String rowNum;
	private String keyword;
	private String count;

	// string parameter constructor injection
	public JSONDataProviderParamConstructorTest(String rowNum, String keyword,
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
