package demo.objects;

import java.util.*;

public record CSV (List<String> csvHeaderFields, List<List<String>> csvRows){}
