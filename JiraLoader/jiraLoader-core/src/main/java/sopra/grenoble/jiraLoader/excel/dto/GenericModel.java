package sopra.grenoble.jiraLoader.excel.dto;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cmouilleron
 *
 */
public abstract class GenericModel {
	
	private static final Logger LOG = LoggerFactory.getLogger(GenericModel.class);

	public String key;
	public String typeDemande;
	public String epicName;
	public String versionName;
	public String resume;
	public String descriptif;
	public String priority;
	public String composantName;
	public String estimation;
	
	/**
	 * @param key
	 * @param typeDemande
	 * @param epicName
	 * @param versionName
	 * @param resume
	 * @param descriptif
	 * @param priority
	 * @param composantName
	 * @param estimation
	 */
	public GenericModel(String key, String typeDemande, String epicName, String versionName, String resume,
			String descriptif, String priority, String composantName, String estimation) {
		super();
		this.key = key;
		this.typeDemande = typeDemande;
		this.epicName = epicName;
		this.versionName = versionName;
		this.resume = resume;
		this.descriptif = descriptif;
		this.priority = priority;
		this.composantName = composantName;
		this.estimation = estimation;
	}
	
	/**
	 * Default construtor
	 */
	public GenericModel() {
		super();
	}
	
	/***
	 * Constructor based on {@link Row}
	 * @param row
	 */
	public void loadRow(Row row) {
		this.key = getStringValueFromRow(row,0);
		this.typeDemande = getStringValueFromRow(row,1);
		this.epicName = getStringValueFromRow(row,2);
		this.versionName = getStringValueFromRow(row,3);
		this.resume = getStringValueFromRow(row,4);
		this.descriptif = getStringValueFromRow(row,5);
		this.priority = getStringValueFromRow(row,6);
		this.composantName = getStringValueFromRow(row,7);
		this.estimation = getStringValueFromRow(row,8);
	}
	
	/**
	 * Extract from excel row the string value. If the cell is empty, return null. If the cell contain a "" string, return null
	 * @param row
	 * @param position
	 * @return
	 */
	private String getStringValueFromRow(Row row, int position) {
		Cell cell = row.getCell(position);
		if (cell == null) {
			return null;
		}
		String value = cell.getStringCellValue();
		if (value == null || value.compareTo("") == 0) {
			return null;
		}
		return value;
	}
	
	@Override
	public String toString() {
		ObjectMapper objMap = new ObjectMapper();
		try {
			return objMap.writeValueAsString(this);
		} catch (IOException e) {
			LOG.warn("Unable to parse dto object in JSON", e);
		}
		return "";
	}

	/**
	 * Validate the file
	 * @return
	 */
	public abstract boolean validate();
}
