package sopra.grenoble.jiraLoader.excel.loaders;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public class ExcelRowUtils {
	
	private static Optional<Cell> getCell(Row row, int columnPosition) {
		Cell cell = row.getCell(columnPosition);
		if (cell == null) {
			return Optional.empty();
		}
		return Optional.of(cell);
	}
	/**
	 * Extract from excel row the string value. If the cell is empty, return
	 * null. If the cell contain a "" string, return null
	 * 
	 * @param row
	 * @param columnPosition
	 * @return {@link Optional}
	 */
	public static Optional<String> getStringValueFromRow(Row row, int columnPosition) {

		if (columnPosition == -1) {
			return null;
		} else {
			return getCell(row, columnPosition).map((cell) -> {
				return (cell.getStringCellValue().length() != 0) ? cell.getStringCellValue() : null;
			});
		}

	}
	
	/**
	 * Extract from excel row the boolean value. If the cell is empty, return
	 * null. If the cell contain a "" string, return null
	 * 
	 * @param row
	 * @param columnPosition
	 * @return {@link Optional}
	 */
	public static Optional<Boolean> getBooleanValueFromRow(Row row, int columnPosition) {
		Optional<String> result = getStringValueFromRow(row, columnPosition);
		return result.map((res) -> Boolean.parseBoolean(res));
	}
	
	/**
	 * Extract from excel row the Integer value. If the cell is empty, return
	 * null. If the cell contain a "" string, return null
	 * 
	 * @param row
	 * @param columnPosition
	 * @return {@link Optional}
	 */
	public static Optional<Integer> getIntegerValueFromRow(Row row, int columnPosition) {
		return getCell(row, columnPosition).map((cell) -> Double.valueOf(cell.getNumericCellValue()).intValue());
	}
}
