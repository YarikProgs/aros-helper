package net.aros.brain.internet;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import net.aros.util.EnvUtils;

import java.util.HashMap;
import java.util.Map;

public class InternetModule {
    public static final String SEARCH_ENGINE = "002845322276752338984:vxqzfa86nqc";

    private Customsearch customsearch;
    private boolean enabled, init;

    public void init() throws Throwable {
        customsearch = new Customsearch.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory(), null)
                .setApplicationName("U.T.K.E.R-SEARCH")
                .setGoogleClientRequestInitializer(new CustomsearchRequestInitializer(EnvUtils.getToken(".env")))
                .build();
        init = true;
    }

    public Map<String, String> search(String query) throws Throwable {
        Map<String, String> results = new HashMap<>();

        Search result = customsearch.cse().list(query).setCx(SEARCH_ENGINE).execute();
        if (result.getItems() != null) for (Result ri : result.getItems()) results.put(ri.getTitle(), ri.getLink());
        return results;
    }

    public boolean isInit() {
        return init;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
