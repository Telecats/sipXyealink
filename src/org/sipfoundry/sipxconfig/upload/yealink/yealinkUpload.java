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

package org.sipfoundry.sipxconfig.upload.yealink;

import org.sipfoundry.sipxconfig.upload.Upload;
import org.sipfoundry.sipxconfig.upload.UploadSpecification;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//! A Yealink files uploading class.
/*!
 Used for various Yealink files uploading, such as:
	Firmware binaries
	LCD localization file(should be UTF32BE encoded)
	Idle Screen XML(for SIP-T38G only)
	Logo images for SIP-T1X and SIP-T2x models in DOB format
*/
public class yealinkUpload extends Upload {

//! Default logging interface object.
    private static final Log LOG = LogFactory.getLog(yealinkUpload.class);

//! Default constructor.
    public yealinkUpload() {
    }

//! Constructor with Bean Id.
    protected yealinkUpload(String beanId) {
	super(beanId);
    }

//! Constructor with specification.
    public yealinkUpload(UploadSpecification specification) {
	super(specification);
    }

//! Files deployment code override.
/*!
 Here you should place additioonal files copying to TFTP directory instructions.
*/
    @Override
    public void deploy() {
	super.deploy();
    }

//! Files undeployment code override.
/*!
 Here you should place additioonal files removal from TFTP directory instructions.
*/
    @Override
    public void undeploy() {
	super.undeploy();
    }
}
