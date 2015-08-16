package character;

import org.junit.Test;

import com.pandore.ffxiv.lodestone.entity.LSCharacter;
import com.pandore.ffxiv.lodestone.parser.LodestoneParser;

public class CharacterParseTest {
	
	@Test
	public void TestParseCharacterById() throws Exception {
		LodestoneParser parser = new LodestoneParser();
		
		
//		String characterId = "6033298"; // Myobi
		String characterId = "6304933"; // Vivishu
		
		LSCharacter character = parser.getCharacterById(characterId);
		
		System.out.println(character.toString());
	}

}
