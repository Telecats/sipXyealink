package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.writer;

import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.ICFGConfig;

//TODO: cleanup, provide by factory
public interface IConfigWriter {
	
	public static enum WriterType {
		DEFAULT(null),
		XMLT2("xmlT2"),
		XMLT3("xmlT3"),
		CFG("cfg");
		
		private final String type;
		
		private WriterType(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
		
		public static final WriterType fromString(String type) {
			if(type == null || type.isEmpty())
				return WriterType.DEFAULT;
			
			for(WriterType wt: WriterType.values()) {
				if(type.equalsIgnoreCase(wt.getType()))
					return wt;
			}
			return WriterType.DEFAULT;
		}
	}

	ICFGConfig getConfigFor(String key, String value);
	
	String writeConfig(String key, String value);
	
	String writeComment(String comment);
	
	String writeSignature(String signature);
	
	//null, xmlT2, xmlT3
	void setWriterType(String type);
	
	String flush();
}
