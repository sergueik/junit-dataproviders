package com.github.sergueik.junitparams;
/**
 * Copyright 2018 - 2019 Serguei Kouzmine
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.lang.UnsupportedOperationException;

@RunWith(Parameterized.class)
public class FlexibleParamConstructorTest extends DataTest {

	// java.lang.UnsupportedOperationException
	// private static List<Object[]> testParamData = Arrays.asList(new Object[][]
	// {
	// { 1.0, "junit", 204 }, { 2.0, "testng", 51 }, { 3.0, "spock", 28 } });

	private static ArrayList<Object[]> testParamData = new ArrayList<Object[]>();

	@Parameters
	public static Collection<Object[]> data() {
		testParamData.add(0, new Object[] { 1.0, "junit", 204 });
		testParamData.add(1, new Object[] { 2.0, "testng", 51 });
		testParamData.add(2, new Object[] { 3.0, "spock", 28 });
		testParamData.add(2, new Object[] { 3.0, "test0", 28 });
		return testParamData;
	}

	private double rowNum;
	private String keyword;
	private int count;
	private static int flexibleDataCount = 1;

	// constructor injection
	public FlexibleParamConstructorTest(double rowNum, String keyword,
			int count) {
		this.rowNum = rowNum;
		this.keyword = keyword;
		this.count = count;
	}

	@Before
	public void beforeEach() {
		// Modify test param data before each test
		flexibleDataCount++;
		Object[] testParamEntry = new Object[] { (float) flexibleDataCount,
				String.format("test param before each test%d", flexibleDataCount), 199 + flexibleDataCount };
		try {
			System.err.println("Adding entry to test param data before each test: "
					+ testParamEntry[1].toString());
			testParamData.add(testParamEntry);
			System.err.println("Modified test param data before each test");
		} catch (UnsupportedOperationException e) {
			System.err.println("Failed to modify test param data before test.");
			e.printStackTrace();
		}
	}

	@Test
	public void parameterizedTest1() {
		try {
			dataTest(keyword, count);
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("keyword: %s , count : %d ", keyword, count));
		}
	}

	// count the number
	@Test
	public void parameterizedTest2() {
		try {
			dataTest(keyword, count);
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("keyword: %s , count : %d ", keyword, count));
		}
	}
}
