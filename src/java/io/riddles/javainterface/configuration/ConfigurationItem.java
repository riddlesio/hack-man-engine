package io.riddles.javainterface.configuration;

/**
 * io.riddles.javainterface.configuration.ConfigurationItem - Created on 30-8-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
class ConfigurationItem {

    private Object value;
    private String type;

    ConfigurationItem(String type, Object value) {
        this.value = value;
        this.type = type;
    }

    String getType() {
        return type;
    }

    Object getValue() {
        return value;
    }
}
