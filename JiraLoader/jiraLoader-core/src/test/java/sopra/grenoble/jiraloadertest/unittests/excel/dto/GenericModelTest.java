package sopra.grenoble.jiraloadertest.unittests.excel.dto;

import org.junit.Test;
import sopra.grenoble.jiraLoader.excel.dto.Version;

public class GenericModelTest {

	@Test
	public void testToStrings() {
		Version v = new Version();
		String toString = v.toString();
		System.out.println(toString);
	}

}
