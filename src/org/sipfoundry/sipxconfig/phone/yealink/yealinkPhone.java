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

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sipfoundry.sipxconfig.device.Device;
//import org.sipfoundry.sipxconfig.device.HotProvisioningManager;
import org.sipfoundry.sipxconfig.device.Profile;
import org.sipfoundry.sipxconfig.device.ProfileContext;
import org.sipfoundry.sipxconfig.device.ProfileFilter;
import org.sipfoundry.sipxconfig.device.ProfileLocation;
import org.sipfoundry.sipxconfig.phone.Line;
import org.sipfoundry.sipxconfig.phone.LineInfo;
import org.sipfoundry.sipxconfig.phone.Phone;
import org.sipfoundry.sipxconfig.phone.PhoneContext;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.writer.ConfigWriter;
import org.sipfoundry.sipxconfig.phone.yealink.speeddials.yealinkSpeedDialManager;
import org.sipfoundry.sipxconfig.phone.yealink.speeddials.yealinkSpeedDialProcessor;
import org.sipfoundry.sipxconfig.phonebook.PhonebookEntry;
import org.sipfoundry.sipxconfig.setting.Setting;
import org.sipfoundry.sipxconfig.setting.SettingExpressionEvaluator;
import org.sipfoundry.sipxconfig.speeddial.SpeedDial;
import org.sipfoundry.provisioning.hot.HotProvisionable;
//import org.sipfoundry.sipxconfig.phone.yealink.writer.PropertyConfigWriter;

/**
 * Yealink abstract phone.
 */
public class yealinkPhone extends Phone implements HotProvisionable {

	// Common static members
	private static final Log LOG = LogFactory.getLog(yealinkPhone.class);

	// Common members
	private SpeedDial m_speedDial = null;

	// Config writer used for writing cfg / xml (T2XP) / xml (T3XG)
    private ConfigWriter m_configWriter = new ConfigWriter();

	// Writer instances
	//private PropertyConfigWriter propertyConfigWriter = new PropertyConfigWriter();

	public yealinkPhone() {
	}
	
	public Date getNow() {
		return new Date();
	}
	
/*
 	@Override
    public void sendCheckSyncToMac() {
        try {
	    //2nd argument: Whether or not to force a reboot by adding reboot=true
            //getSipService().sendCheckSync(getInstrumentAddrSpec(), true);
            getSipService().sendCheckSync(getInstrumentAddrSpec());
        } catch (RuntimeException ex) {
            throw new RestartException(PHONE_SIP_EXCEPTION);
        }
    }
    
    @Override
    protected void sendCheckSyncToFirstLine() {
        if (getLines().size() == 0) {
            throw new RestartException("&phone.line.not.valid");
        }

        Line line = getLine(0);
        try {
        	getSipService().sendCheckSync(line.getAddrSpec(), true);
        } catch (RuntimeException ex) {
            throw new RestartException(PHONE_SIP_EXCEPTION);
        }
    }
*/
	
	public int getMaxLineCount() {
		yealinkModel model = (yealinkModel) getModel();
		if (null != model) {
			return model.getMaxLineCount();
		}
		return 0;
	}

	public int getSoftKeyCount() {
		yealinkModel model = (yealinkModel) getModel();
		if (null != model)
			return model.getSoftKeyCount();
		else
			return 0;
	}

	public boolean getHasHD() {
		yealinkModel model = (yealinkModel) getModel();
		if (null != model)
			return !model.getnoHD();
		else
			return false;
	}

	public String getDirectedCallPickupString() {
		return getPhoneContext().getPhoneDefaults().getDirectedCallPickupCode();
	}

	@Override
	public void initialize() {
		addDefaultBeanSettingHandler(new yealinkPhoneDefaults(getPhoneContext()
				.getPhoneDefaults(), this));
	}

	@Override
	public void initializeLine(Line line) {
		line.addDefaultBeanSettingHandler(new yealinkLineDefaults(
				getPhoneContext().getPhoneDefaults(), line));
	}
	
	private void initSpeedDials(Integer userId) {
		yealinkModel model = (yealinkModel)this.getModel();
		//Get speeddial for user. Create if user has none.
		m_speedDial = model.getSpeedDialManager().getSpeedDialForUserId(userId, true);
	}

	/**
	 * Copy common configuration file.
	 */
	@Override
	protected void copyFiles(ProfileLocation location) {
		yealinkModel model = (yealinkModel) getModel();
		// getProfileGenerator().copy(location, model.getModelDir() +
		// File.separator + yealinkConstants.WEB_ITEMS_LEVEL,
		// yealinkConstants.WEB_ITEMS_LEVEL);
	}

	//Removed because the Override interacted with the "if/unless phone has feature" in the phone.xml  
	// @Override
	// protected SettingExpressionEvaluator getSettingsEvaluator() {
	// return new yealinkSettingExpressionEvaluator(getModel().getModelId());
	// }

	@Override
	public void removeProfiles(ProfileLocation location) {
		Profile[] profiles = getProfileTypes();
		for (Profile profile : profiles) {
			location.removeProfile(profile.getName());
		}
	}
 
	@Override
	public Profile[] getProfileTypes() {
		yealinkModel model = (yealinkModel) getModel();
		Profile[] profileTypes = new Profile[] { new DeviceProfile(
				getDeviceFilename()) };

		if (getPhonebookManager().getPhonebookManagementEnabled())
			if (model.getUsePhonebook())
				profileTypes = (Profile[]) ArrayUtils.add(profileTypes,
						new DirectoryProfile(getDirectoryFilename()));

		if (model.getHasSeparateDialNow())
			profileTypes = (Profile[]) ArrayUtils.add(profileTypes,
					new DialNowProfile(getDialNowFilename()));
 
		final XmlProvisionProfile xmlProvProfile = new XmlProvisionProfile(
				getHotProvisionFilename());
		profileTypes = (Profile[]) ArrayUtils.add(profileTypes, xmlProvProfile);

		return profileTypes;
	}

	@Override
	public String getProfileFilename() {
		return getSerialNumber();
	}

	public String getDeviceFilename() {
		return format("%s.cfg", getSerialNumber());
	}

	public String getDirectoryFilename() {
		return format("%s%s%s", getSerialNumber(), '-',
				yealinkConstants.XML_CONTACT_DATA);
	}

	public String getDialNowFilename() {
		return format("%s%s%s", getSerialNumber(), '-',
				yealinkConstants.XML_DIAL_NOW);
	}

	@Override
	public void restart() {
		sendCheckSyncToMac();
	}

	public SpeedDial getSpeedDial() {
		return m_speedDial;
	}

	/**
	 * Each subclass must decide how as much of this generic line information
	 * translates into its own setting model.
	 */
	@Override
	protected void setLineInfo(Line line, LineInfo info) {
		line.setSettingValue(yealinkConstants.USER_ID_V6X_SETTING,
				info.getUserId());
		line.setSettingValue(yealinkConstants.DISPLAY_NAME_V6X_SETTING,
				info.getDisplayName());
		line.setSettingValue(yealinkConstants.PASSWORD_V6X_SETTING,
				info.getPassword());
		line.setSettingValue(
				yealinkConstants.REGISTRATION_SERVER_HOST_V6X_SETTING,
				info.getRegistrationServer());
		line.setSettingValue(
				yealinkConstants.REGISTRATION_SERVER_PORT_V6X_SETTING,
				info.getRegistrationServerPort());
		line.setSettingValue(yealinkConstants.VOICE_MAIL_NUMBER_V6X_SETTING,
				info.getVoiceMail());

		line.setSettingValue(yealinkConstants.USER_ID_V7X_SETTING,
				info.getUserId());
		line.setSettingValue(yealinkConstants.DISPLAY_NAME_V7X_SETTING,
				info.getDisplayName());
		line.setSettingValue(yealinkConstants.PASSWORD_V7X_SETTING,
				info.getPassword());
		line.setSettingValue(
				yealinkConstants.REGISTRATION_SERVER_HOST_V7X_SETTING,
				info.getRegistrationServer());
		line.setSettingValue(
				yealinkConstants.REGISTRATION_SERVER_PORT_V7X_SETTING,
				info.getRegistrationServerPort());
		line.setSettingValue(yealinkConstants.VOICE_MAIL_NUMBER_V7X_SETTING,
				info.getVoiceMail());
	}

	/**
	 * Each subclass must decide how as much of this generic line information
	 * can be contructed from its own setting model.
	 */
	@Override
	protected LineInfo getLineInfo(Line line) {
		LineInfo info = new LineInfo();
		info.setDisplayName(line
				.getSettingValue(yealinkConstants.DISPLAY_NAME_V6X_SETTING));
		info.setUserId(line
				.getSettingValue(yealinkConstants.USER_ID_V6X_SETTING));
		info.setPassword(line
				.getSettingValue(yealinkConstants.PASSWORD_V6X_SETTING));
		info.setRegistrationServer(line
				.getSettingValue(yealinkConstants.REGISTRATION_SERVER_HOST_V6X_SETTING));
		info.setRegistrationServerPort(line
				.getSettingValue(yealinkConstants.REGISTRATION_SERVER_PORT_V6X_SETTING));
		info.setVoiceMail(line
				.getSettingValue(yealinkConstants.VOICE_MAIL_NUMBER_V6X_SETTING));
		return info;
	}

	public boolean isHotProvisioningSupported() {
		return true;
	}

	private String getHotProvisionFilename() {
		return getSerialNumber().toUpperCase() + ".prov.xml";
	}

	public void pushXmlToPhone(String sipContactHost, String configFilePath) {
		LOG.info("yealink: pushXmlToPhone, sipContactHost:" + sipContactHost);
		LOG.debug("yealink: configFilePath:" + configFilePath);

		// read config from file
		File configFile = new File(configFilePath);
		LOG.debug("yealink: configFile: " + configFile.exists());
		StringBuffer configData = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					configFilePath));
			String line = br.readLine();
			while (line != null) {
				configData.append(line);
				line = br.readLine();
			}
		} catch (IOException e1) {
			LOG.error("yealink: Error reading hotProvision config file:"
					+ configFilePath + "," + e1.getMessage(), e1);
			return;
		}

		// send config to phone
		PostMethod postMethod = new PostMethod("http://" + sipContactHost
				+ ":80");
		postMethod.setRequestHeader("Host", sipContactHost);
		postMethod.setRequestHeader("Connection", "Keep-Alive");
		postMethod.setRequestHeader("Content-Type:", "text/xml");
		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			LOG.debug("yealink: hostAddresss:" + hostAddress);
			postMethod.setRequestHeader("Referer", hostAddress);
		} catch (UnknownHostException e1) {
			LOG.error(
					"yealink: Error detecting hostAddress:" + e1.getMessage(),
					e1);
			e1.printStackTrace();
		}

		LOG.debug("yealink: configData:");
		LOG.debug("yealink: --------------------");
		LOG.debug(configData.toString());
		LOG.debug("yealink: --------------------");

		StringRequestEntity body = new StringRequestEntity(
				configData.toString());

		postMethod.setRequestEntity(body);
		HttpClient httpClient = new HttpClient();

		// sometimes we get (ioexception):Connection reset when phone is busy
		// so retry on error
		int retry = 0;
		int maxRetry = 10;
		boolean success = false;

		while (retry++ <= maxRetry && !success) {
			try {
				LOG.debug("yealink: Sending xml (" + retry + ")...");
				httpClient.executeMethod(postMethod);
				success = true;
				LOG.info("yealink: Sending xml (" + retry + ")... done");
			} catch (HttpException e) {
				LOG.error(
						"yealink: Error performing hotprovisioning (httpexception) ("
								+ retry + "):" + e.getMessage(), e);
			} catch (IOException e) {
				LOG.error(
						"yealink: Error performing hotprovisioning (ioexception) ("
								+ retry + "):" + e.getMessage(), e);
			}

			if (!success) {
				try {
					if (retry < maxRetry) {
						LOG.warn("Couldn't send xml to phone. Retrying, attempt:"
								+ retry);
					} else {
						LOG.warn("Couldn't send xml to phone, attempt:" + retry);
					}
					// wait before retry
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

//	@Override
	public void performHotProvisioning(HashMap<String, String> hotProvProps) {
		String sipContactHost = hotProvProps.get("sipContactHost");
//				.get(HotProvisioningManager.SIP_CONTACT_HOST_PROP);

		yealinkModel model = (yealinkModel) getModel();
		String configFilePath = model.getParentDir() + "/"
				+ getHotProvisionFilename();
		LOG.debug("yealink: configFilePath:" + configFilePath);

		// check prov file is up-2-date, or wait for it to be updated
		long outdatedThreadhold = 5 * 60 * 1000; // 5min
		long waitPeriod = 1000; // 1sec
		int maxRetry = 15; // 15 x 1sec = maxWait 15sec

		// perform up-2-date check
		File provXmlFile = new File(configFilePath);
		long age = (System.currentTimeMillis() - provXmlFile.lastModified());
		int retryAttempt = 1;
		while (age > outdatedThreadhold && retryAttempt < maxRetry) {
			LOG.debug("yealink: hotProvXmlFile outdated (older then:"
					+ outdatedThreadhold
					+ ", wait:'"
					+ waitPeriod
					+ "'ms for new file to become available and retry. Attempt:"
					+ retryAttempt);
			retryAttempt++;
			synchronized (this) {
				try {
					wait(waitPeriod);
				} catch (InterruptedException ie) {
					// ignore
				}
			}
			age = (System.currentTimeMillis() - provXmlFile.lastModified());
		}

		if (age > outdatedThreadhold) {
			LOG.warn("yealink: hotProvXmlFile outdated, older then:"
					+ outdatedThreadhold + ", age is:" + age + ",file:"
					+ configFilePath);
		}

		LOG.debug("yealink: hotProvXmlFile age:" + age);
		pushXmlToPhone(sipContactHost, configFilePath);
	}

    public ConfigWriter getConfigWriter() {
        return m_configWriter;
    }
    

	static class XmlProvisionProfile extends Profile {
		private static final String APPLICATION_XML = "application/xml";

		public XmlProvisionProfile(String name) {
			super(name, APPLICATION_XML);
		}

		@Override
		protected ProfileFilter createFilter(Device device) {
			return null;
		}

		@Override
		protected ProfileContext createContext(Device device) {
			yealinkPhone phone = (yealinkPhone) device;
			
			yealinkModel model = (yealinkModel) phone.getModel();
			yealinkSpeedDialManager sdManager = new yealinkSpeedDialManager(model.getSpeedDialManager(), phone.getDirectedCallPickupString());
			yealinkSpeedDialProcessor sdProcessor = new yealinkSpeedDialProcessor(sdManager);

			return new yealinkXmlConfiguration(phone, model.getXmlProfileTemplate(), sdProcessor);
		}
	}

	static class DeviceProfile extends Profile {
		public DeviceProfile(String name) {
			super(name, yealinkConstants.MIME_TYPE_PLAIN);
		}

		@Override
		protected ProfileFilter createFilter(Device device) {
			return null;
		}

		@Override
		protected ProfileContext createContext(Device device) {
			yealinkPhone phone = (yealinkPhone) device;
			yealinkModel model = (yealinkModel) phone.getModel();
			
			yealinkSpeedDialManager sdManager = new yealinkSpeedDialManager(model.getSpeedDialManager(), phone.getDirectedCallPickupString());
			yealinkSpeedDialProcessor sdProcessor = new yealinkSpeedDialProcessor(sdManager);
			
			return new yealinkDeviceConfiguration(phone, model.getProfileTemplate(), sdProcessor);
		}
	}

	static class DialNowProfile extends Profile {
		public DialNowProfile(String name) {
			super(name, yealinkConstants.MIME_TYPE_XML);
		}

		@Override
		protected ProfileFilter createFilter(Device device) {
			return null;
		}

		@Override
		protected ProfileContext createContext(Device device) {
			yealinkPhone phone = (yealinkPhone) device;
			yealinkModel model = (yealinkModel) phone.getModel();
			return new yealinkDialNowConfiguration(phone,
					model.getdialNowProfileTemplate());
		}
	}

	static class DirectoryProfile extends Profile {

		public DirectoryProfile(String name) {
			super(name, yealinkConstants.MIME_TYPE_PLAIN);
		}

		@Override
		protected ProfileFilter createFilter(Device device) {
			return null;
		}

		@Override
		protected ProfileContext createContext(Device device) {
			yealinkPhone phone = (yealinkPhone) device;
			yealinkModel model = (yealinkModel) phone.getModel();
			PhoneContext phoneContext = phone.getPhoneContext();
			Collection<PhonebookEntry> entries = phoneContext
					.getPhonebookEntries(phone);
			return new yealinkDirectoryConfiguration(phone, entries,
					model.getDirectoryProfileTemplate());
		}
	}

	static class yealinkSettingExpressionEvaluator implements
			SettingExpressionEvaluator {
		private final String m_model;

		public yealinkSettingExpressionEvaluator(String model) {
			m_model = model;
		}

		public boolean isExpressionTrue(String expression, Setting setting_) {
			return m_model.matches(expression);
		}
	}
}
