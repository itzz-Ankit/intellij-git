package cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import entity.ConfigJournalAppEntity;
import repository.ConfigJournalAppRepository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum Keys {
        WEATHER_API
    }

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository ;

    public Map<String, String> appCache = new HashMap<>();

    @PostConstruct
    public void init() {
        appCache.clear();
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll() ;

        for (ConfigJournalAppEntity config : all) {
            appCache.put(config.getKeys(), config.getValues());
        }
    }
}
