import java.util.*;

public class projectSheduler {

    public List<project> generateOptimalSchedule(List<project> projects) {

        int maxDays = 5;

        // Sort by revenue descending
        projects.sort((a, b) ->
                Double.compare(b.getExpectedRevenue(), a.getExpectedRevenue()));

        project[] schedule = new project[maxDays];

        for (project project : projects) {

            // If deadline is more than working days, limit it
            int deadline = Math.min(project.getDeadline(), maxDays);

            // Try to schedule on latest possible day
            for (int day = deadline - 1; day >= 0; day--) {

                if (schedule[day] == null) {
                    schedule[day] = project;
                    break;
                }
            }
        }

        return Arrays.stream(schedule)
                .filter(Objects::nonNull)
                .toList();
    }
    }

