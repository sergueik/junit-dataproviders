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

/* Boilerplate code of JUnit Parameterized test class relying on
 * properties injection 
 * based on https://github.com/junit-team/junit4/wiki/parameterized-tests
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com) 
 */

@RunWith(Parameterized.class)
public class StandardParamPropertiesTest extends DataTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { 1.0, "junit", 204 },
				{ 2.0, "testng", 51 }, { 3.0, "spock", 28 } });
	}

	@Parameter
	// NOTE: @Parameter-annotated class property has to be public -
	// if made
	// protected double rowNum;
	// java.lang.IllegalAccessException will be thrown in runtime
	// Class org.junit.runners.parameterized.BlockJUnt4ClassRunnerWithParameters
	// can not access a member of class
	// com.github.serguei.initparams.ParameterizedPropertiesTest
	// with modifiers "protected"
	public double rowNum;
	// NOTE: the first property annotation in the clss definition
	// is value (0) by default
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
	// will be only detected at execution time as
	// java.lang.Exception: @Parameter(1) is used more than once (2).
	// java.lang.Exception: @Parameter(2) is never used.
	@Test
	// NOTE: java.lang.Exception: Method test should have no parameters
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
