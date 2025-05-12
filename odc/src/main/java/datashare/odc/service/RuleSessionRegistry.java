package datashare.odc.service;

import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleSessionRegistry {

    private final Map<String, KieSession> sessionMap = new ConcurrentHashMap<>();

    public void register(String ruleId, KieSession kieSession) {
        sessionMap.put(ruleId, kieSession);
    }

    public Optional<KieSession> getSession(String ruleId) {
        return Optional.ofNullable(sessionMap.get(ruleId));
    }

    public void dispose(String ruleId) {
        Optional.ofNullable(sessionMap.get(ruleId)).ifPresent(KieSession::dispose);
    }

    public void clearAll() {
        sessionMap.values().forEach(KieSession::dispose);
        sessionMap.clear();
    }
}
