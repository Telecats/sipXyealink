package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types;

public enum PathType {
	
	//ENUM				//phone.xml prefix		//XML path										//XML item prefix / session	
	ACCOUNT			(	"account", 				"/config/voip/sipAccount%s.cfg", 				"account"				),
	MEMORY			(	"memorykey", 			"/config/vpPhone/vpPhone.ini", 					"memory"		),
	LINEKEY			(	"linekey", 				"/config/vpPhone/vpPhone.ini", 					"memory1"		),
	PROGRAMMABLEKEY	(	"programablekey", 		"/config/vpPhone/vpPhone.ini", 					"programablekey"), //Yes, must be spelled this way
	EXPANSION		(	"expansion_module",		"/config/vpPhone/Ext38_0000000000000%s.cfg", 	"Key"			);
	
	private final String cfgType;
	private final String xmlType;
	private final String xmlItemPrefix;
	
	private PathType(final String cfgType, final String xmlType, final String xmlItemPrefix) {
		this.cfgType = cfgType;
		this.xmlType = xmlType;
		this.xmlItemPrefix = xmlItemPrefix;
	}
	
	public String getCfgType() {
		return cfgType;
	}
	
	public String getXmlType() {
		return xmlType;
	}
	
	public String getXmlItemPrefix() {
		return xmlItemPrefix;
	}
	
	public static PathType fromCfgType(String cfgType) {
		if(cfgType == null)
			return null;
		
		for(PathType t: PathType.values()) {
			if(t.getCfgType().equals(cfgType))
				return t;
		}
		
		return null;
	}
	
	public static PathType fromXmlType(String xmlType) {
		if(xmlType == null)
			return null;
		
		for(PathType t: PathType.values()) {
			if(t.getXmlType().equals(xmlType))
				return t;
		}
		
		return null;
	}
}
