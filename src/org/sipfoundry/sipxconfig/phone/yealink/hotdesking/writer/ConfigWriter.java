package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.ConfigConverter;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.DefaultCFGConfig;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.ICFGConfig;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types.ButtonType;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types.PathType;

//TODO: cleanup, splitup(T2X, T3X) and provide by factory
//TODO: document
public class ConfigWriter implements IConfigWriter {

	private static final Log LOG = LogFactory.getLog(ConfigConverter.class);
	
	private WriterType writerType;
	private ConfigConverter converter = new ConfigConverter();
	private String lastPath = null;
	
	public ConfigWriter() {}
	
	public ConfigWriter(WriterType writerType) {
		this.writerType = writerType;
	}
	
	public ConfigWriter(String writerType) {
		setWriterType(writerType);
	}
		
	@Override
	public void setWriterType(String type) {
		writerType = WriterType.fromString(type);
	}
	
	@Override
	public ICFGConfig getConfigFor(String key, String value) {
		return converter.getConfig(key, value);
	}
	
	/*
		<ConfigurationItem setType="config" >
			<Path>/yealink/config/vpPhone/vpPhone.ini</Path>
			<Session>memory10</Session>
			<Parameter>PickupValue</Parameter>
			<Value></Value>
		</ConfigurationItem>
	 */
	
	@Override
	public String writeComment(String comment) {
	    switch(writerType) {
        case DEFAULT:
        case CFG: 
            return "#"+comment;
        case XMLT2:
        case XMLT3:
//            return "<!--\n"+comment+"\n-->";
	    default :
	        return "";
	    }
	}
	
	@Override
	public String writeSignature(String signature) {
	    switch(writerType) {
        case DEFAULT:
        case CFG: 
            return signature;
        case XMLT2:
        case XMLT3:
        default :
            return "";
        }
	}

	@Override
	public String writeConfig(String key, String value) {
		StringBuilder output = new StringBuilder();
		ICFGConfig config = null;
		switch(writerType) {
		case DEFAULT:
		case CFG: 
			output.append(key).append(" = ").append(value);
			break;
		case XMLT2:
			config = getConfigFor(key, value);
			if(config == null)
				return "";
			if(config.hasError()) {
				output.append(getItem(config));
			} else {
				output.append("<ConfigurationItem setType=\"config\">\n");
				output.append(getItem(config)+"\n");
				output.append("</ConfigurationItem>\n");
			}
			break;
		case XMLT3:
			config = getConfigFor(key, value);
			if(config == null)
				return "";
			
			if(LOG.isDebugEnabled())
				LOG.debug( String.format("LastPath = %s :: config.xmlPath = %s", lastPath, config.getXMLPath()) );
			
			if(lastPath == null) {
				//Write path start, write item
				output.append(getPathStart(config));
				output.append(getItem(config));
			} else if(lastPath.equals(config.getXMLPath())) {
				//Write item
				output.append(getItem(config));
			} else {
				//Write path end, write path start, write item
				output.append(getPathEnd());
				output.append(getPathStart(config));
				output.append(getItem(config));
			}
			//if(config.getXMLPath() != null)
			lastPath = config.getXMLPath();
		}
		return output.toString();
	}
	
	
	
	private String getItem(ICFGConfig config) {
		switch(writerType) {
		case XMLT2:
			StringBuilder sb = new StringBuilder();
			if(config.hasError()) {
				if(LOG.isDebugEnabled()) {
					sb.append(String.format("\t<!-- %s -->", config.getXMLItem()));
				} else {
					return "";
				}
			} else {
				sb.append(String.format("\t<Path>%s</Path>\n", config.getXMLPath()));
				sb.append(String.format("\t<Session>%s</Session>\n", config.getSession()));
				sb.append(String.format("\t<Parameter>%s</Parameter>\n", config.getParameter()));
				sb.append(String.format("\t<Value>%s</Value>", config.getValue()));
			}
			return sb.toString();
		case XMLT3:
		default:
			if(config.hasError()) {
				if(LOG.isDebugEnabled()) {
					return String.format("\t<!--<Item>%s = %s</Item>-->", config.getXMLItem(), config.getValue());
				} else {
					return "";
				}
			}
			else {
				return String.format("\t<Item>%s = %s</Item>", config.getXMLItem(), config.getValue());
			}
		}
	}
	
	private String getPathStart(ICFGConfig config) {
		switch(writerType) {
		case XMLT2:
		case XMLT3:
		default:
			return String.format("<Path config=\"%s\">\n", config.getXMLPath());
		}
	}
	
	private String getPathEnd() {
		switch(writerType) {
			case XMLT2:
			case XMLT3:
			default:
				return "</Path>\n";
		}
	}

	public String flush() {
		switch(writerType) {
			case XMLT3:
				return getPathEnd();
			default:
				return "";
		}
	}
	
	
}
