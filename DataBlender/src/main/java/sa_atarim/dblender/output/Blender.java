package sa_atarim.dblender.output;
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
import sa_atarim.dblender.Constants;
import sa_atarim.dblender.sheets.SheetModifier;
import sa_atarim.dblender.sheets.XLSFile;
import sa_atarim.dblender.sheets.key_column.KeyTuple;
import sa_atarim.dblender.sheets.key_column.KeyTupleSet;

public class Blender
{
	private static final String NEW_SHEET_NAME = Constants.PROGRAM_NAME + " new sheet";
	
	/**
	 * Blend multiple sheets into one new sheet.
	 * 
	 * @param request - Specifications for the output file
	 * @throws IOException When the new file cannot be created due to bad path.
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
	 * @param request - A specification of the file's properties
	 * @return The newly created file.
	 * @throws IOException When the new file cannot be created due to bad path.
	 */
	private XLSFile createEmptyFile(OutputRequest request) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		workbook.createSheet(NEW_SHEET_NAME);
		String filePath = request.getFilePath();
		FileOutputStream fileOut = new FileOutputStream(filePath);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();
		
		return new XLSFile(filePath);
	}
	
	/**
	 * Duplicate a sheet into a new clean sheet.
	 * Only the specified columns of the origin sheet will be copied,
	 * and their indices might change accordingly.
	 * 
	 * @param origin - The specification of the sheet to copy
	 * @param destination - The sheet to copy into
	 */
	private void duplicate(FileSpecification origin, XLSFile destination) {
		SheetModifier originSheet = origin.getFile().getSheet();
		XSSFSheet originSourceSheet = originSheet.getSource();
		XSSFSheet destSourceSheet = destination.getSheet().getSource();
		int rowCount = originSourceSheet.getPhysicalNumberOfRows();
		int colIndex = 0;
		
		//create the rows in destination
		for (int r = 0; r < rowCount; r++) destSourceSheet.createRow(r);
		
		//set correct view direction of the file
		CTSheetViews originSheetView = originSourceSheet.getCTWorksheet().getSheetViews();
		CTSheetViews destSheetView = destSourceSheet.getCTWorksheet().getSheetViews();
		boolean rightToLeft = originSheetView.getSheetViewArray(0).getRightToLeft();
		destSheetView.getSheetViewArray(0).setRightToLeft(rightToLeft);
		
		//iterate over every column
		for (String column : origin.getColumns()) {
			
			//iterate over every row
			for (int r = 0; r < rowCount; r++) {
				XSSFRow originRow = originSourceSheet.getRow(r);
				XSSFCell originCell = originRow.getCell(originSheet.getColumnIndex(column));
				XSSFRow destRow = destSourceSheet.getRow(r);
				XSSFCell destCell = destRow.createCell(colIndex);
				copyCell(originCell, destCell);
			}
			
			colIndex++;
		}
		
		destination.write();
	}
	
	/**
	 * Integrate an origin sheet into the destination sheet.
	 * Both files must have the key column, so that the integrated sheet looks at the key column
	 * of the destination sheet and matches for its own rows into the correct indices. 
	 * 
	 * @param origin - The origin sheet to integrate (disassembled sheet)
	 * @param destination - The sheet that's being integrated with (base sheet)
	 * @param keyColumn - The column that the integration is based on (both sheets must have it)
	 * @param intersect - True to delete rows where the key value is not common for both sheets
	 */
	private void integrate(FileSpecification origin, XLSFile destination, String keyColumn, boolean intersect) {
		SheetModifier originSheet = origin.getFile().getSheet();
		SheetModifier destSheet = destination.getSheet();
		XSSFSheet originSourceSheet = originSheet.getSource();
		XSSFSheet destSourceSheet = destination.getSheet().getSource();
		List<String> destColumns = Arrays.asList(destSheet.getColumnNames());
		int destNewColIndex = destSourceSheet.getRow(destSheet.headerRowIndex()).getPhysicalNumberOfCells();
		int destKeyColIndex = destSheet.getColumnIndex(keyColumn);
		int originKeyColIndex = originSheet.getColumnIndex(keyColumn);
		KeyTupleSet finalKeyVals;
		
		//retrieve the origin key values as a set
		KeyTupleSet originKeyVals = new KeyTupleSet();
		
		for (int r = originSheet.headerRowIndex() + 1; r < originSourceSheet.getPhysicalNumberOfRows(); r++) {
			XSSFCell keyCell = originSourceSheet.getRow(r).getCell(originKeyColIndex);
			originKeyVals.add(KeyTuple.create(r, keyCell));
		}
		
		//retrieve the destination key values as a set
		KeyTupleSet destKeyVals = new KeyTupleSet();
		
		for (int r = destSheet.headerRowIndex() + 1; r < destSourceSheet.getPhysicalNumberOfRows(); r++) {
			XSSFCell keyCell = destSourceSheet.getRow(r).getCell(destKeyColIndex);
			destKeyVals.add(KeyTuple.create(r, keyCell));
		}
		
		if (intersect) {
			//create an intersection of the two sets
			finalKeyVals = destKeyVals.intersect(originKeyVals);
		}
		else {
			//create a set that's exclusive for the origin key values
			KeyTupleSet exclusiveOriginKeyVals = new KeyTupleSet(originKeyVals);
			Queue<KeyTuple> keysToRemove = new LinkedList<KeyTuple>();
			Stack<KeyTuple> keysStack = new Stack<KeyTuple>();
			
			for (KeyTuple key : destKeyVals) {
				KeyTuple similarValue = exclusiveOriginKeyVals.getSimilarValue(key.value);
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
				KeyTuple exclusiveKeyTuple = exclusiveOriginKeyVals.getSimilarValue(keyValue);
				exclusiveKeyTuple.rowIndex = r;
			}
			
			//use the union of both files' key values
			finalKeyVals = new KeyTupleSet(destKeyVals);
			finalKeyVals.addAll(exclusiveOriginKeyVals);
		}
		
		//iterate over every origin sheet column
		for (String colum : origin.getColumns()) {
			if (destColumns.contains(colum)) continue;
			
			//get the next free column
			int colIndex = destNewColIndex++;
			
			//insert the column name to the destination
			XSSFRow originRow = originSourceSheet.getRow(originSheet.headerRowIndex());
			XSSFCell originCell = originRow.getCell(originSheet.getColumnIndex(colum));
			XSSFRow destRow = destSourceSheet.getRow(destSheet.headerRowIndex());
			XSSFCell destCell = destRow.createCell(colIndex);
			copyCell(originCell, destCell);
			
			//clone the original key values set for each column
			Set<KeyTuple> keyValsClone = new HashSet<KeyTuple>(finalKeyVals);
			
			//iterate over every row of the origin and match it with the destination
			for (int r = originSheet.headerRowIndex() + 1; r < originSourceSheet.getPhysicalNumberOfRows(); r++) {
				int matchingRow = -1;
				originRow = originSourceSheet.getRow(r);
				originCell = originRow.getCell(originKeyColIndex);
				
				//find the matching row in the destination
				for (KeyTuple key : keyValsClone) {
					if (key.valueEquals(originCell.getStringCellValue())) {
						matchingRow = key.rowIndex;
						keyValsClone.remove(key);
						
						//insert the data to the destination
						originCell = originRow.getCell(originSheet.getColumnIndex(colum));
						destRow = destSourceSheet.getRow(matchingRow);
						destCell = destRow.createCell(colIndex);
						copyCell(originCell, destCell);
						break;
					}
				}
			}
		}
		
		if (intersect) {
			//destSheet.deleteRow(4);
			deleteUnnecessaryRows(destSheet, destKeyColIndex, finalKeyVals);
		}
		destination.write();
	}
	
	/**
	 * Copy a cell.
	 * This method keeps the cell's original format.
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
	 * Delete rows that contain key values that are not in the intersected keys set.
	 * 
	 * @param sheet - The destination sheet
	 * @param keyColumnIndex - Index of the key column in the destination sheet
	 * @param intersectedKeys - A set of the intersected key values between all sheets
	 */
	private void deleteUnnecessaryRows(SheetModifier sheet, int keyColumnIndex, KeyTupleSet intersectedKeys) {
		XSSFSheet sourceSheet = sheet.getSource();
		
		for (int i = sheet.headerRowIndex() + 1; i < sourceSheet.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sourceSheet.getRow(i);
			
			if (row != null) {
				XSSFCell cell = row.getCell(keyColumnIndex);
				Object cellValue = SheetModifier.getGenericCellValue(cell);
				
				//delete row
				if (!intersectedKeys.containsValue(cellValue)) {
					sheet.deleteRow(i);
					i--;
				}
			}
		}
	}
}