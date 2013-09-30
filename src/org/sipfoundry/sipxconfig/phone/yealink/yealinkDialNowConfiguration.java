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

import org.sipfoundry.sipxconfig.device.ProfileContext;
import org.sipfoundry.sipxconfig.speeddial.SpeedDial;
import org.sipfoundry.sipxconfig.speeddial.Button;

/**
 * Responsible for generating ipmid.cfg
 */
public class yealinkDialNowConfiguration extends ProfileContext {

    public yealinkDialNowConfiguration(yealinkPhone device, String profileTemplate) {
        super(device, profileTemplate);
    }
}
