package org.sipfoundry.sipxconfig.phone.yealink.speeddials;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sipfoundry.sipxconfig.phone.Line;
import org.sipfoundry.sipxconfig.phone.yealink.hotdesking.writer.ConfigWriter;
import org.sipfoundry.sipxconfig.speeddial.Button;

public class yealinkSpeedDialProcessor {

	public static final Pattern pLineKey = Pattern
			.compile("linekey\\.([0-9]*)\\.([a-zA-Z_]*)");
	public static final Pattern pMemoryKey = Pattern
			.compile("memorykey\\.([0-9]*)\\.([a-zA-Z_]*)");
	public static final Pattern pExpansionKey = Pattern
			.compile("expansion_module\\.([0-9]*)\\.key\\.([0-9]*)\\.([a-zA-Z_]*)");

	private static final String SPEEDDIAL_TYPE = "type";
	private static final String SPEEDDIAL_LINE = "line";
	private static final String SPEEDDIAL_PICKUP = "pickup_value";
	private static final String SPEEDDIAL_VALUE = "value";
	private static final String SPEEDDIAL_PHONEBOOK = "xml_phonebook";
	private static final String SPEEDDIAL_LABEL = "label";
	private static final String SPEEDDIAL_SUB_TYPE = "sub_type";

	private static final String SPEEDDIAL_TYPE_VALUE_CLEAR = "89";
	private static final String SPEEDDIAL_TYPE_VALUE_AUTO = "99";
	private static final String SPEEDDIAL_TYPE_VALUE_BLA = "13";
	private static final String SPEEDDIAL_TYPE_VALUE_BLF = "16";

	private Map<Integer, Map<String, String>> lineKeys = new TreeMap<Integer, Map<String, String>>();
	private Map<Integer, Map<String, String>> memoryKeys = new TreeMap<Integer, Map<String, String>>();
	private Map<Integer, Map<Integer, Map<String, String>>> expansionKeys = new TreeMap<Integer, Map<Integer, Map<String, String>>>();
	
	private static final String NORMALIZE_LINE_KEY = "linekey.%d.%s";
	private static final String NORMALIZE_MEMORY_KEY = "memorykey.%d.%s";
	private static final String NORMALIZE_EXPMOD_KEY = "expansion_module.%d.key.%d.%s";
	
	private yealinkSpeedDialManager speedDialManager;
	
	public yealinkSpeedDialProcessor(yealinkSpeedDialManager speedDialManager) {
		this.speedDialManager = speedDialManager;
	}
	
	public void init(Line line) {
		speedDialManager.init(line);
	}

	/**
	 * Returns true if the key got processed as a button. Returns false if the
	 * key is not a button. Write output in the velocity macro if the return
	 * value is false. If the return value is true, write the output that is
	 * coming from the getOutput() method.
	 * 
	 * @param key
	 *            The key as retrieved in the velocity macro.
	 * @param value
	 *            The value as retrieved in the velocity macro.
	 * @return If the key got processed as a button.
	 */
	public boolean process(String key, String value) {
		Matcher matcher = null;

		if ((matcher = pLineKey.matcher(key)).matches()) {
			processLineKey(matcher, key, value);

		} else if ((matcher = pMemoryKey.matcher(key)).matches()) {
			processMemoryKey(matcher, key, value);

		} else if ((matcher = pExpansionKey.matcher(key)).matches()) {
			processExpansionKey(matcher, key, value);

		} else {
			return false;

		}
		return true;
	}
	
	/**
	 * Normalizes properties in the maps (linekeys, memorykeys, expansionkeys) to a normalized cfg output
	 * @return A string that can be written to a pones MAC.cfg
	 */
	public String getOutput(ConfigWriter writer) {
		StringBuilder sb = new StringBuilder();
		processSpeedDials();
		Map<String, String> normalizedProperties = getNormalizedProperties();
		for(String key: normalizedProperties.keySet()) {
		    sb.append(writer.writeConfig(key, normalizedProperties.get(key))).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Processes a LineKey and stores it into a Map
	 * 
	 * @param matcher A Matcher that matched on a LineKey
	 * @param key The key as in the phones MAC.cfg
	 * @param value The value as in the phones MAC.cfg
	 */
	private void processLineKey(Matcher matcher, String key, String value) {
		Integer index = Integer.parseInt(matcher.group(1));
		String property = matcher.group(2);
		
		Map<String, String> lineKeyProperties = lineKeys.get(index);
		if (lineKeyProperties == null) {
			lineKeyProperties = new LinkedHashMap<String, String>();
			lineKeys.put(index, lineKeyProperties);
		}
		lineKeyProperties.put(property, value);
	}

	/**
	 * Processes a MemoryKey and stores it into a Map
	 * 
	 * @param matcher A Matcher that matched on a MemoryKey
	 * @param key The key as in the phones MAC.cfg
	 * @param value The value as in the phones MAC.cfg
	 */
	private void processMemoryKey(Matcher matcher, String key, String value) {
		Integer index = Integer.parseInt(matcher.group(1));
		String property = matcher.group(2);

		Map<String, String> memoryKeyProperties = memoryKeys.get(index);
		if (memoryKeyProperties == null) {
			memoryKeyProperties = new HashMap<String, String>();
			memoryKeys.put(index, memoryKeyProperties);
		}
		memoryKeyProperties.put(property, value);
	}

	/**
	 * Processes a ExpansionKey and stores it into a Map
	 * 
	 * @param matcher A Matcher that matched on a ExpansionKey
	 * @param key The key as in the phones MAC.cfg
	 * @param value The value as in the phones MAC.cfg
	 */
	private void processExpansionKey(Matcher matcher, String key, String value) {
		Integer expModIndex = Integer.parseInt(matcher.group(1));
		Integer buttonIndex = Integer.parseInt(matcher.group(2));
		String property = matcher.group(3);

		Map<Integer, Map<String, String>> keyMapForExpMod = expansionKeys
				.get(expModIndex);
		if (keyMapForExpMod == null) {
			keyMapForExpMod = new TreeMap<Integer, Map<String, String>>();
			expansionKeys.put(expModIndex, keyMapForExpMod);
		}

		Map<String, String> expKeyProperties = keyMapForExpMod.get(buttonIndex);
		if (expKeyProperties == null) {
			expKeyProperties = new HashMap<String, String>();
			keyMapForExpMod.put(buttonIndex, expKeyProperties);
		}

		expKeyProperties.put(property, value);
	}

	/**
	 * Processes each Map (lineKeys, memoryKeys, expModKeys) to create auto speeddials
	 */
	public void processSpeedDials() {
		processLineKeySpeedDials();
		processMemoryKeySpeedDials();
		processExpModKeySpeedDials();
	}

	/**
	 * Processes the lineKey map
	 */
	private void processLineKeySpeedDials() {
		for (Integer index : lineKeys.keySet()) {
			Map<String, String> properties = lineKeys.get(index);
			storeButtonToProperties(properties);
		}
	}
	
	/**
	 * Processes the memoryKey map
	 */
	private void processMemoryKeySpeedDials() {
		for (Integer index : memoryKeys.keySet()) {
			Map<String, String> properties = memoryKeys.get(index);
			storeButtonToProperties(properties);
		}
	}

	/**
	 * Processes the expModKey map
	 */
	private void processExpModKeySpeedDials() {
		for (Integer expModIndex : expansionKeys.keySet()) {
			Map<Integer, Map<String, String>> expModKeys = expansionKeys.get(expModIndex);
			for(Integer expKeyIndex: expModKeys.keySet()) {
				Map<String, String> properties = expModKeys.get(expKeyIndex);
				storeButtonToProperties(properties);
			}
		}
	}
	
	/**
	 * Stores a button to a map. This can be a defined button or an auto speeddial
	 */
	private void storeButtonToProperties(Map<String, String> properties) {
		String line = "0", type = "%NULL%", value = "%NULL%", pickupValue = "%NULL%", label = "%NULL%", phonebook = "%NULL%", subType = "%NULL%";
		
		if (properties.containsKey(SPEEDDIAL_TYPE)
				&& properties.get(SPEEDDIAL_TYPE).equals(SPEEDDIAL_TYPE_VALUE_AUTO)) {

			if (speedDialManager.hasMoreSpeedDials()) {
				Button speedDial = speedDialManager.getNextSpeedDial();
				
				//Assume line must be "0" for each speeddial. This might need to change if we decide to hotprovision muliple lines on one phone
				//line = properties.get(SPEEDDIAL_LINE);
				//line = line.isEmpty() ? "0" : line;

				type = speedDial.isBlf() ? SPEEDDIAL_TYPE_VALUE_BLF : SPEEDDIAL_TYPE_VALUE_BLA;
				value = speedDial.getNumber();
				pickupValue = speedDialManager.getPickupValueFor(speedDial);
				label = speedDial.getLabel();
			}

			storeButtonToProperties(properties, line, type, value,
					pickupValue, label, phonebook, subType);
			
		} else if (properties.containsKey(SPEEDDIAL_TYPE)
				&& properties.get(SPEEDDIAL_TYPE).equals(SPEEDDIAL_TYPE_VALUE_CLEAR)) {
			
			storeButtonToProperties(properties, line, type, value,
					pickupValue, label, phonebook, subType);
		}
		
		
	}

	/**
	 * Stores the values in a property map
	 */
	private void storeButtonToProperties(Map<String, String> properties,
			String line, String type, String value, String pickupValue,
			String label, String phonebook, String subType) {
		
		properties.put(SPEEDDIAL_LINE, line);
		properties.put(SPEEDDIAL_TYPE, type);
		properties.put(SPEEDDIAL_VALUE, value);
		properties.put(SPEEDDIAL_PICKUP, pickupValue);
		properties.put(SPEEDDIAL_LABEL, label);
		properties.put(SPEEDDIAL_PHONEBOOK, phonebook);
		properties.put(SPEEDDIAL_SUB_TYPE, subType);
	}
	
	/**
	 * Reverts the structure to a flat key = value structure for each map
	 */
	public Map<String, String> getNormalizedProperties() {
		Map<String, String> allProperties = new TreeMap<String, String>();
		allProperties.putAll(normalizeLineKeys());
		allProperties.putAll(normalizeMemoryKeys());
		allProperties.putAll(normalizeExpModKeys());
		return allProperties;
	}
	
	/**
	 * Reverts the structure to a flat key = value structure for the lineKeys
	 */
	private Map<String, String> normalizeLineKeys() {
		Map<String, String> normalizedProperties = new LinkedHashMap<String, String>();
		String normalizedProperty = null, value = null;
		for (Integer index : lineKeys.keySet()) {
			Map<String, String> properties = lineKeys.get(index);
			for(String property: properties.keySet()) {
				//linekey.%d.%s
				normalizedProperty = String.format(NORMALIZE_LINE_KEY, index, property);
				value = properties.get(property);
				normalizedProperties.put(normalizedProperty, value);
			}
		}
		return normalizedProperties;
	}
	
	/**
	 * Reverts the structure to a flat key = value structure for the memoryKeys
	 */
	private Map<String, String> normalizeMemoryKeys() {
		Map<String, String> normalizedProperties = new LinkedHashMap<String, String>();
		String normalizedProperty = null, value = null;
		for (Integer index : memoryKeys.keySet()) {
			Map<String, String> properties = memoryKeys.get(index);
			for(String property: properties.keySet()) {
				//memorykey.%d.%s
				normalizedProperty = String.format(NORMALIZE_MEMORY_KEY, index, property);
				value = properties.get(property);
				normalizedProperties.put(normalizedProperty, value);
			}
		}
		return normalizedProperties;
	}
	
	/**
	 * Reverts the structure to a flat key = value structure for the expModKeys
	 */
	private Map<String, String> normalizeExpModKeys() {
		Map<String, String> normalizedProperties = new LinkedHashMap<String, String>();
		String normalizedProperty = null, value = null;
		for (Integer expModIndex : expansionKeys.keySet()) {
			Map<Integer, Map<String, String>> expMod = expansionKeys.get(expModIndex);
			for(Integer keyIndex: expMod.keySet()) {
				Map<String, String> properties = expMod.get(keyIndex);
				for(String property: properties.keySet()) {
					//expansion_module.%d.key.%d.%s
					normalizedProperty = String.format(NORMALIZE_EXPMOD_KEY, expModIndex, keyIndex, property);
					value = properties.get(property);
					normalizedProperties.put(normalizedProperty, value);
				}
			}
		}
		return normalizedProperties;
	}
}
