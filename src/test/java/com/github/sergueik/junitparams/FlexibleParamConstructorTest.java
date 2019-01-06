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

	private static List<Object[]> testParamData = Arrays.asList(new Object[][] {
			{ 1.0, "junit", 204 }, { 2.0, "testng", 51 }, { 3.0, "spock", 28 } });

	private static ArrayList<Object[]> dummyData = new ArrayList<Object[]>();

	@Parameters
	public static Collection<Object[]> data() {
		return testParamData;
	}

	private double rowNum;
	private String keyword;
	private int count;
	private static int cnt = 42;

	// constructor injection
	public FlexibleParamConstructorTest(double rowNum, String keyword,
			int count) {
		Object[] entry = new Object[] { (float) cnt, String.format("test%d", cnt),
				150 + cnt };
		cnt++;
		System.err.println(
				"Initialize dummy data in constructor: " + entry[1].toString());
		try {
			dummyData.add(entry);
		} catch (UnsupportedOperationException e) {
			System.err.println("Failed to modify dummy data during test.");
			e.printStackTrace();
		}
		this.rowNum = rowNum;
		this.keyword = keyword;
		this.count = count;
	}

	@Before
	public void beforeEach() {
		// Modify dummy data before each test
		cnt++;
		String[] entry = new String[] { String.format("before each test%d", cnt) };
		Object[] testParamEntry = new Object[] { (float) cnt,
				String.format("test param before each test%d", cnt), 199 + cnt };
		try {
			System.err.println("Adding entry to dummy data before each test: "
					+ entry[0].toString());
			dummyData.add(entry);
			System.err.println("Modified dummy data before each test");
		} catch (UnsupportedOperationException e) {
			System.err.println("Failed to modify dummy data before test.");
		}
		// Modify test param data before each test
		try {
			System.err.println("Adding entry to test param data before each test: "
					+ testParamEntry[1].toString());
			testParamData.add(testParamEntry);
			System.err.println("Modified test param data before each test");
		} catch (UnsupportedOperationException e) {
			System.err.println("Failed to modify test param data before test.");
			e.printStackTrace();
			// Throwable.printStackTrace() writes the stack trace to System.err
			// PrintStream.
			// System.setErr(System.out);
			// e.printStackTrace();
		}
	}

	@Test
	public void parameterizedTest1() {
		// Modify dummy data during test
		try {
			Object[] entry = new Object[] { String.format("test%d", cnt) };
			cnt++;
			System.err.println(
					"Adding entry to dummy data during test: " + entry[0].toString());
			dummyData.add(entry);
			System.err.println("Modified dummy data during test");
		} catch (UnsupportedOperationException e) {
			System.err.println("Failed to modify dummy data during test.");
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
