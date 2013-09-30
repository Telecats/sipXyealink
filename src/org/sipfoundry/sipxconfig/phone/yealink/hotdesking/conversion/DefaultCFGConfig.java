package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion;

import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types.PathType;

//TODO: Splitup(T2X, T3X) and provide by factory
public class DefaultCFGConfig implements ICFGConfig {
	
	private String path;
	private String key;
	private String value;
	
	private String prefix;
	private int index;
	
	private PathType pathType;
	private String xmlItem;
	private String xmlPath;
	private String session;
	private String parameter;
	
	private boolean error = false;
	
	public DefaultCFGConfig() {}
	
	public DefaultCFGConfig(String path, String prefix, int index, String key, String value, PathType pathType) {
		setPath(path);
		setPrefix(prefix);
		setIndex(index);
		setKey(key);
		setValue(value);
		setPathType(pathType);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public PathType getPathType() {
		return pathType;
	}

	public void setPathType(PathType pathType) {
		this.pathType = pathType;
	}
	
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	
	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getXMLPath() {
		return xmlPath;
	}

	@Override
	public String getXMLItem() {
		return xmlItem;
	}

	public void setXmlItem(String xmlItem) {
		this.xmlItem = xmlItem;
	}

	public boolean hasError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	
	
	
}