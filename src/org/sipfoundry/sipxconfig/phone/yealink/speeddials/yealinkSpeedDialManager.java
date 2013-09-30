package org.sipfoundry.sipxconfig.phone.yealink.speeddials;

import java.util.ArrayList;
import java.util.List;

import org.sipfoundry.sipxconfig.phone.Line;
import org.sipfoundry.sipxconfig.speeddial.Button;
import org.sipfoundry.sipxconfig.speeddial.SpeedDial;
import org.sipfoundry.sipxconfig.speeddial.SpeedDialManager;

public class yealinkSpeedDialManager {

	private List<Button> speeddials = new ArrayList<Button>();
	private String directedCallPickupValue;
	private int currentSpeeddial = -1;
	private SpeedDialManager m_speedDialManager;
	
	public yealinkSpeedDialManager(SpeedDialManager speedDialManager,
			String directedCallPickupValue) {
		this.m_speedDialManager = speedDialManager;
		this.directedCallPickupValue = directedCallPickupValue;
	}
	
	public void init(Line line) {
		int userId = line.getUser().getId();
		SpeedDial speedDial = m_speedDialManager.getSpeedDialForUserId(userId, true);
		this.speeddials.addAll(speedDial.getButtons());
	}
	
	public boolean hasMoreSpeedDials() {
		int nextSpeeddialIndex = currentSpeeddial + 1;
		return nextSpeeddialIndex < speeddials.size();
	}

	public Button getNextSpeedDial() {
		if (hasMoreSpeedDials()) {
			return speeddials.get(++currentSpeeddial);
		}
		return null;
	}
	
	public String getPickupValueFor(Button button) {
		//return directedCallPickupValue + button.getNumber();
		return directedCallPickupValue;
	}
	
	
}
