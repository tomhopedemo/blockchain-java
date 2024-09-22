package crypto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {

    private static Map getMap(Class<? extends Request> type) {
        return Request.getRequestMap(type);
    }

    public static void add(String id, Request request) {
        Map requests = getMap(request.getClass());
        ((List) requests.computeIfAbsent(id, _ -> new ArrayList<>())).add(request);
    }

    public static List<? extends Request> get(String id, Class<? extends Request> type) {
        return (List) getMap(type).get(id);
    }

    public static void remove(String id, List<? extends Request> requests, Class<? extends Request> type) {
        List<? extends Request> found = get(id, type);
        if (found != null) found.removeAll(requests);
    }

}
