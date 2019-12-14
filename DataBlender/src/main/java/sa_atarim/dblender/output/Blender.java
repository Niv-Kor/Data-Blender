package sa_atarim.dblender.output;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import sa_atarim.dblender.Constants;
import sa_atarim.dblender.sheets.CellFormat;
import sa_atarim.dblender.sheets.ConstantCell;
import sa_atarim.dblender.sheets.ConstantCellSet;
import sa_atarim.dblender.sheets.SheetModifier;
import sa_atarim.dblender.sheets.XLSFile;

public class Blender
{
	private static final String NEW_SHEET_NAME = Constants.PROGRAM_NAME + " sheet";
	
	/**
	 * Blend multiple sheets into one new sheet.
	 * 
	 * @param request - Specifications for the output file
	 * @throws IOException When the new file cannot be created due to bad path.
	 */
	public void blend(OutputRequest request) throws IOException {
		if (request == null || !request.isValid()) return;
		
		List<FileSpecification> files = request.getFiles();
		String tempFilePath = FileProcessor.createTempFile(NEW_SHEET_NAME);
		File tempFile = new File(tempFilePath);
		XLSFile blendedFile = new XLSFile(tempFilePath);
		String keyColumnName = request.getKeyColumn();
		duplicate(files.get(0), blendedFile);
		
		for (int i = 1; i < files.size(); i++)
			integrate(files.get(i), blendedFile, keyColumnName, request.usesIntersection());
		
		groupSimilarKeys(blendedFile, keyColumnName);
		blendedFile.getSheet().alignCells(Constants.HEADERS_ALIGNMENT, Constants.DATA_ALIGNMENT);
		highlightCruicialCells(blendedFile, keyColumnName, IndexedColors.TEAL,
							   IndexedColors.WHITE, IndexedColors.AQUA);
		
		//close all files
		for (FileSpecification specification : files) specification.getFile().close();
		
		//write to desired path
		File outputFile = new File(request.getFilePath());
		blendedFile.write(outputFile);
		
		//delete temp file and close
		blendedFile.close();
		tempFile.delete();
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
				Row originRow = originSourceSheet.getRow(r);
				Cell originCell = originRow.getCell(originSheet.getColumnIndex(column));
				Row destRow = destSourceSheet.getRow(r);
				Cell destCell = destRow.createCell(colIndex);
				
				if (originCell != null) CellFormat.copyCell(originCell, destCell);
			}
			
			colIndex++;
		}
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
		Sheet originSourceSheet = originSheet.getSource();
		Sheet destSourceSheet = destination.getSheet().getSource();
		List<String> destColumns = Arrays.asList(destSheet.getColumnNames());
		int destNewColIndex = destSourceSheet.getRow(destSheet.getHeaderRowIndex()).getPhysicalNumberOfCells();
		int destKeyColIndex = destSheet.getColumnIndex(keyColumn);
		int originKeyColIndex = originSheet.getColumnIndex(keyColumn);
		ConstantCellSet finalKeyVals;
		
		//retrieve the origin key values as a set
		ConstantCellSet originKeyVals = new ConstantCellSet();
		
		for (int r = originSheet.getHeaderRowIndex() + 1; r < originSourceSheet.getPhysicalNumberOfRows(); r++) {
			Cell keyCell = originSourceSheet.getRow(r).getCell(originKeyColIndex);
			originKeyVals.add(new ConstantCell(r, keyCell));
		}
		
		//retrieve the destination key values as a set
		ConstantCellSet destKeyVals = new ConstantCellSet();
		
		for (int r = destSheet.getHeaderRowIndex() + 1; r < destSourceSheet.getPhysicalNumberOfRows(); r++) {
			Cell keyCell = destSourceSheet.getRow(r).getCell(destKeyColIndex);
			destKeyVals.add(new ConstantCell(r, keyCell));
		}
		
		if (intersect) {
			//create an intersection of the two sets
			finalKeyVals = destKeyVals.intersect(originKeyVals);
		}
		else {
			//create a set that's exclusive for the origin key values
			ConstantCellSet exclusiveOriginKeyVals = new ConstantCellSet(originKeyVals);
			Queue<ConstantCell> keysToRemove = new LinkedList<ConstantCell>();
			Stack<ConstantCell> keysStack = new Stack<ConstantCell>();
			
			for (ConstantCell key : destKeyVals) {
				ConstantCell similarValue = exclusiveOriginKeyVals.getSimilarValue(key.value);
				if (similarValue != null) keysToRemove.add(similarValue);
			}
			
			//remove unnecessary keys
			while (!keysToRemove.isEmpty()) exclusiveOriginKeyVals.remove(keysToRemove.poll());
			keysStack.addAll(exclusiveOriginKeyVals);
			
			//create a new row for each of the exclusive origin key values
			int destNewRowIndex = destSourceSheet.getPhysicalNumberOfRows();
			
			for (int r = destNewRowIndex; r < destNewRowIndex + exclusiveOriginKeyVals.size(); r++) {
				Row destRow = destSourceSheet.createRow(r);
				Cell destCell = destRow.createCell(destKeyColIndex);
				Object keyValue = keysStack.pop().value;
				destCell.setCellValue(String.valueOf(keyValue));
				
				//update new row indexes on the exclusive origin key values
				ConstantCell exclusiveKeyTuple = exclusiveOriginKeyVals.getSimilarValue(keyValue);
				exclusiveKeyTuple.index = r;
			}
			
			//use the union of both files' key values
			finalKeyVals = new ConstantCellSet(destKeyVals);
			finalKeyVals.addAll(exclusiveOriginKeyVals);
		}
		
		//iterate over every origin sheet column
		for (String colum : origin.getColumns()) {
			if (destColumns.contains(colum)) continue;
			
			//get the next free column
			int colIndex = destNewColIndex++;

			//insert the column name to the destination
			Row originRow = originSourceSheet.getRow(originSheet.getHeaderRowIndex());
			Cell originCell = originRow.getCell(originSheet.getColumnIndex(colum));
			Row destRow = destSourceSheet.getRow(destSheet.getHeaderRowIndex());
			Cell destCell = destRow.createCell(colIndex);
			CellFormat.copyCell(originCell, destCell);
			
			//clone the original key values set for each column
			ConstantCellSet keyValsClone = new ConstantCellSet(finalKeyVals);
			
			//iterate over every row of the origin and match it with the destination
			for (int r = originSheet.getHeaderRowIndex() + 1; r < originSourceSheet.getPhysicalNumberOfRows(); r++) {
				int matchingRow = -1;
				originRow = originSourceSheet.getRow(r);
				originCell = originRow.getCell(originKeyColIndex);
				
				//find the matching row in the destination
				String originKeyValue = originCell.getStringCellValue();
				
				for (ConstantCell key : keyValsClone) {
					if (key.toString().equals(originKeyValue)) {
						matchingRow = key.index;
						keyValsClone.remove(key);
						
						//insert the data to the destination
						originCell = originRow.getCell(originSheet.getColumnIndex(colum));
						destRow = destSourceSheet.getRow(matchingRow);
						destCell = destRow.createCell(colIndex);
						CellFormat.copyCell(originCell, destCell);
						break;
					}
				}
			}
		}
		
		if (intersect) deleteUnnecessaryRows(destSheet, destKeyColIndex, finalKeyVals);
	}
	
	/**
	 * Group together rows that contain similar key values.
	 * 
	 * @param file - The file to work on
	 * @param keyColumn - Name of the key column
	 */
	private void groupSimilarKeys(XLSFile file, String keyColumn) {
		SheetModifier sheet = file.getSheet();
		Sheet source = sheet.getSource();
		Map<String, List<ConstantCell[]>> duplicatedRows = new HashMap<String, List<ConstantCell[]>>();
		int columnsAmount = source.getRow(sheet.getHeaderRowIndex()).getPhysicalNumberOfCells();
		
		//find all duplicated rows in the sheet
		for (int r = 0, deletedRows = 0; r < source.getPhysicalNumberOfRows() - deletedRows; r++) {
			Row row = source.getRow(r);
			String keyValue = sheet.cellValueString(row, 0);
			
			//create new entry for the first instance of the key
			if (!duplicatedRows.containsKey(keyValue))
				duplicatedRows.put(keyValue, new ArrayList<ConstantCell[]>());
			
			//seen at least two instances of the same key
			else {
				List<ConstantCell[]> rowsList = duplicatedRows.get(keyValue);
				ConstantCell[] rowBuffer = new ConstantCell[columnsAmount];
				rowsList.add(rowBuffer);
				
				//save the entire row in a buffer
				for (int i = 0; i < rowBuffer.length; i++)
					rowBuffer[i] = new ConstantCell(i, row.getCell(i));
				
				//delete the saved row
				sheet.deleteRow(r);
				deletedRows++;
				r--;
			}
		}
		
		//remove empty entries from the map
		Queue<String> keysToRemove = new LinkedList<String>();
		
		for (String key : duplicatedRows.keySet())
			if (duplicatedRows.get(key).isEmpty()) keysToRemove.add(key);
		
		while (!keysToRemove.isEmpty())
			duplicatedRows.remove(keysToRemove.poll());
		
		//sort duplicated rows under the origin of each
		for (int r = 0; r < source.getPhysicalNumberOfRows(); r++) {
			Row row = source.getRow(r);
			String keyValue = sheet.cellValueString(row, 0);
			
			//insert the row from the buffer
			if (duplicatedRows.containsKey(keyValue)) {
				for (ConstantCell[] rowBuffer : duplicatedRows.get(keyValue))
					sheet.insertRowAfter(rowBuffer, r);
				
				duplicatedRows.remove(keyValue);
			}
		}
	}
	
	/**
	 * Highlight the header of the key column.
	 * 
	 * @param destination - The destination file
	 * @param keyColumn - The name of the key column
	 * @param headerBG - Header highlight color
	 * @param headerFG - Header highlight color
	 * @param dataBG - Data highlight color
	 */
	private void highlightCruicialCells(XLSFile destination, String headerName, IndexedColors headerBG,
								 		IndexedColors headerFG, IndexedColors dataBG) {
		
		SheetModifier sheet = destination.getSheet();
		Sheet source = sheet.getSource();
		Workbook workbook = source.getWorkbook();
		int headerRowIndex = sheet.getHeaderRowIndex();
		int columnIndex = sheet.getColumnIndex(headerName);
		
		//highlight headers
		for (int i = 0; i < sheet.getColumnsAmount(); i++) {
			Row headerRow = source.getRow(headerRowIndex);
			Cell headerCell = headerRow.getCell(i);
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.cloneStyleFrom(headerCell.getCellStyle());
			 
			Font headerFont = workbook.createFont();
		    headerFont.setFontHeightInPoints((short) 11);
		    headerFont.setFontName("Arial");
		    headerFont.setColor(headerFG.getIndex());
		    
		    headerStyle.setFont(headerFont);
			headerStyle.setFillBackgroundColor(headerBG.getIndex());
			headerStyle.setFillForegroundColor(headerBG.getIndex());
			headerStyle.setFillPattern(FillPatternType.LESS_DOTS);
			
			headerCell.setCellStyle(headerStyle);
		}
		
		//highlight column data
		for (int i = headerRowIndex + 1; i < source.getPhysicalNumberOfRows(); i++) {
			Row row = source.getRow(i);
			Cell cell = row.getCell(columnIndex);
			CellStyle dataStyle = source.getWorkbook().createCellStyle();
		    dataStyle.cloneStyleFrom(cell.getCellStyle());
		    
		    dataStyle.setFillBackgroundColor(dataBG.getIndex());
		    dataStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		    dataStyle.setFillPattern(FillPatternType.LESS_DOTS);
		    
		    cell.setCellStyle(dataStyle);
		}
	}
	
	/**
	 * Delete rows that contain key values that are not in the intersected keys set.
	 * 
	 * @param sheet - The destination sheet
	 * @param keyColumnIndex - Index of the key column in the destination sheet
	 * @param intersectedKeys - A set of the intersected key values between all sheets
	 */
	private void deleteUnnecessaryRows(SheetModifier sheet, int keyColumnIndex, ConstantCellSet intersectedKeys) {
		Sheet sourceSheet = sheet.getSource();
		
		for (int i = sheet.getHeaderRowIndex() + 1; i < sourceSheet.getPhysicalNumberOfRows(); i++) {
			Row row = sourceSheet.getRow(i);
			
			if (row != null) {
				Cell cell = row.getCell(keyColumnIndex);
				Object cellValue = CellFormat.getGenericValue(cell);
				
				//delete row
				if (!intersectedKeys.containsValue(cellValue)) {
					sheet.deleteRow(i);
					i--;
				}
			}
		}
	}
}