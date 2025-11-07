package be.ucll.model;

import java.util.HashMap;
import java.util.Map;

public class RequestData {
    private Map<String, Object> fields = new HashMap<>();
    
    public void setField(String key, Object value) {
        fields.put(key, value);
    }
    
    public <T> T getField(String key, Class<T> type) {
        return type.cast(fields.get(key));
    }

    public Map<String, Object> getAllFields(){
        return new HashMap<>(fields);         
    }
}
