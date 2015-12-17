package sopra.grenoble.jiraLoader.unittests.excel.dto;

import static org.junit.Assert.*;

import org.junit.Test;

import sopra.grenoble.jiraLoader.excel.dto.Epic;

public class EpicTest {

	@Test
	public void testValidationFailed_SpecialChar() {
		Epic e = new Epic();
		e.composantName = "COMP";
		e.resume = "RESU-ME";
		
		assertFalse(e.validate());
	}
	
	@Test
	public void testValidation_SpecialChar() {
		Epic e = new Epic();
		e.composantName = "COMP";
		e.resume = "RESUME";
		
		assertTrue(e.validate());
	}

}
