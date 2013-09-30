package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types;

public enum ButtonType {
	
	//ENUM				//M7 key suffix		//M2 parameter
	DKTYPE			(	"type", 			"DKtype"		),
	LABEL			(	"label", 			"Label"			),
	LINE			(	"line", 			"Line"			),
	VALUE			(	"value", 			"Value"			),
	PICKUP_VALUE	(	"pickup_value", 	"PickupValue"	),
	HISTORY_TYPE	(	"history_type", 	"HistoryType"	),
	//Dont know if this ones are correct
	XML_PHONEBOOK	(	"xml_phonebook", 	"XMLPhonebook"	),
	SUB_TYPE		(	"sub_type", 		"SubType"		);
	
	private final String cfgType, xmlType;
	
	private ButtonType(String cfgType, String xmlType) {
		this.cfgType = cfgType;
		this.xmlType = xmlType;
	}
	
	public String getCfgType() {
		return cfgType;
	}
	
	public String getXmlType() {
		return xmlType;
	}

	public static ButtonType fromCfgType(String cfgPath) {
		if(cfgPath == null)
			return null;
		
		for(ButtonType bt: ButtonType.values()) {
			if(bt.getCfgType().equals(cfgPath))
				return bt;
		}
		
		return null;
	}
	
	public static ButtonType fromXmlType(String xmlItem) {
		if(xmlItem == null)
			return null;
		
		for(ButtonType bt: ButtonType.values()) {
			if(bt.getXmlType().equals(xmlItem))
				return bt;
		}
		
		return null;
	}
}