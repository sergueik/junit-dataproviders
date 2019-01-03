package com.github.sergueik.junitparams;
/**
 * Copyright 2018,2019 Serguei Kouzmine
 */

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;

/**
 * Sampe test exposing the passed parameters for ExcelParametersProvider and JSON  
 * junitparams data provider annotated Juit test scenarios 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DataTest {
	// "strongly" typed method
	protected void dataTest(String keyword, double count) {
		assertThat(keyword, notNullValue());
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		assertThat("search", keyword, anyOf(is("junit"), is("testng"), is("spock"),
				is("whatever"), is("allure"), is("there is no such thing")));
		assertThat((int) count, greaterThan(0));
	}

	// "loosely" typed method
	protected void dataTest(String strCount, String keyword) {
		assertThat(keyword, notNullValue());
		assertThat("search", keyword, anyOf(is("junit"), is("testng"), is("spock"),
				is("whatever"), is("allure"), is("there is no such thing")));
		double count = Double.valueOf(strCount);
		assertThat((int) count, greaterThan(0));
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count: %s",
						keyword, strCount));
	}

}
