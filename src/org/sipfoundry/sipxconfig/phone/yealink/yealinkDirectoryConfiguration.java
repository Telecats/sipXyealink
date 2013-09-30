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

import java.io.File;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sipfoundry.sipxconfig.device.Device;
import org.sipfoundry.sipxconfig.device.ProfileContext;
import org.sipfoundry.sipxconfig.phonebook.Phonebook;
import org.sipfoundry.sipxconfig.phonebook.PhonebookEntry;
import org.sipfoundry.sipxconfig.phonebook.PhonebookManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class yealinkDirectoryConfiguration extends ProfileContext {
	private final Collection<PhonebookEntry> m_entries;

	// Common static members
	private static final Log LOG = LogFactory
			.getLog(yealinkDirectoryConfiguration.class);

	public yealinkDirectoryConfiguration(yealinkPhone device,
			Collection<PhonebookEntry> entries, String profileTemplate) {
		super(device, profileTemplate);
		m_entries = entries;
	}

	public Collection<yealinkPhonebookEntry> getRows() {
		int size = getSize();
		if (size == 0) {
			return Collections.emptyList();
		}
		Collection<yealinkPhonebookEntry> yealinkEntries = new LinkedHashSet<yealinkPhonebookEntry>(
				size);
		if (m_entries != null) {
			transformPhoneBook(m_entries, yealinkEntries);
		}
		return yealinkEntries;
	}

	private int getSize() {
		int size = 0;
		if (m_entries != null) {
			size += m_entries.size();
		}
		return size;
	}

	void transformPhoneBook(Collection<PhonebookEntry> phonebookEntries,
			Collection<yealinkPhonebookEntry> yealinkEntries) {
		for (PhonebookEntry entry : phonebookEntries) {
			yealinkEntries.add(new yealinkPhonebookEntry(entry));
		}
		List<yealinkPhonebookEntry> tmp = Collections.list(Collections
				.enumeration(yealinkEntries));
		Collections.sort(tmp);
		yealinkEntries.clear();
		for (yealinkPhonebookEntry entry : tmp) {
			yealinkEntries.add(entry);
		}
	}

	/**
	 * Due to Yealink limitation all entries with the same contact are equal.
	 */
	public static class yealinkPhonebookEntry implements
			Comparable<yealinkPhonebookEntry> {
		private final String m_firstName;
		private String m_lastName;
		private final String m_contact;

		public yealinkPhonebookEntry(PhonebookEntry entry) {
			m_contact = entry.getNumber();
			m_lastName = entry.getLastName();
			m_firstName = entry.getFirstName();
		}

		public String getFirstName() {
			String firstName = m_firstName;
			if (firstName == null && m_lastName == null) {
				return m_contact;
			}
			return firstName;
		}

		public String getLastName() {
			return m_lastName;
		}

		public String getContact() {
			return m_contact;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(m_contact).toHashCode();
		}

		@Override
		public int compareTo(yealinkPhonebookEntry a) {
			int result = 0;
			if (a == null)
				return 0;
			if (m_lastName != null && a.getLastName() != null)
				result = m_lastName.compareTo(a.getLastName());
			if (m_firstName != null && a.getFirstName() != null)
				return result == 0 ? m_firstName.compareTo(a.getFirstName())
						: result;
			else
				return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof yealinkPhonebookEntry)) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			yealinkPhonebookEntry rhs = (yealinkPhonebookEntry) obj;
			return new EqualsBuilder().append(m_contact, rhs.m_contact)
					.isEquals();
		}
	}
}
