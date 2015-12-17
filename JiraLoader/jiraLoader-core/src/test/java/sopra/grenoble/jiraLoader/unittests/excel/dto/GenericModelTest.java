package sopra.grenoble.jiraLoader.unittests.excel.dto;

import static org.junit.Assert.*;

import org.junit.Test;

import sopra.grenoble.jiraLoader.excel.dto.GenericModel;
import sopra.grenoble.jiraLoader.excel.dto.Version;

public class GenericModelTest {

	@Test
	public void testToStrings() {
		Version v = new Version();
		String toString = v.toString();
		System.out.println(toString);
	}

}
