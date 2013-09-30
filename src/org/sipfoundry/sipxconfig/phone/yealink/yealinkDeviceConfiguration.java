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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.sipfoundry.sipxconfig.device.ProfileContext;
import org.sipfoundry.sipxconfig.phone.yealink.speeddials.yealinkSpeedDialProcessor;
import org.sipfoundry.sipxconfig.speeddial.Button;

/**
 * Responsible for generating ipmid.cfg
 */
public class yealinkDeviceConfiguration extends ProfileContext {
	
	private yealinkSpeedDialProcessor m_speedDialProcessor = null;

	public yealinkDeviceConfiguration(yealinkPhone device, String profileTemplate, yealinkSpeedDialProcessor speedDialProcessor) {
		super(device, profileTemplate);
		this.m_speedDialProcessor = speedDialProcessor;
	}
	
	
	@Override
	public Map<String, Object> getContext() {
		Map<String, Object> context = super.getContext();
		context.put("speeddial", m_speedDialProcessor);
		return context;
	}

	public Collection<Button> getSpeedDial() {
		yealinkPhone phone = (yealinkPhone) getDevice();
		if (null == phone.getSpeedDial()) {
			return Collections.emptyList();
		} else {
			return phone.getSpeedDial().getButtons();
		}
	}
}
