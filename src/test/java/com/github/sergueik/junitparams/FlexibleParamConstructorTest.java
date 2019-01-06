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
 * Example of parameterized JUnit test trying to update the test parameter from inside the 
 * @test or from @Before method, both failing to achieve the immediate goal
 * of dynamically increas the number of test iterations  
 * Does not appear to be supported by @RunWith(Parameterized.class)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
//   
@RunWith(Parameterized.class)
public class FlexibleParamConstructorTest extends DataTest {

	// java.lang.UnsupportedOperationException from testParamData.add(Object[])
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
	private static int flexibleTestPAramDataCount = 1;

	// constructor injection
	public FlexibleParamConstructorTest(double rowNum, String keyword,
			int count) {
		this.rowNum = rowNum;
		this.keyword = keyword;
		this.count = count;
	}

	@Before
	public void beforeEach() {
	}

	@Test
	public void parameterizedTest1() {
		// Modify test param data before test 2
		flexibleTestPAramDataCount++;
		Object[] testParamEntry = new Object[] { (float) flexibleTestPAramDataCount,
				String.format("test param changed in test 1: test%d",
						flexibleTestPAramDataCount),
				100 + flexibleTestPAramDataCount };
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
