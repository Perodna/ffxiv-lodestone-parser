package character;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pandore.ffxiv.lodestone.entity.LSFreeCompany;
import com.pandore.ffxiv.lodestone.parser.LodestoneParser;

import exceptions.LodestoneParserException;

public class FreeCompanyParseTest {

	@BeforeClass
	public static void setupProxy() {
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "3129");
	}
	
	@Test
	public void TestParseFreeCompanyByIdWithoutMembers() throws LodestoneParserException {
		LodestoneParser parser = new LodestoneParser();
		
		String fcId = "9237023573225243170"; // Gardiens d'Inwilis
		LSFreeCompany fc = parser.getFreeCompanyByid(fcId, false);
		
		System.out.println(fc.toString());
	}
	
	@Test
	public void TestParseSmallFCWithMembers() throws LodestoneParserException {
		LodestoneParser parser = new LodestoneParser();
		
		String fcId = "9237023573225275675"; // The Sanctum (Kennard's FC, small one with ~20 members -> 1 page)
		LSFreeCompany fc = parser.getFreeCompanyByid(fcId, true);
		
		System.out.println(fc.toString());
	}
	
	@Test
	public void TestParseBigFCWithMembers() throws LodestoneParserException {
		LodestoneParser parser = new LodestoneParser();
		
		String fcId = "9237023573225243170"; // Gardiens d'Inwilis, > 100 members
		LSFreeCompany fc = parser.getFreeCompanyByid(fcId, true);
		
		System.out.println(fc.toString());
	}

}
