package privacyTransformation;

import java.util.ArrayList;

public class Configuration {

    public class ConfigurationItem {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConfigurationItem that = (ConfigurationItem) o;

            if (Double.compare(that.value, value) != 0) return false;
            return type == that.type;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = type != null ? type.hashCode() : 0;
            temp = Double.doubleToLongBits(value);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        public TransformationTypes type;
        public double value;

        public ConfigurationItem(TransformationTypes type, double configurationValue) {
            this.type = type;
            this.value = configurationValue;
        }
    }

    private final ArrayList<ConfigurationItem> items;

    public Configuration() {
        this.items = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        return items != null ? items.equals(that.items) : that.items == null;
    }

    @Override
    public int hashCode() {
        return items != null ? items.hashCode() : 0;
    }

    public void add(TransformationTypes type, double configurationValue) {
        items.add(new ConfigurationItem(type, configurationValue));

    }
}
