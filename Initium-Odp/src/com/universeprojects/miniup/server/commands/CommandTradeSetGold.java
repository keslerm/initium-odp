package com.universeprojects.miniup.server.commands;

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

public class CommandTradeSetGold extends Command {
	
	public CommandTradeSetGold(HttpServletRequest request, HttpServletResponse response)
	{
		super(request, response);
	}
	
	public void run(Map<String,String> parameters) throws UserErrorMessage {
		
		ODPDBAccess db = getDB();
		CachedDatastoreService ds = getDS();
		String dogecoinStr = parameters.get("amount");
		Long characterId = tryParseId(parameters,"characterId");
		CachedEntity otherCharacter = db.getEntity(KeyFactory.createKey("Character", characterId));
        dogecoinStr = dogecoinStr.replace(",", "");
        Long dogecoin = null;
        if (dogecoinStr!=null)
        {
            dogecoin = Long.parseLong(dogecoinStr);
        }
        
        db.setTradeDogecoin(null, db.getCurrentCharacter(request), dogecoin);
        db.sendNotification(ds,otherCharacter.getKey(),NotificationType.tradeChanged);
       
     
        return;
	}
}