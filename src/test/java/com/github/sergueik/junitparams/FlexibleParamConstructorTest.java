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

/**
 * Failed attempt of update the parameterized JUnit test 
 * test parameter from inside the 
 * @Test or from @Before method, 
 * does not appear to be achieve the immediate goal: 
 * of dynamically increase the number of test iterations  
 * Does not appear to be supported by @RunWith(Parameterized.class)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

@RunWith(Parameterized.class)
public class FlexibleParamConstructorTest extends DataTest {

	// NOTE: java.lang.UnsupportedOperationException 
	// from trying to call an .add(Object[]) on a List<Object[]>
	// private static List<Object[]> testParamData = Arrays.asList(new Object[][]
	// {{ 1.0, "junit", 204 }, { 2.0, "testng", 51 }, { 3.0, "spock", 28 }});

	private static ArrayList<Object[]> testParamData = new ArrayList<Object[]>();

	@Parameters
	public static Collection<Object[]> data() {
		testParamData.add(0, new Object[] { 1.0, "junit", 204 });
		testParamData.add(1, new Object[] { 2.0, "testng", 51 });
		testParamData.add(2, new Object[] { 3.0, "spock", 28 });
		return testParamData;
	}

	private double rowNum;
	private String keyword;
	private int count;
	private static int flexibleTestParamDataCount = 1;

	// this uses the constructor injection
	public FlexibleParamConstructorTest(double rowNum, String keyword,
			int count) {
		this.rowNum = rowNum;
		this.keyword = keyword;
		this.count = count;
	}

	@Before
	// Failing attempt Modify test param data before each test
	public void beforeEach() {
		flexibleTestParamDataCount++;
		Object[] testParamEntry = new Object[] { (float) flexibleTestParamDataCount,
				String.format("test param before each test%d", flexibleTestParamDataCount), 50 + flexibleTestParamDataCount };
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
	// Failing attempt to Modify test param data from one test before the other test
	public void parameterizedTest1() {
		flexibleTestParamDataCount++;
		Object[] testParamEntry = new Object[] { (float) flexibleTestParamDataCount,
				String.format("test param changed in test 1: test%d",
						flexibleTestParamDataCount),
				100 + flexibleTestParamDataCount };
		try {
			System.err.println("Adding entry to test param from the test: "
					+ testParamEntry[1].toString());
			testParamData.add(testParamEntry);
			System.err.println("Modified test param data from the test.");
		} catch (UnsupportedOperationException e) {
			System.err.println("Failed to modify test param data from the test.");
			e.printStackTrace();
		}
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
