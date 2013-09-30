package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion;

import java.util.regex.Pattern;

public interface IConfigConverter {
	
	//linekey 			(linekey.6.line)
	//programablekey 	(programablekey.1.type)
	//memorykey 		(memorykey.1.line)
	//account 			(account.1.enable) (some are different like account.1.nat.nat_traversal)
	public static final Pattern pDefault = Pattern.compile("([a-zA-Z_]*)\\.([0-9]*)\\.([a-zA-Z_]*)(.*)");
	
	//extended account 	(account.1.nat.nat_traversal)
	public static final Pattern pExtAccount = Pattern.compile("([a-zA-Z_]*)");
	
	//Expansion pad unparsed	(.1.type)
	public static final Pattern pExtensionPad = Pattern.compile("\\.([0-9]*)\\.([a-zA-Z_]*)");
	
	public ICFGConfig getConfig(String key, String value);	
	
}
