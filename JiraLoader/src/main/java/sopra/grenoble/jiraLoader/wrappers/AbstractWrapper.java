package sopra.grenoble.jiraLoader.wrappers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import sopra.grenoble.jiraLoader.JiraUserConfiguration;
import sopra.grenoble.jiraLoader.excel.dto.GenericModel;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.jira.dao.project.IProjectService;

/**
 * @author cmouilleron
 * Generic wrapper class which is used to load {@link GenericModel} classes in JIRA application.
 * This class is an abstract class and must be extended to be used.
 * 
 * This class exposes some generic functions.
 *
 * @param <E> : object which must extend the {@link GenericModel} class.
 */
public abstract class AbstractWrapper<E extends GenericModel> {

	/*
	 * Static logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractWrapper.class);

	/**
	 * The DTO excel model associated to this Wrapper
	 * This object is an instance of the {@link GenericModel} class. 
	 */
	protected E dtoExcelModel;
	
	/**
	 * The Excel row reference
	 */
	private Row excelRow;
	
	@Autowired
	private IProjectService projectService;
	
	@Autowired
	protected JiraUserConfiguration confBean;
	

	/**
	 * Default constructor
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public AbstractWrapper() throws InstantiationException, IllegalAccessException {
		super();
        Class<E> modelClass = getTypeParameterClass();
        LOG.info("Instanciate Wrapper class with DTO => " + modelClass);
        this.dtoExcelModel = (E) modelClass.newInstance();
	}
	
	/**
	 * @return the type of the GenericSubClass
	 */
	@SuppressWarnings("unchecked")
	private Class<E> getTypeParameterClass()
    {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<E>) paramType.getActualTypeArguments()[0];
    }
	
	/**
	 * Function to load data in DTO
	 * @param row
	 * @return 
	 */
	public E loadRow(Row row) {
		this.dtoExcelModel.loadRow(row);
		LOG.debug("Create DTO with following datas " + this.dtoExcelModel.toString());
		this.excelRow = row;
		return this.dtoExcelModel;
	}

	/**
	 * Abstract function to insert the dto object {@link #dtoExcelModel} in JIRA.
	 * @return 
	 * @throws JiraGeneralException
	 */
	protected abstract void insertInJira() throws JiraGeneralException;
	
	/**
	 * Function to validate the dto object. Validations are :
	 * 	<li> validate the mandatory parameters in dto object
	 * 	<li> validate that the {@link GenericModel#composantName} exists in the project.
	 * @return true if the validation is done without error.
	 * @throws JiraGeneralException : if the communication with JIRA is not opened or cannot be done
	 */
	public boolean validateRow() throws JiraGeneralException {
		//validate DTO data
		if (!this.dtoExcelModel.validate()) {
			LOG.info("Line <" + excelRow.getRowNum() + "> is not valid - Missing data in Excel file");
			return false;
		}
		
		//validate JIRA requirements
		if (dtoExcelModel.composantName != null) {
			//composantName must exist in JIRA
			if(!projectService.isComponentNameExistsInProject(confBean.getProjectName(), dtoExcelModel.composantName)) {
				LOG.info("Line <" + excelRow.getRowNum() + "> is not valid - Component name <" + dtoExcelModel.composantName + "> does not exist in JIRA");
				return false;
			}
		}
		LOG.info("Line <" + excelRow.getRowNum() + "> is valid");
		return true;
	}

	/**
	 * It's a create action if dto object has a value in key
	 * @return
	 */
	public boolean isCreateAction() {
		if (this.dtoExcelModel.key != null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Based on the dto, update the row line.
	 * For the moment, just update the key value
	 */
	public void updateRowLineInExcel() {
		Cell c = this.excelRow.getCell(0);
		if (c == null) {
			//create an empty cell
			c = this.excelRow.createCell(0);
		}
		//set the value
		c.setCellValue(dtoExcelModel.key);
	}

	public void createRowInJira() throws JiraGeneralException {
		//call the function to inject excel row in JIRA
		this.insertInJira();
		//update excel file with updated datas
		this.updateRowLineInExcel();
	}
	
	public void updateRowInJira() {
		LOG.info("Line <" + excelRow.getRowNum() + "> is skipped - Update function is not implemented... Maybe in next release");
	}
}
