package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types;

public enum AccountType {

	ENABLE			(	"enable",			"account.Enable",		"Enable"),
	LABEL			(	"label", 			"account.Label",		"Label"),
	DISPLAY_NAME	(	"display_name",		"account.DisplayName",	"DisplayName"),
	USER_NAME		(	"user_name",		"account.UserName",		"UserName"),
	AUTH_NAME		(	"auth_name",		"account.AuthName",		"AuthName"),
	PASSWORD		(	"password",			"account.password",		"password");
	
	private final String cfgType;
	//Used for xml T3X type
	private final String xmlType;
	//Used for xml T2X type
	private final String parameter;
	
	private AccountType(String cfgType, String xmlType, String parameter) {
		this.cfgType = cfgType;
		this.xmlType = xmlType;
		this.parameter = parameter;
	}
	
	public String getCfgType() {
		return cfgType;
	}
	
	public String getXmlType() {
		return xmlType;
	}
	
	public String getParameter() {
		return parameter;
	}
	
	public static AccountType fromCfgType(String cfgType) {
		if(cfgType == null)
			return null;
		
		for(AccountType accountType: AccountType.values()) {
			if(accountType.getCfgType().equals(cfgType))
				return accountType;
		}
		
		return null;
	}
	
	public static AccountType fromXmlType(String xmlType) {
		if(xmlType == null)
			return null;
		
		for(AccountType accountType: AccountType.values()) {
			if(accountType.getXmlType().equals(xmlType))
				return accountType;
		}
		
		return null;
	}
	
	public static AccountType fromParameter(String parameter) {
		if(parameter == null)
			return null;
		
		for(AccountType accountType: AccountType.values()) {
			if(accountType.getParameter().equals(parameter))
				return accountType;
		}
		
		return null;
	}
}
