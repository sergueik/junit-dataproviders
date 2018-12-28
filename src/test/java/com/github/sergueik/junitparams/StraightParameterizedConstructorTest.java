package com.github.sergueik.junitparams;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

// Boilerplate code for "straight junit" test class constructor parameter injection 
// documented in Junit 4 wiki https://github.com/junit-team/junit4/wiki/parameterized-tests
@RunWith(Parameterized.class)
public class StraightParameterizedConstructorTest extends DataTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { 1.0, "junit", 204 },
				{ 2.0, "testng", 51 }, { 3.0, "spock", 28 } });
	}

	private double rowNum;
	private String keyword;
	private int count;

	// constructor injection
	public StraightParameterizedConstructorTest(double rowNum, String keyword, int count) {
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
			dataTest(keyword, count);
		} catch (IllegalStateException e) {
			System.err
					.println(String.format("keyword: %s , count : %d ", keyword, count));
		}
	}
}