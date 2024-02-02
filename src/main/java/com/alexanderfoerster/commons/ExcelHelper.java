package com.alexanderfoerster.commons;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;


public class ExcelHelper {
	
	// ExcelHelper ist eine reine Methodensammlung und
	// sollte nicht instanziiert werden
	
	private ExcelHelper() {
	}
	
	// Erzeugung Excel-artiger Spaltenbezeichnungen mit Buchstaben (z. B. Spalte 'AM')
	// insbesondere für die Lesbarkeit der Fehlermeldungen
	public static String excelSpaltenBezeichnung(int spaltenNr) {
		// spaltenNr beginnend mit Null
		
		char basis = 'A';
		char zweitesZeichen = (char)((int)basis + spaltenNr % 26);
		
		if( spaltenNr >= 26 ) {
			char erstesZeichen = (char)( (int)basis + (spaltenNr / 26 - 1) );
			return String.format("%c%c", erstesZeichen, zweitesZeichen);
		} else
			return String.format("%c", zweitesZeichen);
	}
	
	// Textzelle lesen
	// liefert Fehler, wenn
	// * Zelle nicht existiert
	// * Zelle nicht vom Typ String ist
	public static String readTextCell(Sheet sheet, int rowNr, int colNr) throws ReadExcelError {
		Row row = sheet.getRow(rowNr);
		if(row == null)
			throw new ReadExcelError("Zeile " + rowNr + " existiert nicht");
		Cell cell = row.getCell(colNr);
		if(cell == null)
			throw new ReadExcelError("Spalte " + excelSpaltenBezeichnung(colNr) + " existiert nicht in Zeile " + (rowNr+1) );
		if(cell.getCellType() != Cell.CELL_TYPE_STRING)
			throw new ReadExcelError("Zelle in Spalte " + excelSpaltenBezeichnung(colNr) + " und Zeile " + (rowNr+1) + " beinhaltet keinen Text");
		return cell.getStringCellValue();
	}
	
	// Textzelle lesen
	// liefert Fehler, wenn
	// * Zelle nicht existiert
	// * Zelle nicht vom Typ String oder Blank ist
	// liefert "", wenn Zelle Blank
	public static String readTextCellOrBlank(Sheet sheet, int rowNr, int colNr) throws ReadExcelError {
		Row row = sheet.getRow(rowNr);
		if(row == null)
			throw new ReadExcelError("Zeile " + rowNr + " existiert nicht");
		Cell cell = row.getCell(colNr);
		if(cell == null)
			throw new ReadExcelError("Spalte " + excelSpaltenBezeichnung(colNr) + " existiert nicht in Zeile " + (rowNr+1));
		if(cell.getCellType() == Cell.CELL_TYPE_STRING)
			return cell.getStringCellValue();
		if(cell.getCellType() == Cell.CELL_TYPE_BLANK)
			return "";
		
		throw new ReadExcelError("Zelle in Spalte " + excelSpaltenBezeichnung(colNr) + " und Zeile " + (rowNr+1) + " beinhaltet keinen Text und ist nicht leer");
		
	}
	
	public static double readNumericCell(Sheet sheet, int rowNr, int colNr) throws ReadExcelError {
		Row row = sheet.getRow(rowNr);
		if(row == null)
			throw new ReadExcelError("Zeile " + rowNr + " existiert nicht");
		Cell cell = row.getCell(colNr);
		if(cell == null)
			throw new ReadExcelError("Spalte " + excelSpaltenBezeichnung(colNr) + " existiert nicht in Zeile " + (rowNr+1));
		if(cell.getCellType() != Cell.CELL_TYPE_NUMERIC)
			throw new ReadExcelError("Zelle in Spalte " + excelSpaltenBezeichnung(colNr) + " und Zeile " + (rowNr+1) + " beinhaltet keine Zahl");
		return cell.getNumericCellValue();
	}
	
	public static double readNumericCellOrBlank(Sheet sheet, int rowNr, int colNr) throws ReadExcelError {
		Row row = sheet.getRow(rowNr);
		if(row == null)
			throw new ReadExcelError("Zeile " + rowNr + " existiert nicht");
		Cell cell = row.getCell(colNr);
		if(cell == null)
			return 0.0;
		if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			return cell.getNumericCellValue();
		if(cell.getCellType() == Cell.CELL_TYPE_BLANK)
			return 0.0;
		throw new ReadExcelError("Lesefehler in Spalte " + excelSpaltenBezeichnung(colNr) + " und Zeile " + (rowNr+1));
	}
	
	public static boolean isCellBlank(Sheet sheet, int rowNr, int colNr) throws ReadExcelError {
		Row row = sheet.getRow(rowNr);
		if(row == null)
			throw new ReadExcelError("Zeile " + rowNr + " existiert nicht");
		Cell cell = row.getCell(colNr);
		if(cell == null)
			throw new ReadExcelError("Spalte " + excelSpaltenBezeichnung(colNr) + " existiert nicht in Zeile " + (rowNr+1));
		return cell.getCellType() == Cell.CELL_TYPE_BLANK;
	}

	public static boolean isCellBlankOrNotExistent(Sheet sheet, int rowNr, int colNr) {
		boolean cellIsBlank = false;
		try {
			cellIsBlank = isCellBlank(sheet, rowNr, colNr);
		} catch(ReadExcelError e) {
			cellIsBlank = true;
		}
		return cellIsBlank;
	}
	
	public static double readNumericCellOrZero(Sheet sheet, int rowNr, int colNr) throws ReadExcelError {
		Row row = sheet.getRow(rowNr);
		if(row == null)
			throw new ReadExcelError("Zeile " + rowNr + " existiert nicht");
		Cell cell = row.getCell(colNr);
		if(cell == null)
			throw new ReadExcelError("Spalte " + excelSpaltenBezeichnung(colNr) + " existiert nicht in Zeile " + (rowNr+1));
		if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			return cell.getNumericCellValue();
		if(cell.getCellType() == Cell.CELL_TYPE_BLANK)
			return 0.0;
		throw new ReadExcelError("Zelle in Spalte " + excelSpaltenBezeichnung(colNr) + " und Zeile " + (rowNr+1) + " beinhaltet keine Zahl und ist nicht leer");
	}
	
	public static double readBuchhaltungsWert(Sheet sheet, int rowNr, int firstColNr) throws ReadExcelError {
		double value = readNumericCellOrZero(sheet, rowNr, firstColNr);
		if(value == 0.0)
			return 0.0;
		String sCol = readTextCellOrBlank(sheet, rowNr, firstColNr+1).trim();
		String hCol = readTextCellOrBlank(sheet, rowNr, firstColNr+2).trim();
		
		//System.out.println("value: " + value + " - sCol: " + sCol + " - hCol: " + hCol);
		
		if( sCol.equalsIgnoreCase("S") && hCol.isEmpty() )
			return -value;
		if( hCol.equalsIgnoreCase("H") && sCol.isEmpty() )
			return value;
		else throw new ReadExcelError("Zelle in Spalte " + excelSpaltenBezeichnung(firstColNr) + " und Zeile " + (rowNr+1) + " konnte S/H nicht eindeutig zugewiesen werden");
	}

}