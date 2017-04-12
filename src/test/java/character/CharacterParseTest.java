package character;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pandore.ffxiv.lodestone.entity.LSCharacter;
import com.pandore.ffxiv.lodestone.parser.LodestoneParser;

public class CharacterParseTest {
	
	@BeforeClass
	public static void setupProxy() {
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "3129");
	}
	
	@Test
	public void TestParseCharacterById() throws Exception {
		LodestoneParser parser = new LodestoneParser();
		
		
//		String characterId = "6033298"; // Myobi
		String characterId = "6304933"; // Vivishu
		
		LSCharacter character = parser.getCharacterById(characterId);
		
		System.out.println(character.printCharacter(true));
	}

}
