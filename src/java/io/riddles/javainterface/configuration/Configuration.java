package io.riddles.javainterface.configuration;

import org.json.JSONObject;

import java.util.HashMap;

import io.riddles.javainterface.exception.ConfigurationException;

/**
 * io.riddles.javainterface.configuration.Configuration - Created on 30-8-16
 *
 * Stores a configuration item
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class Configuration {

    private HashMap<String, ConfigurationItem> values;

    public Configuration() {
        this.values = new HashMap<>();
    }

    public void put(String key, JSONObject configInput) {
        String type = configInput.getString("type").toLowerCase();
        Object value = configInput.get("value");

        ConfigurationItem configurationItem = new ConfigurationItem(type, value);
        this.values.put(key, configurationItem);
    }

    public void put(String key, int value) {
        ConfigurationItem configurationItem = new ConfigurationItem("integer", value);
        this.values.put(key, configurationItem);
    }

    public void put(String key, String value) {
        ConfigurationItem configurationItem = new ConfigurationItem("string", value);
        this.values.put(key, configurationItem);
    }

    public void put(String key, double value) {
        ConfigurationItem configurationItem = new ConfigurationItem("double", value);
        this.values.put(key, configurationItem);
    }

    private ConfigurationItem get(String key) {
        ConfigurationItem item = this.values.get(key);

        if (item == null) {
            throw new ConfigurationException(
                    String.format("Configuration with key %s not found.", key));
        }

        return item;
    }

    private void checkType(ConfigurationItem item, String expected) throws ConfigurationException {
        String type = item.getType();
        if (!type.equals(expected)) {
            throw new ConfigurationException(
                    String.format("Can't get integer when type is %s.", type));
        }
    }

    public int getInt(String key) {
        ConfigurationItem item = get(key);
        checkType(item, "integer");

        try {
            return (int) item.getValue();
        } catch (Exception ex) {
            throw new ConfigurationException("Can't convert value to integer.");
        }
    }

    public String getString(String key) {
        ConfigurationItem item = get(key);
        checkType(item, "string");

        try {
            return (String) item.getValue();
        } catch (Exception ex) {
            throw new ConfigurationException("Can't convert value to String.");
        }
    }

    public double getDouble(String key) {
        ConfigurationItem item = get(key);
        checkType(item, "double");

        try {
            return (double) item.getValue();
        } catch (Exception ex) {
            throw new ConfigurationException("Can't convert value to double.");
        }
    }
}
