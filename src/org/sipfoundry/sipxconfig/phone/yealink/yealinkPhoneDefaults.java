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
//import org.sipfoundry.sipxconfig.service.UnmanagedService;
//import static yealinkConstants.*;
import java.util.Iterator;

import org.sipfoundry.sipxconfig.address.AddressManager;
import org.sipfoundry.sipxconfig.common.User;
import org.sipfoundry.sipxconfig.device.DeviceDefaults;
import org.sipfoundry.sipxconfig.device.DeviceTimeZone;
import org.sipfoundry.sipxconfig.dns.DnsManager;
import org.sipfoundry.sipxconfig.phonebook.Phonebook;
import org.sipfoundry.sipxconfig.phonebook.PhonebookManager;
import org.sipfoundry.sipxconfig.phonelog.PhoneLog;
//import org.sipfoundry.sipxconfig.service.UnmanagedService;
import org.sipfoundry.sipxconfig.setting.SettingEntry;

public class yealinkPhoneDefaults {
	private final DeviceDefaults m_defaults;
	private final yealinkPhone m_phone;

	public yealinkPhoneDefaults(DeviceDefaults defaults, yealinkPhone phone) {
		m_defaults = defaults;
		m_phone = phone;
	}

	@SettingEntry(paths = { 
			yealinkConstants.LOCAL_TIME_SERVER1_V6X_SETTING,
			yealinkConstants.LOCAL_TIME_SERVER1_V7X_SETTING })
	public String getTimeServer1() {
		//String server = m_defaults.getServer(0, UnmanagedService.NTP);
		String server = m_defaults.getNtpServer();
		return server == null ? m_defaults.getTftpServer().getAddress() : server;
	}

	@SettingEntry(paths = { 
			yealinkConstants.LOCAL_TIME_SERVER2_V6X_SETTING,
			yealinkConstants.LOCAL_TIME_SERVER2_V7X_SETTING })
	public String getTimeServer2() {
		return m_defaults.getAlternateNtpServer();
	}

	private DeviceTimeZone getZone() {
		return m_defaults.getTimeZone();
	}

	@SettingEntry(paths = { 
			yealinkConstants.LOCAL_TIME_ZONE_V6X_SETTING,
			yealinkConstants.LOCAL_TIME_ZONE_V7X_SETTING })
	public String getTimeZone() {
		Integer tz = getZone().getOffsetInHours();
		if (tz < 0)
			return "-" + tz.toString();
		else
			return "+" + tz.toString();
	}

	@SettingEntry(paths = { 
			yealinkConstants.SYSLOG_SERVER_V6X_SETTING,
			yealinkConstants.SYSLOG_SERVER_V7X_SETTING })
	public String getSyslogdIP() {
		AddressManager addressManager = m_defaults.getAddressManager();
        if (addressManager.getSingleAddress(PhoneLog.PHONELOG) != null) {
            return addressManager.getSingleAddress(PhoneLog.PHONELOG).getAddress();
        }
        return null;
	}

	public String getTFTPServer() {
		return m_defaults.getTftpServer().getAddress();
	}

	@SettingEntry(paths = { 
			yealinkConstants.DNS_SERVER1_V6X_SETTING,
			yealinkConstants.DNS_SERVER1_V7X_SETTING })
	public String getNameServer1() {
		AddressManager addressManager = m_defaults.getAddressManager();
        if (addressManager.getSingleAddress(DnsManager.DNS_ADDRESS) != null) {
            return addressManager.getSingleAddress(DnsManager.DNS_ADDRESS).getAddress();
        }
        return null;
	}

	@SettingEntry(paths = { 
			yealinkConstants.DNS_SERVER2_V6X_SETTING,
			yealinkConstants.DNS_SERVER2_V7X_SETTING })
	public String getNameServer2() {
		AddressManager addressManager = m_defaults.getAddressManager();
        if (addressManager.getSingleAddress(DnsManager.DNS_ADDRESS) != null) {
            return addressManager.getSingleAddress(DnsManager.DNS_ADDRESS).getAddress();
        }
        return null;
	}

	@SettingEntry(paths = {
			yealinkConstants.REMOTE_PHONEBOOK_0_NAME_V6X_SETTING,
			yealinkConstants.REMOTE_PHONEBOOK_0_NAME_V7X_SETTING })
	public String getPhonebook0Name() {
		User user = m_phone.getPrimaryUser();
		if (user != null) {
			PhonebookManager pbm = m_phone.getPhonebookManager();
			if (pbm != null) {
				Collection<Phonebook> books = pbm.getAllPhonebooksByUser(user);
				if (books != null) {
					Iterator pbit = books.iterator();
					if (pbit != null) {
						if (pbit.hasNext()) {
							Phonebook pb0 = (Phonebook) pbit.next();
							if (pb0 != null) {
								if (pb0.getShowOnPhone()) {
									String pbName = pb0.getName();
									if (pbName != null)
										return pbName;
								}
							}
						}
					}
				}
			}
		}
		return new String();
	}

	@SettingEntry(paths = {
			yealinkConstants.REMOTE_PHONEBOOK_0_URL_V6X_SETTING,
			yealinkConstants.REMOTE_PHONEBOOK_0_URL_V7X_SETTING })
	public String getPhonebook0URL() {
		//return "tftp://" + m_defaults.getTftpServer() + "/"
		//		+ m_phone.getSerialNumber() + "-"
		//		+ yealinkConstants.XML_CONTACT_DATA;
		return "%NULL%";
	}

	@SettingEntry(paths = { yealinkConstants.FIRMWARE_SERVER_ADDRESS_SETTING,
			yealinkConstants.PUSH_XML_SERVER_V6X_SETTING,
			yealinkConstants.PUSH_XML_SERVER_V7X_SETTING })
	public String getserver_ip() {
		return m_defaults.getTftpServer().getAddress();
	}

	@SettingEntry(path = yealinkConstants.FIRMWARE_HTTP_URL_SETTING)
	public String gethttp_url() {
		yealinkModel model = (yealinkModel) m_phone.getModel();
		return m_defaults.getProfileRootUrl() + "/" + model.getName() + ".rom";
	}

	@SettingEntry(paths = { 
			yealinkConstants.FIRMWARE_URL_V6X_SETTING,
			yealinkConstants.FIRMWARE_URL_V7X_SETTING })
	public String geturl() {
		yealinkModel model = (yealinkModel) m_phone.getModel();
		return "tftp://" + m_defaults.getTftpServer() + "/" + model.getName()
				+ ".rom";
	}

	@SettingEntry(paths = {
			yealinkConstants.AUTOPROVISIONING_SERVER_URL_V6X_SETTING
	})
	public String getstrServerURL() {
		return "tftp://" + m_defaults.getTftpServer() + "/"
				+ m_phone.getSerialNumber() + ".cfg";
	}

	@SettingEntry(paths = {
			yealinkConstants.AUTOPROVISIONING_SERVER_ADDRESS_SETTING,
			yealinkConstants.AUTOPROVISIONING1_SERVER_ADDRESS_SETTING,
			yealinkConstants.AUTOPROVISIONING_SERVER_URL_V7X_SETTING,
			yealinkConstants.AUTOPROVISIONING1_SERVER_URL_V7X_SETTING})
	public String getserver_Address() {
		return "tftp://" + m_defaults.getTftpServer();
	}

	@SettingEntry(path = 
			yealinkConstants.FIRMWARE_NAME_SETTING)
	public String getfirmware_name() {
		yealinkModel model = (yealinkModel) m_phone.getModel();
		return model.getName() + ".rom";
	}

	@SettingEntry(paths = { 
			yealinkConstants.LANG_FILE_NAME_V6X_SETTING,
			yealinkConstants.LANG_FILE_NAME_V7X_SETTING })
	public String getLangURL() {
		return "tftp://" + m_defaults.getTftpServer() + "/lang+English.txt";
	}

	@SettingEntry(paths = { 
			yealinkConstants.LOGO_FILE_NAME_V6X_SETTING,
			yealinkConstants.LOGO_FILE_NAME_V7X_SETTING })
	public String getLogoURL() {
		return "tftp://" + m_defaults.getTftpServer()
				+ "/yealinkLogo132x64.dob";
	}

	@SettingEntry(paths = { 
			yealinkConstants.WALLPAPER_FILE_NAME_V6X_SETTING,
			yealinkConstants.WALLPAPER_FILE_NAME_V7X_SETTING })
	public String getWallPaperURL() {
		return "tftp://" + m_defaults.getTftpServer() + "/yealinkWallpaper.jpg";
	}

	@SettingEntry(paths = { 
			yealinkConstants.DIAL_NOW_URL_V6X_SETTING,
			yealinkConstants.DIAL_NOW_URL_V7X_SETTING })
	public String getDialnowUrl() {
		return "tftp://" + m_defaults.getTftpServer() + "/"
				+ m_phone.getSerialNumber() + "-"
				+ yealinkConstants.XML_DIAL_NOW;
	}

	// @SettingEntry(path = yealinkConstants.ALERT_INFO_TEXT_0)
	// public String getText0() {
	// return getPhoneContext().getIntercomForPhone(m_phone).getCode();
	// }

}
