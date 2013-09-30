package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types.AccountType;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types.ButtonType;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types.PathType;

//TODO: cleanup, splitup(T2X, T3X) and provide by factory
//TODO: document
public class ConfigConverter implements IConfigConverter {
	
	private static final Log LOG = LogFactory.getLog(ConfigConverter.class);

	@Override
	public ICFGConfig getConfig(String key, String value) {
		
		Matcher matcher = pDefault.matcher(key);
		if( !matcher.matches() )
			return null;
		
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Matcher matches %d groups.", matcher.groupCount()));
			if( matcher.groupCount() != 4 ) {
				LOG.debug(String.format("Groupcount != 4: Cannot parse this key: (%s). Returning null.", key));
				return null;
			}
		}
		
		String prefix = matcher.group(1);
		int index = Integer.parseInt(matcher.group(2));
		String keyName = matcher.group(3);
		String unparsed = matcher.group(4);
		
		//Cleanup unparsed
		if(unparsed.isEmpty())
			unparsed = null;
		
		if(LOG.isDebugEnabled())
			LOG.debug(String.format("Default config: input:key(%s) parsed: (group1:prefix(%s) group2:index(%d) group3:keyName(%s) group4:unparsed(%s)) value(%s).", key, prefix, index, keyName, unparsed, value));
		
		PathType pathType = PathType.fromCfgType(prefix);
		DefaultCFGConfig config = new DefaultCFGConfig(key, prefix, index, keyName, value, pathType);
		if(pathType == null) {
			if(LOG.isDebugEnabled())
				LOG.debug(String.format("Could not map (%s) to a valid path. Returning null.", prefix));
			return null;
		}
		
		convert(config, unparsed);
		return config;
	}
	
	private Matcher parseUnparsed(String unparsed, Pattern pattern) {
		Matcher matcher = pattern.matcher(unparsed);	
		return matcher; 
	}
	
	private void convert(DefaultCFGConfig config, String unparsed) {		
		StringBuilder xmlItem = new StringBuilder();
		String xmlPath = config.getPathType().getXmlType();
		String session = null, parameter = null;
		
		boolean error = false;
		
		if(LOG.isDebugEnabled())
			LOG.debug(String.format("Switching on: %s", config.getPathType()));
		
		switch(config.getPathType()) {
		//Account
		case ACCOUNT:
			xmlPath = String.format(xmlPath.toString(), config.getIndex() - 1);
			session = config.getPathType().getXmlItemPrefix();
			//Catch things with double keys like:
			//	account.1.nat.stun_port
			//	TODO: Do these need to be implemented?
			if(unparsed == null || unparsed.isEmpty()) {
				if(LOG.isDebugEnabled())
					LOG.debug(String.format("Accounttype (%s) for key (%s).", AccountType.fromCfgType(config.getKey()), config.getKey()));
				
				AccountType accountType = AccountType.fromCfgType(config.getKey());
				if(accountType == null) {
					if(LOG.isDebugEnabled()) {
						String warning = String.format("Not implemented [%s]", config.getKey());
						LOG.debug(warning + ". Setting error to true (output will be commented).");
						xmlItem.append(warning);
					}
					error = true;
				} else {
					parameter = accountType.getParameter();
					xmlItem.append(accountType.getXmlType());
				}
			} else {
				if( LOG.isDebugEnabled() ) {
					String warning = String.format("Not implemented account.x%s [%s]", config.getKey(), unparsed);
					LOG.debug(warning + ". Setting error to true (output will be commented).");
					xmlItem.append(warning);
				}
				error = true;
			}
			break;
		//MemKey, ProgKey, LineKey
		case MEMORY:
		case PROGRAMMABLEKEY:
		case LINEKEY:
			session = config.getPathType().getXmlItemPrefix() + config.getIndex();
			parameter = ButtonType.fromCfgType(config.getKey()).getXmlType();
			//memory, programablekey, linekey
			xmlItem.append(config.getPathType().getXmlItemPrefix()).
					//1. ,n.
					append(config.getIndex()).append('.').
					//Label, DKtype, PickupValue, etc...
					append(ButtonType.fromCfgType(config.getKey()).getXmlType());
			break;
		case EXPANSION:
			xmlPath = String.format(xmlPath.toString(), config.getIndex());
			xmlItem.append(config.getPathType().getXmlItemPrefix());
			
			Matcher matcher = parseUnparsed(unparsed, pExtensionPad);
			if(matcher.matches() && matcher.groupCount() == 2) {
				String keyIndex = matcher.group(1);
				xmlItem.append(keyIndex).append(".");
				session = config.getPathType().getXmlItemPrefix() + keyIndex;
				
				ButtonType buttonType = ButtonType.fromCfgType(matcher.group(2));
				if(buttonType == null) {
					error = true;
					xmlItem.append(String.format("UnimplementedKey[%s]", matcher.group(2)));
				} else {
					parameter = buttonType.getXmlType();
					xmlItem.append(buttonType.getXmlType());
				}
			} else {
				error = true;
				xmlItem.append(String.format("UnimplementedParseFormat [%s]", unparsed));
			}		
			break;
		default:
		}
		config.setXmlPath(xmlPath);
		config.setXmlItem(xmlItem.toString());
		config.setSession(session);
		config.setParameter(parameter);
		config.setError(error);
	}

}
