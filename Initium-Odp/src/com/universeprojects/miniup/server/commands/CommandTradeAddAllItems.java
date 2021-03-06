package com.universeprojects.miniup.server.commands;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.KeyFactory;
import com.universeprojects.cacheddatastore.CachedDatastoreService;
import com.universeprojects.cacheddatastore.CachedEntity;
import com.universeprojects.miniup.server.NotificationType;
import com.universeprojects.miniup.server.ODPDBAccess;
import com.universeprojects.miniup.server.commands.framework.Command;
import com.universeprojects.miniup.server.commands.framework.UserErrorMessage;

public class CommandTradeAddAllItems extends Command {
	
	public CommandTradeAddAllItems(HttpServletRequest request, HttpServletResponse response)
	{
		super(request, response);
	}
	
	public void run(Map<String,String> parameters) throws UserErrorMessage {
		
		ODPDBAccess db = getDB();
		CachedDatastoreService ds = getDS();
		
		CachedEntity character = db.getCurrentCharacter(request);
		List<CachedEntity> items = db.getFilteredList("Item", "containerKey", character.getKey());
		Long characterId = tryParseId(parameters,"characterId");
        CachedEntity otherCharacter = db.getEntity(KeyFactory.createKey("Character", characterId));
        
		for(CachedEntity item:items)
		{
			if (db.checkCharacterHasItemEquipped(character, item.getKey()))
                continue;
            
            if (db.checkItemIsVending(character.getKey(), item.getKey()))
                continue;
		}   
        
		db.addTradeItems(ds, db.getCurrentCharacter(request), items);
        db.sendNotification(ds, otherCharacter.getKey(), NotificationType.tradeChanged);
        
        return;
	}
}