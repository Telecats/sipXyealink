/*
 *
 *
 * Author: Konstantin S. Vishnivetsky
 * E-mail: info@siplabs.ru
 * Copyright (C) 2011 SibTelCom, JSC., certain elements licensed under a Contributor Agreement.
 * Contributors retain copyright to elements licensed under a Contributor Agreement.
 * Licensed to the User under the LGPL license.
 *
 *
 */
package org.sipfoundry.sipxconfig.phone.yealink;

import java.util.Map;

import org.sipfoundry.sipxconfig.device.ProfileContext;
import org.sipfoundry.sipxconfig.phone.yealink.speeddials.yealinkSpeedDialProcessor;

/**
 * Responsible for generating ipmid.cfg
 */
public class yealinkXmlConfiguration extends ProfileContext {

    private yealinkSpeedDialProcessor m_speedDialProcessor;
    
    
	public yealinkXmlConfiguration(yealinkPhone device, String profileTemplate, yealinkSpeedDialProcessor speedDialProcessor) {
		super(device, profileTemplate);
		m_speedDialProcessor = speedDialProcessor;
	}
		
	@Override
	public Map<String, Object> getContext() {
		Map<String, Object> context = super.getContext();
		context.put("speeddial", m_speedDialProcessor);
		return context;
	}

}
