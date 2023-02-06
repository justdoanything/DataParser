package data.util;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelUtil {

	/**
	 * Check cell type and return cell value as String type
	 * @param cell
	 * @return
	 */
	public static String getCellValue(Cell cell) {
		String msg = "";
		if (cell != null) {
			switch (cell.getCellType()) {
			case FORMULA:
				msg = cell.getCellFormula();
				break;
			case NUMERIC:
				msg = cell.getNumericCellValue() == (int) cell.getNumericCellValue() ?
						String.valueOf((int) cell.getNumericCellValue()) : String.valueOf(cell.getNumericCellValue());
				break;
			case STRING:
				msg = cell.getStringCellValue().trim();
				break;
			case BLANK:
				msg = cell.toString();
				break;
			case BOOLEAN:
				msg = String.valueOf(cell.getBooleanCellValue());
				break;
			case ERROR:
				msg = String.valueOf(cell.getErrorCellValue());
				break;
			default:
				msg = "";
				break;
			}
		}
		return msg.trim();
	}

}
