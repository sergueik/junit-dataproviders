package com.github.sergueik.junitparams;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

// Boilerplate code for default test class parameter injection supported by Junit 4
// origin: https://github.com/junit-team/junit4/wiki/parameterized-tests
@RunWith(Parameterized.class)
public class ParameterizedConstructorTest extends DataTest {

	private static DataSource dataSource = DataSource.getInstance();
	private static String dataFile = "src/test/resources/data2.json";

	@Parameters
	public static Collection<Object[]> data() {
		dataSource.setDataFile(dataFile);
		return dataSource.getdata();
	}

	private String  rowNum;
	private String keyword;
	private String  count;

	// constructor injection
	// NOTE: with JSON the column order is not predictable - needs to be enforced
//	public ParameterizedConstructorTest(int count, double rowNum,
//			String keyword) {
		public ParameterizedConstructorTest(String  rowNum, String keyword, String count
				) {
		this.rowNum = rowNum;
		this.keyword = keyword;
		this.count = count;
	}

	@Test
	// java.lang.Exception: Method test should have no parameters
	// public void test(double rowNum, String keyword,
	// double count) {
	// java.lang.IllegalArgumentException: wrong number of arguments
	public void parameterizedTest() {
		try {
			dataTest(count, keyword);
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("keyword: %s , count : %d ", keyword, count));
		}
	}
}
