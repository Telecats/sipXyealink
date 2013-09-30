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

import org.sipfoundry.sipxconfig.common.User;
import org.sipfoundry.sipxconfig.device.DeviceDefaults;
import org.sipfoundry.sipxconfig.phone.Line;
import org.sipfoundry.sipxconfig.setting.SettingEntry;

public class yealinkLineDefaults {
	private final DeviceDefaults m_defaults;
	private final Line m_line;

	yealinkLineDefaults(DeviceDefaults defaults, Line line) {
		m_defaults = defaults;
		m_line = line;
	}

	@SettingEntry(paths = { 
			yealinkConstants.AUTH_ID_V6X_SETTING,
			yealinkConstants.AUTH_ID_V7X_SETTING })
	public String getAuthName() {
		String userName = null;
		User user = m_line.getUser();
		if (user != null) {
			userName = user.getUserName();
		}
		String macAddress = "";
		macAddress = m_line.getPhone().getSerialNumber();
		return userName + "/" + macAddress;
	}

	@SettingEntry(paths = { 
			yealinkConstants.USER_ID_V6X_SETTING,
			yealinkConstants.USER_ID_V7X_SETTING })
	public String getUserName() {
		String userName = null;
		User user = m_line.getUser();
		if (user != null) {
			userName = user.getUserName();
		}
		return userName;
	}

	@SettingEntry(paths = { 
			yealinkConstants.DISPLAY_NAME_V6X_SETTING,
			yealinkConstants.DISPLAY_NAME_V7X_SETTING,
			yealinkConstants.USER_LABEL_67X_SETTING,
			yealinkConstants.USER_LABEL_V7X_SETTING })
	public String getDisplayName() {
		String displayName = null;
		User user = m_line.getUser();
		if (user != null) {
			displayName = user.getDisplayName();
		}
		return displayName;
	}

	@SettingEntry(paths = { 
			yealinkConstants.PASSWORD_V6X_SETTING,
			yealinkConstants.PASSWORD_V7X_SETTING })
	public String getPassword() {
		String password = null;
		User user = m_line.getUser();
		if (user != null) {
			password = user.getSipPassword();
		}
		return password;
	}

	@SettingEntry(paths = {
			yealinkConstants.REGISTRATION_SERVER_HOST_V6X_SETTING,
			yealinkConstants.REGISTRATION_SERVER_HOST_V7X_SETTING })
	public String getRegistrationServer() {
		return m_defaults.getDomainName();
	}

	@SettingEntry(paths = { yealinkConstants.OUTBOUND_HOST_V6X_SETTING,
			yealinkConstants.BACKUP_OUTBOUND_HOST_V6X_SETTING,
			yealinkConstants.OUTBOUND_HOST_V7X_SETTING,
			yealinkConstants.BACKUP_OUTBOUND_HOST_V7X_SETTING })
	public String getOutboundHost() {
		//return m_defaults.getProxyServerAddr();
		return "";
	}

	@SettingEntry(paths = { yealinkConstants.OUTBOUND_PORT_V6X_SETTING,
			yealinkConstants.BACKUP_OUTBOUND_PORT_V6X_SETTING,
			yealinkConstants.OUTBOUND_PORT_V7X_SETTING,
			yealinkConstants.BACKUP_OUTBOUND_PORT_V7X_SETTING })
	public String getRegistrationServerPort() {
		//return m_defaults.getProxyServerSipPort();
		return "";
	}

	@SettingEntry(paths = { 
			yealinkConstants.VOICE_MAIL_NUMBER_V6X_SETTING,
			yealinkConstants.VOICE_MAIL_NUMBER_V7X_SETTING })
	public String getVoiceMail() {
		String voicemail = null;
		User u = m_line.getUser();
		if (u != null) {
			voicemail = m_defaults.getVoiceMail();
		}
		return voicemail;
	}
	/*
	 * @SettingEntry(path = yealinkConstants.MOH_URI_SETTING) public String
	 * getMusicOnHoldUri() { String mohUri; User u = m_line.getUser(); if (u !=
	 * null) { mohUri = u.getMusicOnHoldUri(); } else { mohUri =
	 * m_defaults.getMusicOnHoldUri(); } return mohUri; }
	 * 
	 * @SettingEntry(path = yealinkConstants.BLA_NUMBER_SETTING) public String
	 * getBLANumber() { User u = m_line.getUser(); String BLANumber = ""; if (u
	 * != null) { if (u.getIsShared()) BLANumber = u.getUserName(); } return
	 * BLANumber; }
	 * 
	 * @SettingEntry(path = yealinkConstants.IDLE_SCREEN_SETTING) public String
	 * getIdleScreenURL() { return "tftp://" + m_defaults.getTftpServer() +
	 * "/yealink_SIP-T38G_idle_screen.xml"; }
	 */
}
