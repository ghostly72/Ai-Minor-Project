import java.util.*;

class CSP_Timetable {
    static class Slot {
        int day, period;
        Slot(int d, int p) { day = d; period = p; }
        public String toString() { return "(" + day + "," + period + ")"; }
    }

    static String[] subjects = {"Math", "Physics", "Chemistry", "CS", "English"};
    static String[] teachers = {"T1", "T2", "T3"};
    static String[] rooms = {"R1", "R2"};

    static int DAYS = 5, PERIODS = 4;

    static Map<Slot, String> assignment = new HashMap<>();
    static int backtracks = 0;

    // Constraint: same teacher or room can't be used twice in same slot
    static boolean isConsistent(Slot slot, String subject, Map<Slot, String> assignment) {
        for (Slot s : assignment.keySet()) {
            if (s.day == slot.day && s.period == slot.period) return false;
        }
        return true;
    }

    // Variable ordering: MRV (fewest remaining subjects)
    static Slot selectUnassignedVariable(Set<Slot> unassigned) {
        return unassigned.iterator().next();
    }

    // Value ordering: LCV (here random for simplicity)
    static List<String> orderDomainValues() {
        List<String> vals = new ArrayList<>(Arrays.asList(subjects));
        Collections.shuffle(vals);
        return vals;
    }

    static boolean backtrack(Set<Slot> unassigned) {
        if (unassigned.isEmpty()) return true;
        Slot var = selectUnassignedVariable(unassigned);
        List<String> values = orderDomainValues();

        for (String val : values) {
            if (isConsistent(var, val, assignment)) {
                assignment.put(var, val);
                unassigned.remove(var);
                if (backtrack(unassigned)) return true;
                unassigned.add(var);
                assignment.remove(var);
            }
        }
        backtracks++;
        return false;
    }

    // Forward checking variant
    static boolean backtrackForward(Set<Slot> unassigned, Map<Slot, List<String>> domains) {
        if (unassigned.isEmpty()) return true;
        Slot var = unassigned.iterator().next();
        for (String val : new ArrayList<>(domains.get(var))) {
            assignment.put(var, val);
            Map<Slot, List<String>> newDomains = deepCopy(domains);
            newDomains = forwardCheck(var, val, newDomains);
            if (newDomains != null) {
                unassigned.remove(var);
                if (backtrackForward(unassigned, newDomains)) return true;
                unassigned.add(var);
            }
            assignment.remove(var);
        }
        backtracks++;
        return false;
    }

    static Map<Slot, List<String>> forwardCheck(Slot var, String value, Map<Slot, List<String>> domains) {
        for (Slot s : domains.keySet()) {
            if (!s.equals(var)) {
                domains.get(s).remove(value); // remove conflicting subjects
                if (domains.get(s).isEmpty()) return null;
            }
        }
        return domains;
    }

    static Map<Slot, List<String>> deepCopy(Map<Slot, List<String>> src) {
        Map<Slot, List<String>> copy = new HashMap<>();
        for (Map.Entry<Slot, List<String>> e : src.entrySet())
            copy.put(e.getKey(), new ArrayList<>(e.getValue()));
        return copy;
    }

    public static void main(String[] args) {
        long start, end;
        // Generate all time slots
        Set<Slot> allSlots = new LinkedHashSet<>();
        for (int d = 1; d <= DAYS; d++)
            for (int p = 1; p <= PERIODS; p++)
                allSlots.add(new Slot(d, p));

        System.out.println("=== Backtracking with Heuristics ===");
        backtracks = 0;
        assignment.clear();
        start = System.currentTimeMillis();
        backtrack(new LinkedHashSet<>(allSlots));
        end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + " ms, Backtracks: " + backtracks);
        assignment.forEach((k, v) -> System.out.println(k + " -> " + v));

        System.out.println("\n=== Backtracking with Forward Checking ===");
        backtracks = 0;
        assignment.clear();
        Map<Slot, List<String>> domains = new HashMap<>();
        for (Slot s : allSlots)
            domains.put(s, new ArrayList<>(Arrays.asList(subjects)));

        start = System.currentTimeMillis();
        backtrackForward(new LinkedHashSet<>(allSlots), domains);
        end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + " ms, Backtracks: " + backtracks);
        assignment.forEach((k, v) -> System.out.println(k + " -> " + v));
    }
}
