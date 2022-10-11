package info.scce.addlib.gencodegenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties extends Properties {

    private static final long serialVersionUID = 3009250760959897365L;

    private static AppProperties instance = null;

    private AppProperties() {
        /* Hide constructor */
    }

    public static AppProperties getInstance() throws IOException {
        if (instance == null)
            instance = new AppProperties().load();
        return instance;
    }

    public AppProperties load() throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("app.properties");
        try {
            load(in);
        } finally {
            in.close();
        }
        return this;
    }

    public String getAppName() {
        return getProperty("app.name");
    }

    public String getAppVersion() {
        return getProperty("app.version");
    }
}
