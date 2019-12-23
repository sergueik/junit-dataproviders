package com.github.sergueik.junitparams;
/**
 *	 Copyright 2019 Serguei Kouzmine
 */

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.custom.CustomParameters;
import junitparams.custom.ParametersProvider;

/**
 * Sample parameterized JUnit test scenarios annotated for Google Sheet Parameters 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

@RunWith(JUnitParamsRunner.class)
public class GoogleSheetParamsTest extends DataTest {

	private static Map<String, String> env = System.getenv();

	// Detect Travis build
	private static final boolean isCIBuild = (env.containsKey("TRAVIS")
			&& env.get("TRAVIS").equals("true")) ? true : false;

	private static final String SECRET_FILEPATH = "C:/Users/Serguei/.secret/client_secret.json";

	@Test
	@GoogleSheetParameters(applicationName = "Google Sheets Example", filepath = "17ImW6iKSF7g-iMvPzeK4Zai9PV-lLvMsZkl6FEkytRg", sheetName = "", secretFilePath = SECRET_FILEPATH, debug = true)
	public void loadParamsFromGoogleSheet(String strRowNum, String keyword,
			String strCount) {
		dataTest(keyword, Double.parseDouble(strCount));
	}

}
