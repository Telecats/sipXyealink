package org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion;

import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.conversion.types.PathType;

public interface ICFGConfig {

	public String getPath();
	public PathType getPathType();
	public String getValue();
	public String getXMLPath();
	public String getXMLItem();
	public boolean hasError();
	public String getKey();
	public String getSession();
	public String getParameter();
	
}
