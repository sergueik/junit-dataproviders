package com.github.sergueik.junitparams;
/**
 * Copyright 2017-2019 Serguei Kouzmine
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.custom.CustomParameters;

/**
 * @ExcelParameters Annotation interface for ExcelParametersProvider JUnitparams data provider
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

@RunWith(JUnitParamsRunner.class)
@Retention(RetentionPolicy.RUNTIME)
@CustomParameters(provider = ExcelParametersProvider.class)
public @interface ExcelParameters {
	String filepath();
	String sheetName() default "";
	String type();
	boolean loadEmptyColumns() default false;
	// TODO: parameter for column names
	boolean debug() default false;

	// inspired by
	// https://docs.pytest.org/en/latest/fixture.html#parametrizing-fixtures
	// http://software-testing.ru/forum/index.php?/topic/37870-kastomizatciia-parametrizatcii-v-pytest/
	// storing more then one set of tests in one spreadsheet and picking the ones
	// which column is set to a specified value
	String controlColumn() default "";
	String withValue() default "";
}
