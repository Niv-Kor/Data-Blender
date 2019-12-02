package sa_atarim.dblender.sheets;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import sa_atarim.dblender.output.FileSpecification;
import sa_atarim.dblender.output.OutputRequest;

public class Blender
{
	private static class KeyTuple
	{
		public int rowIndex;
		public Object value;
		
		public static KeyTuple create(int rowIndex, XSSFCell cell) {
			return new KeyTuple(rowIndex, Sheet.getGenericCellValue(cell));
		}
		
		private KeyTuple(int row, Object val) {
			this.rowIndex = row;
			this.value = val;
		}
		
		public boolean valueEquals(Object other) {
			String otherVal = (String) other;
			String thisVal = (String) value;
			return thisVal.equals(otherVal);
		}
	}
	
	private static final String NEW_SHEET_NAME = "Data Blender";
	
	/**
	 * Blend multiple files into one new file.
	 * 
	 * @param request - Specifications for the output file
	 * @throws IOException when the new file cannot be created due to bad path.
	 */
	public void blend(OutputRequest request) throws IOException {
		List<FileSpecification> files = request.getFiles();
		if (files.isEmpty()) return;
		
		XLSFile blendedFile = createEmptyFile(request);
		duplicate(files.get(0), blendedFile);
		
		for (int i = 1; i < files.size(); i++)
			integrate(files.get(i), blendedFile, request.getKeyColumn(), request.usesIntersection());
		
		//close all files
		for (FileSpecification specification : files) specification.getFile().close();
		blendedFile.close();
	}
	
	/**
	 * Create an empty file according to the given specification.
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private XLSFile createEmptyFile(OutputRequest request) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		workbook.createSheet(NEW_SHEET_NAME);
		FileOutputStream fileOut = new FileOutputStream(request.getFullPath());
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();
		
		return new XLSFile(request.getFullPath(), false);
	}
	
	private void duplicate(FileSpecification origin, XLSFile destination) {
		Sheet originSheet = origin.getFile().getSheet();
		XSSFSheet originSourceSheet = originSheet.getSource();
		XSSFSheet destSourceSheet = destination.getSheet().getSource();
		String[] columns = originSheet.getColumnNames();
		int rowCount = originSourceSheet.getPhysicalNumberOfRows();
		
		//create the rows in destination
		for (int r = 0; r < rowCount; r++) destSourceSheet.createRow(r);
		
		//set correct view direction of the file
		CTSheetViews originSheetView = originSourceSheet.getCTWorksheet().getSheetViews();
		CTSheetViews destSheetView = destSourceSheet.getCTWorksheet().getSheetViews();
		boolean rightToLeft = originSheetView.getSheetViewArray(0).getRightToLeft();
		destSheetView.getSheetViewArray(0).setRightToLeft(rightToLeft);
		
		//iterate over every column
		for (int c = 0; c < columns.length; c++) {
			if (!origin.getColumns().contains(columns[c])) continue;
			int colIndex = originSheet.getColumnIndex(columns[c]);
			
			//iterate over every row
			for (int r = 0; r < rowCount; r++) {
				XSSFRow originRow = originSourceSheet.getRow(r);
				XSSFCell originCell = originRow.getCell(colIndex);
				XSSFRow destRow = destSourceSheet.getRow(r);
				XSSFCell destCell = destRow.createCell(colIndex);
				copyCell(originCell, destCell);
			}
		}
		
		destination.write();
	}
	
	@SuppressWarnings("unchecked")
	private void integrate(FileSpecification origin, XLSFile destination, String keyColumn, boolean intersect) {
		Sheet originSheet = origin.getFile().getSheet();
		Sheet destSheet = destination.getSheet();
		XSSFSheet originSourceSheet = originSheet.getSource();
		XSSFSheet destSourceSheet = destination.getSheet().getSource();
		String[] originColumns = originSheet.getColumnNames();
		List<String> destColumns = Arrays.asList(destSheet.getColumnNames());
		int destNewColIndex = destSourceSheet.getRow(destSheet.getFirstRow()).getPhysicalNumberOfCells();
		int destKeyColIndex = destSheet.getColumnIndex(keyColumn);
		int originKeyColIndex = originSheet.getColumnIndex(keyColumn);
		Set<KeyTuple> finalKeyVals;
		
		//retrieve the origin key values as a set
		Set<KeyTuple> originKeyVals = new HashSet<KeyTuple>();
		
		for (int r = originSheet.getFirstRow() + 1; r < originSourceSheet.getPhysicalNumberOfRows(); r++) {
			XSSFCell keyCell = originSourceSheet.getRow(r).getCell(originKeyColIndex);
			originKeyVals.add(KeyTuple.create(r, keyCell));
		}
		
		//retrieve the destination key values as a set
		Set<KeyTuple> destKeyVals = new HashSet<KeyTuple>();
		
		for (int r = destSheet.getFirstRow() + 1; r < destSourceSheet.getPhysicalNumberOfRows(); r++) {
			XSSFCell keyCell = destSourceSheet.getRow(r).getCell(destKeyColIndex);
			destKeyVals.add(KeyTuple.create(r, keyCell));
		}
		
		if (intersect) {
			//create an intersection of the two sets
			finalKeyVals = intersectKeySets(destKeyVals, originKeyVals);
		}
		else {
			//create a set that's exclusive for the origin key values
			Set<KeyTuple> exclusiveOriginKeyVals = new HashSet<KeyTuple>(originKeyVals);
			Queue<KeyTuple> keysToRemove = new LinkedList<KeyTuple>();
			Stack<KeyTuple> keysStack = new Stack<KeyTuple>();
			
			for (KeyTuple key : destKeyVals) {
				KeyTuple similarValue = getSimilarValue(exclusiveOriginKeyVals, key.value);
				if (similarValue != null) keysToRemove.add(similarValue);
			}
			
			//remove unnecessary keys
			while (!keysToRemove.isEmpty()) exclusiveOriginKeyVals.remove(keysToRemove.poll());
			keysStack.addAll(exclusiveOriginKeyVals);
			
			//create a new row for each of the exclusive origin key values
			int destNewRowIndex = destSourceSheet.getPhysicalNumberOfRows();
			for (int r = destNewRowIndex; r < destNewRowIndex + exclusiveOriginKeyVals.size(); r++) {
				XSSFRow destRow = destSourceSheet.createRow(r);
				XSSFCell destCell = destRow.createCell(destKeyColIndex);
				Object keyValue = (String) keysStack.pop().value;
				destCell.setCellValue((String) keyValue);
				
				//update new row indexes on the exclusive origin key values
				KeyTuple exclusiveKeyTuple = getSimilarValue(exclusiveOriginKeyVals, keyValue);
				exclusiveKeyTuple.rowIndex = r;
			}
			
			//use the union of both files' key values
			finalKeyVals = new HashSet<KeyTuple>(destKeyVals);
			finalKeyVals.addAll(exclusiveOriginKeyVals);
		}
		
		//iterate over every origin sheet column
		for (int c = 0; c < originColumns.length; c++) {
			if (!origin.getColumns().contains(originColumns[c])) continue;
			else if (destColumns.contains(originColumns[c])) continue;
			
			//get the next free column
			int colIndex = destNewColIndex++;
			
			//insert the column name to the destination
			XSSFRow originRow = originSourceSheet.getRow(originSheet.getFirstRow());
			XSSFCell originCell = originRow.getCell(originSheet.getColumnIndex(originColumns[c]));
			XSSFRow destRow = destSourceSheet.getRow(destSheet.getFirstRow());
			XSSFCell destCell = destRow.createCell(colIndex);
			copyCell(originCell, destCell);
			
			//clone the original key values set for each column
			Set<KeyTuple> keyValsClone = new HashSet<KeyTuple>(finalKeyVals);
			
			//iterate over every row of the origin and match it with the destination
			for (int r = originSheet.getFirstRow() + 1; r < originSourceSheet.getPhysicalNumberOfRows(); r++) {
				int matchingRow = -1;
				originRow = originSourceSheet.getRow(r);
				originCell = originRow.getCell(originKeyColIndex);
				
				//find the matching row in the destination
				for (KeyTuple key : keyValsClone) {
					if (key.valueEquals(originCell.getStringCellValue())) {
						matchingRow = key.rowIndex;
						keyValsClone.remove(key);
						
						//insert the data to the destination
						originCell = originRow.getCell(c);
						destRow = destSourceSheet.getRow(matchingRow);
						destCell = destRow.createCell(colIndex);
						copyCell(originCell, destCell);
						break;
					}
				}
			}
		}
		
		if (intersect) deleteUnnecessaryRows(destSheet, destKeyColIndex, finalKeyVals);
		destination.write();
	}
	
	/**
	 * Copy a cell.
	 * 
	 * @param origin - The origin cell to copy from
	 * @param dest - The cell to copy the value into
	 */
	private void copyCell(XSSFCell origin, XSSFCell dest) {
		if (origin != null && origin.getCellType() != CellType.BLANK) {
			switch (origin.getCellType()) {
				case NUMERIC: dest.setCellValue(origin.getNumericCellValue()); break;
				case BOOLEAN: dest.setCellValue(origin.getBooleanCellValue()); break;
				default: dest.setCellValue(origin.getStringCellValue());
			}
		}
	}
	
	/**
	 * Create a set of key values that contains an intersection of all given sets.
	 * 
	 * @param base - The base key values set (it contains the important row indexes)
	 * @param integrators - All other key values sets
	 * @return An intersection of all sets.
	 */
	@SuppressWarnings("unchecked")
	private Set<KeyTuple> intersectKeySets(Set<KeyTuple> base, Set<KeyTuple> ... integrators) {
		Set<KeyTuple> intersectionKeyVals = new HashSet<KeyTuple>(base);
		
		for (KeyTuple key1 : base) {
			boolean match = false;
			
			for (Set<KeyTuple> integrator : integrators) {
				for (KeyTuple key2 : integrator) {
					if (key1.valueEquals(key2.value)) {
						match = true;
						break;
					}
				}
			}
			
			//remove key from intersection
			if (!match) intersectionKeyVals.remove(key1);
		}
		
		return intersectionKeyVals;
	}
	
	/**
	 * Delete rows that contain key values that are not in the intersected keys set.
	 * 
	 * @param sheet - The destination sheet
	 * @param keyColumnIndex - Index of the key column in the destination sheet
	 * @param intersectedKeys - A set of the intersected key values between all sheets
	 */
	private void deleteUnnecessaryRows(Sheet sheet, int keyColumnIndex, Set<KeyTuple> intersectedKeys) {
		XSSFSheet sourceSheet = sheet.getSource();
		
		for (int i = sheet.getFirstRow() + 1; i < sourceSheet.getLastRowNum(); i++) {
			XSSFRow row = sourceSheet.getRow(i);
			XSSFCell cell = row.getCell(keyColumnIndex);
			Object cellValue = Sheet.getGenericCellValue(cell);
			
			//delete row
			if (!containsValue(intersectedKeys, cellValue)) {
				sheet.deleteRow(i);
				i--;
			}
		}
	}
	
	/**
	 * @param set - A set of key values
	 * @param value - The value to check
	 * @return True if the set contains the value.
	 */
	private boolean containsValue(Set<KeyTuple> set, Object value) {
		for (KeyTuple key : set)
			if (key.valueEquals(value)) return true;

		return false;
	}
	
	/**
	 * @param set - A set of key values
	 * @param value - The value to retrieve
	 * @return A key tuple from the set that has the same value.
	 */
	private KeyTuple getSimilarValue(Set<KeyTuple> set, Object value) {
		for (KeyTuple key : set)
			if (key.valueEquals(value)) return key;

		return null;
	}
}