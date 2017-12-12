package com.github.sergueik.junitparams;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.custom.CustomParameters;

@RunWith(JUnitParamsRunner.class)
@Retention(RetentionPolicy.RUNTIME)
@CustomParameters(provider = ExcelParametersProvider.class)
public @interface ExcelParameters {
	String filepath();

	String sheetName(); // optional ?

	String type();
	// TODO: parameter for sheet name
	// TODO: parameter for column names
}
