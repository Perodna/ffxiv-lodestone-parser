package com.pandore.ffxiv.lodestone.parser;

import com.pandore.ffxiv.lodestone.entity.LSCharacter;
import com.pandore.ffxiv.lodestone.entity.LSFreeCompany;

import exceptions.LodestoneParserException;

public class LodestoneParser {
	

	public LSCharacter getCharacterById(String characterId) throws LodestoneParserException {
		String rootUrl = "http://" + URLS.EU;
		CharacterParser charParser = new CharacterParser(rootUrl);
		return charParser.getCharacterById(characterId);
	}
	
	public LSFreeCompany getFreeCompanyByid(String freeCompanyId, boolean parseMembers) throws LodestoneParserException {
		String rootUrl = "http://" + URLS.EU;
		FreeCompanyParser fcParser = new FreeCompanyParser(rootUrl);
		return fcParser.getFreeCompanyById(freeCompanyId, parseMembers);
	}
	
}
