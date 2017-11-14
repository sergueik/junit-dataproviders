package junitparams;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import junitparams.custom.CustomParameters;
import junitparams.custom.ParametersProvider;
import junitparams.custom.FileParametersProvider;
import junitparams.mappers.CsvWithHeaderMapper;
import junitparams.mappers.DataMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.lang.StringUtils;

//OLE2 Office Documents
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.util.CellReference;

//Office 2007+ XML
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jopendocument.dom.ODValueType;
import org.jopendocument.dom.spreadsheet.Cell;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

@RunWith(JUnitParamsRunner.class)
public class FileParamsTest {

	@Ignore
	@Test
	@FileParameters("src/test/resources/test.csv")
	public void loadParamsFromFileWithIdentityMapper(int age, String name) {
		assertThat(age).isGreaterThan(0);
	}

	@Ignore
	@Test
	@FileParameters(value = "src/test/resources/test.csv", mapper = PersonMapper.class)
	public void loadParamsFromFileWithCustomMapper(Person person) {
		assertThat(person.getAge()).isGreaterThan(0);
	}

	@Ignore
	@Test
	@FileParameters("classpath:test.csv")
	public void loadParamsFromFileAtClasspath(int age, String name) {
		assertThat(age).isGreaterThan(0);
	}

	@Ignore
	@Test
	@FileParameters("file:src/test/resources/test.csv")
	public void loadParamsFromFileAtFilesystem(int age, String name) {
		assertThat(age).isGreaterThan(0);
	}

	@Ignore
	@Test
	@ExcelParameters(filepath = "classpath:data_2007.xlsx", type = "Excel 2007")
	public void loadParamsFromEmbeddedExcel2007(String keyword, double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		 */
	}

	@Ignore
	@Test
	@ExcelParameters(filepath = "classpath:data_2003.xls", type = "Excel 2003")
	public void loadParamsFromEmbeddedExcel2003(String keyword, double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		 */
	}

	@Ignore
	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data_2007.xlsx", type = "Excel 2007")
	public void loadParamsFromFileExcel2007(String keyword, double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		 */
	}

	@Ignore
	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data_2003.xls", type = "Excel 2003")
	public void loadParamsFromFileExcel2003(String keyword, double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
		 */
	}

	@Test
	@ExcelParameters(filepath = "file:src/test/resources/data.ods", type = "OpenOffice Spreadsheet")
	public void loadParamsFromFileOpenOfficeSpreadsheel(String keyword,
			double count) {
		assumeTrue("search", keyword.matches("(?:junit|testng|spock)"));
		assertThat((int) count).isGreaterThan(0);
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@CustomParameters(provider = ExcelParametersProvider.class)
	public @interface ExcelParameters {
		String filepath();

		String type();
	}

	public static class ExcelParametersProvider
			implements ParametersProvider<ExcelParameters> {

		private String filepath;
		private String filename;
		private String protocol;
		private String type;

		@Override
		public void initialize(ExcelParameters parametersAnnotation,
				FrameworkMethod frameworkMethod) {
			filepath = parametersAnnotation.filepath();
			type = parametersAnnotation.type();
			protocol = filepath.substring(0, filepath.indexOf(':'));
			filename = filepath.substring(filepath.indexOf(':') + 1);
		}

		@Override
		public Object[] getParameters() {
			return paramsFromFile();
		}

		private Object[] map(InputStream inputStream) {
			switch (type) {
			case "Excel 2007":
				return createDataFromExcel2007(inputStream);
			case "Excel 2003":
				return createDataFromExcel2003(inputStream);
			case "OpenOffice Spreadsheet":
				return createDataFromOpenOfficeSpreadsheet(inputStream);
			default:
				throw new RuntimeException("wrong format");
			}
		}

		private Object[] createDataFromOpenOfficeSpreadsheet(
				InputStream inputStream) {

			// ?
			try {
				inputStream.close();
			} catch (IOException e) {
				// ignore
			}
			HashMap<String, String> columns = new HashMap<>();
			List<Object[]> result = new ArrayList<>();
			Object[] resultRow = {};

			Sheet sheet;

			String search_keyword = "";
			double expected_count = 0;
			int id = 0;

			try {
				File file = new File(filename);
				sheet = SpreadSheet.createFromFile(file).getFirstSheet();
				// System.err.println("Sheet name: " + sheet.getName());
				// String sheetName = "Employee Data";
				// sheet = SpreadSheet.createFromFile(file).getSheet(sheetName);
				int nColCount = sheet.getColumnCount();
				int nRowCount = sheet.getRowCount();
				@SuppressWarnings("rawtypes")
				Cell cell = null;
				for (int nColIndex = 0; nColIndex < nColCount; nColIndex++) {
					String header = sheet.getImmutableCellAt(nColIndex, 0).getValue()
							.toString();
					if (StringUtils.isBlank(header)) {
						break;
					}
					String column = CellReference.convertNumToColString(nColIndex);
					/*
					System.err.println(nColIndex + " = " + column + " " + header);
					*/
					columns.put(column, header);

				}
				// often there may be no ranges defined
				Set<String> rangeeNames = sheet.getRangesNames();
				Iterator<String> rangeNamesIterator = rangeeNames.iterator();

				while (rangeNamesIterator.hasNext()) {
					System.err.println("Range = " + rangeNamesIterator.next());
				}
				// isCellBlank has protected access in Table
				for (int nRowIndex = 1; nRowIndex < nRowCount
						&& StringUtils.isNotBlank(sheet.getImmutableCellAt(0, nRowIndex)
								.getValue().toString()); nRowIndex++) {
					for (int nColIndex = 0; nColIndex < nColCount && StringUtils
							.isNotBlank(sheet.getImmutableCellAt(nColIndex, nRowIndex)
									.getValue().toString()); nColIndex++) {
						cell = sheet.getImmutableCellAt(nColIndex, nRowIndex);
						String cellName = CellReference.convertNumToColString(nColIndex);
						if (columns.get(cellName).equals("COUNT")) {
							assertEquals(cell.getValueType(), ODValueType.FLOAT);
							expected_count = Double.valueOf(cell.getValue().toString());
						}
						if (columns.get(cellName).equals("SEARCH")) {
							assertEquals(cell.getValueType(), ODValueType.STRING);
							search_keyword = cell.getTextValue();
						}
						if (columns.get(cellName).equals("ID")) {
							/*
							System.err.println("Column: " + columns.get(cellName));
							*/
							assertEquals(cell.getValueType(), ODValueType.FLOAT);
							id = Integer.decode(cell.getValue().toString());
						}
					}
					/*
					System.err.println(String.format(
							"Row ID:%d\tSearch term:'%s'\tExpected minimum link count:%d", id,
							search_keyword, (int) expected_count));
							*/
					resultRow = new Object[] { search_keyword, expected_count };
					result.add(resultRow);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			Object[][] resultArray = new Object[result.size()][];
			result.toArray(resultArray);
			return resultArray;
		}

		private Object[] createDataFromExcel2003(InputStream inputStream) {
			List<Object[]> result = new LinkedList<>();
			Object[] resultRow = {};

			// String fileName = "data_2003.xls";
			HSSFWorkbook wb = null;
			try {
				wb = new HSSFWorkbook(inputStream);

				HSSFSheet sheet = wb.getSheetAt(0);
				// String sheetName = "Employee Data";
				// HSSFSheet sheet = wb.getSheet(sheetName);
				HSSFRow row;
				HSSFCell cell;

				String search_keyword = "";
				double expected_count = 0;

				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext()) {
					row = (HSSFRow) rows.next();
					if (row.getRowNum() == 0) { // ignore the header
						continue;
					}
					Iterator<org.apache.poi.ss.usermodel.Cell> cells = row.cellIterator();
					while (cells.hasNext()) {
						cell = (HSSFCell) cells.next();
						if (cell.getColumnIndex() == 2) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								expected_count = cell.getNumericCellValue();
							} else {
								expected_count = 0;
							}
						}
						if (cell.getColumnIndex() == 1) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
								search_keyword = cell.getStringCellValue();
							} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								search_keyword = Double.toString(cell.getNumericCellValue());
							} else {
								// TODO: Boolean, Formula, Errors
								search_keyword = "";
							}
						}
					}
					resultRow = new Object[] { search_keyword, expected_count };
					result.add(resultRow);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (wb != null) {
					try {
						wb.close();
					} catch (IOException e) {
					}
				}
			}
			/*
			Object[][] resultArray = new Object[result.size()][];
			result.toArray(resultArray);
			return resultArray;
			*/
			return result.toArray();
		}

		private Object[] createDataFromExcel2007(InputStream inputStream) {
			List<Object[]> result = new LinkedList<>();
			HashMap<String, String> columns = new HashMap<>();
			Object[] resultRow = {};
			XSSFWorkbook wb = null;
			try {

				wb = new XSSFWorkbook(inputStream);

				XSSFSheet sheet = wb.getSheetAt(0);
				// String sheetName = sheet.getSheetName();
				// String sheetName = "Employee Data";
				// XSSFSheet sheet = wb.getSheet(sheetName);
				XSSFRow row;
				XSSFCell cell;
				int cellIndex = 0;
				String cellColumn = "";
				String keyword = "";
				double count = 0;
				int id = 0;
				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext()) {
					row = (XSSFRow) rows.next();

					if (row.getRowNum() == 0) {
						// skip the header
						Iterator<org.apache.poi.ss.usermodel.Cell> cells = row
								.cellIterator();
						while (cells.hasNext()) {
							cell = (XSSFCell) cells.next();
							String dataHeader = cell.getStringCellValue();
							cellIndex = cell.getColumnIndex();
							cellColumn = CellReference.convertNumToColString(cellIndex);
							/*
							System.err
									.println(cellIndex + " = " + cellColumn + " " + dataHeader);
									*/
							columns.put(cellColumn, dataHeader);
						}
						continue;
					}
					Iterator<org.apache.poi.ss.usermodel.Cell> cells = row.cellIterator();
					while (cells.hasNext()) {
						cell = (XSSFCell) cells.next();
						cellColumn = CellReference
								.convertNumToColString(cell.getColumnIndex());
						if (columns.get(cellColumn).equals("ID")) {
							assertEquals(cell.getCellType(), XSSFCell.CELL_TYPE_NUMERIC);
							id = (int) cell.getNumericCellValue();
						}
						if (cellColumn.equals("B")) {
							assertEquals(cell.getCellType(), XSSFCell.CELL_TYPE_STRING);
							keyword = cell.getStringCellValue();
						}
						if (cellColumn.equals("C")) {
							assertEquals(cell.getCellType(), XSSFCell.CELL_TYPE_NUMERIC);
							count = cell.getNumericCellValue();
						}
					}
					/* System.err.println(String.format(
							"Loading row ID:%d\tSearch keyword:'%s'\tExpected minimum link count:%d",
							id, keyword, (int) count));
					    */
					resultRow = new Object[] { keyword, count };
					result.add(resultRow);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (wb != null) {
					try {
						wb.close();
					} catch (IOException e) {
					}
				}
			}
			return result.toArray();
		}

		private Object[] paramsFromFile() {
			try {
				InputStream inputStream = createProperReader();
				try {
					return map(inputStream);
				} finally {
					inputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(
						"Could not successfully read parameters from file: " + filepath, e);
			}
		}

		private InputStream createProperReader() throws IOException {

			// TODO: parameter for sheeet name
			// String encoding = fileParameters.encoding();

			// System.err.println("createProperReader: " + filepath);
			if (filepath.indexOf(':') < 0) {
				return new FileInputStream(filepath);
			}

			if ("classpath".equals(protocol)) {
				return getClass().getClassLoader().getResourceAsStream(filename);
			} else if ("file".equals(protocol)) {
				return new FileInputStream(filename);
			}

			throw new IllegalArgumentException(
					"Unknown file access protocol. Only 'file' and 'classpath' are supported!");
		}
	}

	@Test
	@FileParameters(value = "classpath:data.json", mapper = JSONMapper.class)
	public void loadParamsFromJSONEmbedded(String keyword, double count) {
		assertThat((int) count).isGreaterThan(0);
		assertTrue(keyword.matches("(?:junit|testng|spock)"));
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
						*/
	}

	@Test
	@FileParameters(value = "file:src/test/resources/data.json", mapper = JSONMapper.class)
	public void loadParamsFromJSONFile(String keyword, double count) {
		assertTrue(keyword.matches("(?:junit|testng|spock)"));
		assertThat(keyword, notNullValue());
		/*
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
						*/
	}

	@Ignore
	@Test
	@FileParameters(value = "classpath:with_header.csv", mapper = CsvWithHeaderMapper.class)
	public void csvWithHeader(int id, String name) {
		assertThat(id).isGreaterThan(0);
	}

	/*
		@Test
		@FileParameters(value = "classpath:with_special_chars.csv", encoding = "UTF-8")
		public void loadParamWithCorrectEncoding(String value) {
			assertThat(value).isEqualTo("åäöÅÄÖ");
		}
	
		@Test
		@FileParameters(value = "classpath:with_special_chars.csv", encoding = "ISO-8859-1")
		public void loadParamWithWrongEncoding(String value) {
			assertThat(value).isNotEqualTo("åäöÅÄÖ");
		}
	
		@Test
		@FileParameters(value = "src/test/resources/ISO-8859-1.csv", encoding = "iso-8859-1")
		public void loadFromAnsi(String fromFile) throws IOException {
			String expectedLine = firstLineFromFile("src/test/resources/ISO-8859-1.csv",
					"iso-8859-1");
	
			assertThat(fromFile.getBytes("iso-8859-1"))
					.isEqualTo(expectedLine.getBytes("iso-8859-1"));
		}
	
		@Test
		@FileParameters(value = "src/test/resources/x-UTF-16LE-BOM.csv", encoding = "utf-16le")
		public void loadFromUtf16Le(String fromFile) throws IOException {
			String expectedLine = firstLineFromFile(
					"src/test/resources/x-UTF-16LE-BOM.csv", "utf-16le");
	
			assertThat(fromFile.getBytes("utf-16le"))
					.isEqualTo(expectedLine.getBytes("utf-16le"));
		}
	
		private String firstLineFromFile(String filePath, String encoding)
				throws FileNotFoundException {
			Scanner scanner = new Scanner(new InputStreamReader(
					new FileInputStream(filePath), Charset.forName(encoding)));
			return scanner.nextLine();
		}
	*/
	public static class Person {

		private String name;
		private int age;

		public Person(Integer age) {
			this.age = age;
		}

		public Person(String name, Integer age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public boolean isAdult() {
			return age >= 18;
		}

		public int getAge() {
			return age;
		}

		@Override
		public String toString() {
			return "Person of age: " + age;
		}
	}

	public static class PersonMapper extends CsvWithHeaderMapper {
		@Override
		public Object[] map(Reader reader) {
			Object[] map = super.map(reader);
			List<Object[]> result = new LinkedList<>();
			for (Object lineObj : map) {
				String line = (String) lineObj;
				result.add(new Object[] { line.substring(2),
						Integer.parseInt(line.substring(0, 1)) });
			}
			return result.toArray();
		}
	}

	public static class JSONMapper implements DataMapper {
		@Override
		public Object[] map(Reader reader) {

			List<Object[]> result = new LinkedList<>();
			String rawData = "{}";
			JSONObject allTestData = new JSONObject();
			JSONArray rows = new JSONArray();
			String testName = "test";

			try {
				List<String> lines = new LinkedList<>();
				BufferedReader br = new BufferedReader(reader);
				String line;
				while ((line = br.readLine()) != null) {
					lines.add(line);
				}
				String[] data = new String[lines.size()];
				lines.toArray(data);
				rawData = String.join("\n", data);
				// System.err.println("Read rawdata: " + rawData);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			try {
				ArrayList<String> hashes = new ArrayList<>();
				String keyword = "";
				double count = 0;

				allTestData = new JSONObject(rawData);

				assertTrue(allTestData.has(testName));
				String dataString = allTestData.getString(testName);

				rows = new JSONArray(dataString);
				for (int i = 0; i < rows.length(); i++) {
					hashes.add(rows.getString(i));
				}
				assertTrue(hashes.size() > 0);
				for (String entry : hashes) {
					JSONObject entryObj = new JSONObject();
					entryObj = new JSONObject(entry);
					@SuppressWarnings("unchecked")
					Iterator<String> entryKeyIterator = entryObj.keys();

					while (entryKeyIterator.hasNext()) {
						String entryKey = entryKeyIterator.next();
						String entryData = entryObj.get(entryKey).toString();
						switch (entryKey) {
						case "keyword":
							keyword = entryData;
							break;
						case "count":
							count = Double.valueOf(entryData);
							break;
						}
					}
					// System.err.println("keyword: " + keyword);
					// System.err.println(String.format("count: %.2f", count));
					result.add(new Object[] { keyword, count });
				}
			} catch (org.json.JSONException e) {
				e.printStackTrace();
			}
			return result.toArray();

		}
	}

	@Ignore
	@Test
	@Parameters(source = ParamSetProvider.class)
	public void paramSetFromClassTest(String str, Integer val) {
		assertThat(str).isEqualTo("a");
		assertThat(val).isEqualTo(1);
	}

	public static class ParamSetProvider {
		public static Object[] provideParamSet() {
			return new Object[] { "a", 1 };
		}
	}

	@Ignore
	@Test
	@CustomParameters(provider = MethodNameReader.class)
	public void getDataMethodName(String name) throws Exception {
		assertThat(name).isEqualTo("getDataMethodName");
	}

	public static class MethodNameReader
			implements ParametersProvider<CustomParameters> {
		private FrameworkMethod frameworkMethod;

		@Override
		public void initialize(CustomParameters parametersAnnotation,
				FrameworkMethod frameworkMethod) {
			this.frameworkMethod = frameworkMethod;
		}

		@Override
		public Object[] getParameters() {
			return new Object[] { frameworkMethod.getName() };
		}
	}
}
