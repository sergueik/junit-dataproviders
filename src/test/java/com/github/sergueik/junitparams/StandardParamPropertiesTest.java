package com.github.sergueik.junitparams;
/**
 * Copyright 2017-2018 Serguei Kouzmine
 */


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

// Boilerplate code for straightforward JUnit test class with propertiesinjection
// documented in Junit 4 wiki https://github.com/junit-team/junit4/wiki/parameterized-tests
@RunWith(Parameterized.class)
public class StandardParamPropertiesTest extends DataTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { 1.0, "junit", 204 },
				{ 2.0, "testng", 51 }, { 3.0, "spock", 28 } });
	}

	// NOTE: the first property annotation is value (0) is default
	@Parameter
	// NOTE: @Parameter-annotated class property has to be public - 
	// will throw exception in runtime
	// java.lang.IllegalAccessException: Class
	// org.junit.runners.parameterized.BlockJUnt4ClassRunnerWithParameters can not
	// access a member of class
	// com.github.serguei.initparams.ParameterizedPropertiesTest with modifiers
	// "protected"
	// protected double rowNum;
	public double rowNum;
	@Parameter(1)
	public String keyword;
	@Parameter(2)
	public int count;

	// NOTE: possible typo in annotation DSL (annotate multiple properties with
	// the same index )
	// @Parameter(1)
	// protected Object foo;
	// @Parameter(1)
	// protected Object bar;
	// is only detected execution time
	// java.lang.Exception: @Parameter(1) is used more than once (2).
	// java.lang.Exception: @Parameter(2) is never used.
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
