import java.util.ArrayList;
import java.util.List;

public class projectSheduler {

    private final int MAX_DAYS = 5;

    private List<project> bestSchedule = new ArrayList<>();

    private double maxProfit = 0;

    public List<project> generateOptimalSchedule(List<project> projects) {

        projects.sort((a, b) -> {
            if (Double.compare(b.getExpectedRevenue(), a.getExpectedRevenue()) != 0) {
                return Double.compare(b.getExpectedRevenue(), a.getExpectedRevenue());
            } else {
                return Integer.compare(a.getDeadline(), b.getDeadline());
            }
        });

        boolean[] usedDays = new boolean[MAX_DAYS];

        List<project> currentSchedule = new ArrayList<>();

        backtrack(projects, 0, usedDays, currentSchedule, 0);

        return bestSchedule;
    }

    private void backtrack(List<project> projects,
                           int index,
                           boolean[] usedDays,
                           List<project> currentSchedule,
                           double currentProfit) {

        if (currentProfit > maxProfit) {

            maxProfit = currentProfit;

            bestSchedule = new ArrayList<>(currentSchedule);
        }

        if (index >= projects.size()) {
            return;
        }

        project currentProject = projects.get(index);

        int deadline = Math.min(currentProject.getDeadline(), MAX_DAYS);

        for (int day = 0; day < deadline; day++) {

            if (!usedDays[day]) {

                usedDays[day] = true;

                currentSchedule.add(currentProject);

                backtrack(projects,
                        index + 1,
                        usedDays,
                        currentSchedule,
                        currentProfit + currentProject.getExpectedRevenue());

                usedDays[day] = false;

                currentSchedule.remove(currentSchedule.size() - 1);
            }
        }

        backtrack(projects,
                index + 1,
                usedDays,
                currentSchedule,
                currentProfit);
    }
}
