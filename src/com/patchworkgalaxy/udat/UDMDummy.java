package com.patchworkgalaxy.udat;

import com.patchworkgalaxy.general.util.Utils;
import com.patchworkgalaxy.template.TemplateRegistry;
import static com.patchworkgalaxy.udat.SpecialKeys.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class is entirely a placeholder. Its functionality will be provided by
 * Plex rules once that system is online. In the mean time, it validates the
 * udat values we care about right now.
 */
class UDMDummy {
    
    private UDMDummy() {}
    
    static void dummyset(UserData mutate, String key, String value, UserData requestor) throws UDatException {
	key = key.toLowerCase();
	value = validate(mutate, key, value, requestor);
	mutate.setDatum(key, value);
    }
    
    private static String validate(UserData mutate, String key, String value, UserData requestor) throws UDatException {
	if(key.startsWith(IFF_FRIENDLY) || key.startsWith(IFF_HOSTILE))
	    return value;
	switch(key) {
	case FACTION:
	    validateFaction(value);
	    break;
	case HOST:
	    requireInternal(requestor);
	    validateBoolean(value);
	    break;
	case ALLOWS_SPECTATORS:
	    validateBoolean(value);
	    break;
	case OBSERVER:
	    validateBoolean(value);
	    if(mutate.booleanDatum(HOST))
		throw new UDatException("You can't observe a game you're hosting");
	    break;
	case READY:
	    validateBoolean(value);
	    break;
	case COLOR:
	    validateColor(value);
	    break;
	case WINS:
	case LOSSES:
	case DRAWS:
	    requireInternal(requestor);
	    validateInt(value);
	    break;
	case SQUELCH_UNTIL:
	    requireMod(requestor);
	    if(value.isEmpty() || value.equalsIgnoreCase("false"))
		break;
	    int minutes;
	    try {
		minutes = validateInt(value);
	    }
	    catch(UDatException e) {
		throw new UDatException("The length of a squelch must be an integer number of minutes.");
	    }
	    long squelchends = new Date().getTime() + TimeUnit.MINUTES.convert(minutes, TimeUnit.MILLISECONDS);
	    value = "" + squelchends;
	    break;
	case SUSPEND_UNTIL:
	    requireMod(requestor);
	    if(value.isEmpty() || value.equalsIgnoreCase("false"))
		break;
	    int hours;
	    try {
		hours = validateInt(value);
	    }
	    catch(UDatException e) {
		throw new UDatException("The length of a suspension must be an integer number of hours.");
	    }
	    long suspendends = new Date().getTime() + TimeUnit.HOURS.convert(hours, TimeUnit.MILLISECONDS);
	    value = "" + suspendends;
	    break;
	case MOD_RIGHTS:
	case ADMIN_RIGHTS:
	case BANNED:
	    requireAdmin(requestor);
	    break;
	default:
	    throw new UDatException("Unknown command");
	}
	return value;
    }
    
    private static void validateFaction(String value) throws UDatException {
	if(TemplateRegistry.FACTIONS.lookup(value) == null)
	    throw new UDatException("Unknown faction " + value);
    }
    
    private static void validateBoolean(String value) throws UDatException {
	if(value.length() > 0 && !value.equals("true") && !value.equals("false"))
	    throw new UDatException("Non-boolean value " + value);
    }
    
    private static void validateColor(String value) throws UDatException {
	try {
	    if(value.length() == 8) //we don't want alpha channel in color codes
		throw new IllegalArgumentException();
	    Utils.parseColor(value);
	}
	catch(IllegalArgumentException e) {
	    throw new UDatException("Invalid color " + value + ". Colors should be supplied as three- or six-digit hexidecimal codes with no leading prefix (eg. ff00ff.");
	}
    }
    
    private static int validateInt(String value) throws UDatException {
	try {
	    return Integer.valueOf(value);
	}
	catch(NumberFormatException e) {
	    throw new UDatException("Invalid integer " + value);
	}
    }
    
    private static boolean bool(UserData udat, String key) {
	String datum = udat.getDatum(key);
	if(datum == null || datum.isEmpty() || datum.equalsIgnoreCase("false"))
	    return false;
	else
	    return true;
    }
    
    private static void requireMod(UserData requestor) throws UDatException {
	if(!bool(requestor, MOD_RIGHTS))
	    throw new UDatException("That command requires moderator access or higher");
    }
    
    private static void requireAdmin(UserData requestor) throws UDatException {
	if(!bool(requestor, ADMIN_RIGHTS))
	    throw new UDatException("That command requires administrator access");
    }
    
    private static void requireInternal(UserData requestor) throws UDatException {
	if(requestor != null)
	    throw new UDatException("Unknown command");
    }
    
}
