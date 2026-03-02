import java.util.*;

public class ProjectScheduler {

    private static final int MAX_WORKING_DAYS = 5;

    private List<project> missedProjects = new ArrayList<>();
    private List<project> futureProjects = new ArrayList<>();
    private List<project> scheduledProjects = new ArrayList<>();

    private double lastWeekAvg = 0;
    private double last3WeekAvg = 0;

    /**
     * Generate THIS WEEK's predictive schedule.
     * Automatically fetches averages from database.
     */
    public void generateThisWeekSchedule(ProjectDoa dao) throws Exception {

        List<project> projects = dao.getPendingProjects();

        if (projects.isEmpty()) {
            System.out.println("No pending projects available.");
            return;
        }

        // Fetch historical averages from database (no user input needed)
        lastWeekAvg = dao.getLastWeekAvgProfit();
        last3WeekAvg = dao.getLast3WeekAvgProfit();

        System.out.println("\n📊 Predictive Analysis Data:");
        System.out.println("   Last Week Avg Profit/Project: ₹"
                + String.format("%.0f", lastWeekAvg));
        System.out.println("   Last 3 Weeks Avg Profit/Project: ₹"
                + String.format("%.0f", last3WeekAvg));

        // Scheduling day is always Sunday (as per requirement)
        String schedulingDay = "sunday";

        Scanner scanner = new Scanner(System.in);

        System.out.print("\nEnter Busy Days for this week (0-5): ");
        int busyDays = scanner.nextInt();

        List<project> schedule = generatePredictiveSchedule(
                projects, schedulingDay, busyDays);

        printSchedule(schedule, "THIS WEEK (PREDICTIVE)");
    }

    /**
     * Generate NEXT WEEK's predictive schedule.
     * Includes carried-over and future projects.
     */
    public void generateNextWeekSchedule() {

        System.out.println("\n===== Predictive Schedule for NEXT WEEK =====");

        // Combine future projects with non-expired missed projects
        List<project> nextWeekCandidates = new ArrayList<>(futureProjects);

        for (project p : missedProjects) {
            if (!"Expired".equals(p.getMissReason())) {
                nextWeekCandidates.add(p);
            }
        }

        if (nextWeekCandidates.isEmpty()) {
            System.out.println("No projects available for next week.");
            return;
        }

        // Calculate predicted score for each project
        for (project p : nextWeekCandidates) {

            double predictedProfit = calculatePredictedProfit(
                    p.getExpectedRevenue());

            p.setPredictedScore(predictedProfit);
        }

        // Sort by predicted score descending
        nextWeekCandidates.sort((a, b) -> Double.compare(
                b.getPredictedScore(),
                a.getPredictedScore()));

        int day = 1;
        double total = 0;

        System.out.println(
                String.format("%-6s %-30s %-15s %-18s %-18s %s",
                        "Day", "Title", "Revenue",
                        "Predicted", "Rem. Deadline", "Status"));
        System.out.println("-".repeat(110));

        for (project p : nextWeekCandidates) {

            String tag = missedProjects.contains(p)
                    ? "[Carried Over]"
                    : "[Future]";

            System.out.println(
                    String.format("%-6d %-30s ₹%-14.0f ₹%-17.0f %-18d %s",
                            day++,
                            p.getTitle(),
                            p.getExpectedRevenue(),
                            p.getPredictedScore(),
                            p.getRemainingDeadline(),
                            tag));

            total += p.getPredictedScore();

            if (day > MAX_WORKING_DAYS)
                break;
        }

        System.out.println("\n💰 Predicted Total Profit: ₹"
                + String.format("%.0f", total));
    }

    /**
     * Core predictive scheduling algorithm.
     *
     * Steps:
     * 1. Calculate remaining deadline (calendar days including Sat/Sun)
     * 2. Convert to remaining working days
     * 3. Compute predicted profit using averages
     * 4. Sort by predicted profit DESC, then urgency ASC
     * 5. Greedy job sequencing into 5-day slots
     * 6. Auto carry-over for unscheduled projects
     */
    public List<project> generatePredictiveSchedule(
            List<project> projects,
            String schedulingDay,
            int busyDays) {

        missedProjects.clear();
        futureProjects.clear();
        scheduledProjects.clear();

        project[] slots = new project[MAX_WORKING_DAYS];
        boolean[] filled = new boolean[MAX_WORKING_DAYS];

        // Mark busy days
        for (int i = 0; i < busyDays && i < MAX_WORKING_DAYS; i++) {
            filled[i] = true;
        }

        List<project> currentWeek = new ArrayList<>();

        // No planning offset needed — getDaysBetween already
        // measures days passed from submission to scheduling day
        int planningOffset = 0;

        // STEP 1: Classify projects
        for (project p : projects) {

            int calendarDaysPassed = getDaysBetween(
                    p.getSubmissionDay(),
                    schedulingDay);

            // Remaining deadline in calendar days
            int remainingCalendarDays = p.getDeadline() - (calendarDaysPassed + planningOffset);

            // Convert to working days (exclude weekends)
            int remainingWorkingDays = calendarToWorkingDays(remainingCalendarDays);

            p.setRemainingDeadline(remainingWorkingDays);

            if (remainingWorkingDays < 0) {

                p.setMissReason("Expired");
                missedProjects.add(p);
            } else if (remainingWorkingDays <= MAX_WORKING_DAYS) {

                // Calculate predicted profit
                double predicted = calculatePredictedProfit(
                        p.getExpectedRevenue());

                p.setPredictedScore(predicted);

                currentWeek.add(p);
            } else {
                // Calculate predicted profit for future too
                double predicted = calculatePredictedProfit(
                        p.getExpectedRevenue());

                p.setPredictedScore(predicted);

                futureProjects.add(p);
            }
        }

        // STEP 2: Sort by predicted profit DESC, then urgency ASC
        currentWeek.sort((a, b) -> {

            int profitCompare = Double.compare(
                    b.getPredictedScore(),
                    a.getPredictedScore());

            if (profitCompare != 0)
                return profitCompare;

            return Integer.compare(
                    a.getRemainingDeadline(),
                    b.getRemainingDeadline());
        });

        // STEP 3: Greedy job sequencing
        for (project p : currentWeek) {

            int deadline = Math.min(
                    p.getRemainingDeadline(), MAX_WORKING_DAYS);

            boolean placed = false;

            for (int d = deadline - 1; d >= 0; d--) {

                if (!filled[d]) {

                    filled[d] = true;
                    slots[d] = p;
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                // Could not fit → carry over to next week
                p.setMissReason("Low Predicted Priority");
                p.setStatus("carried_over");
                missedProjects.add(p);
            }
        }

        // STEP 4: Collect scheduled projects
        List<project> schedule = new ArrayList<>();

        for (int i = 0; i < MAX_WORKING_DAYS; i++) {

            if (slots[i] != null) {
                schedule.add(slots[i]);
                scheduledProjects.add(slots[i]);
            }
        }

        return schedule;
    }

    /**
     * PREDICTIVE PROFIT FORMULA
     *
     * predictedProfit = (expectedRevenue + lastWeekAvg + last3WeekAvg) / 3.0
     *
     * This blends the project's expected revenue with historical
     * performance data to get a more realistic profit prediction.
     */
    private double calculatePredictedProfit(double expectedRevenue) {

        // If no history, fall back to expected revenue
        if (lastWeekAvg == 0 && last3WeekAvg == 0)
            return expectedRevenue;

        return (expectedRevenue + lastWeekAvg + last3WeekAvg) / 3.0;
    }

    /**
     * Convert calendar days to working days.
     * Sat/Sun count in calendar but are not working days.
     *
     * Example: 7 calendar days = 5 working days
     * 5 calendar days (Fri deadline) = ~3-4 working days
     */
    private int calendarToWorkingDays(int calendarDays) {

        if (calendarDays <= 0)
            return calendarDays;

        int fullWeeks = calendarDays / 7;
        int remainingDays = calendarDays % 7;

        // Each full week has 5 working days
        int workingDays = fullWeeks * 5;

        // For remaining days, cap weekends
        workingDays += Math.min(remainingDays, 5);

        return workingDays;
    }

    /**
     * Print the schedule with predictive analysis details
     */
    private void printSchedule(
            List<project> schedule,
            String week) {

        System.out.println("\n===== Schedule for " + week + " =====");

        if (schedule.isEmpty()) {
            System.out.println("No projects scheduled.");
        } else {

            System.out.println(
                    String.format("%-6s %-30s %-15s %-18s %-18s %s",
                            "Day", "Title", "Revenue",
                            "Predicted", "Rem. Deadline", "Status"));
            System.out.println("-".repeat(110));

            int day = 1;
            double totalExpected = 0;
            double totalPredicted = 0;

            for (project p : schedule) {

                String statusTag = "carried_over".equals(p.getStatus())
                        ? "[Carried Over]"
                        : "[New]";

                System.out.println(
                        String.format("%-6d %-30s ₹%-14.0f ₹%-17.0f %-18d %s",
                                day++,
                                p.getTitle(),
                                p.getExpectedRevenue(),
                                p.getPredictedScore(),
                                p.getRemainingDeadline(),
                                statusTag));

                totalExpected += p.getExpectedRevenue();
                totalPredicted += p.getPredictedScore();
            }

            System.out.println("\n💰 Expected Total: ₹"
                    + String.format("%.0f", totalExpected));
            System.out.println("📈 Predicted Total: ₹"
                    + String.format("%.0f", totalPredicted));

            double diff = totalPredicted - totalExpected;
            String trend = diff >= 0 ? "▲" : "▼";

            System.out.println("📊 Prediction vs Expected: "
                    + trend + " ₹" + String.format("%.0f", Math.abs(diff)));
        }

        // Missed projects
        System.out.println("\n===== Missed / Carried Over Projects =====");

        if (missedProjects.isEmpty()) {
            System.out.println("None");
        } else {

            for (project p : missedProjects) {

                System.out.println(
                        "  ❌ " + p.getTitle()
                                + " | Reason: "
                                + p.getMissReason()
                                + " | Revenue: ₹"
                                + String.format("%.0f",
                                        p.getExpectedRevenue()));
            }
        }

        // Future projects
        System.out.println("\n===== Future Projects =====");

        if (futureProjects.isEmpty()) {
            System.out.println("None");
        } else {

            for (project p : futureProjects) {

                System.out.println(
                        "  📋 " + p.getTitle()
                                + " | Predicted: ₹"
                                + String.format("%.0f",
                                        p.getPredictedScore())
                                + " | Remaining: "
                                + p.getRemainingDeadline()
                                + " working days");
            }
        }
    }

    /**
     * Get scheduled projects (for saving week results)
     */
    public List<project> getScheduledProjects() {
        return scheduledProjects;
    }

    public List<project> getMissedProjects() {
        return missedProjects;
    }

    /**
     * Calculate days between two day names.
     * Days: monday=0, tuesday=1, ..., sunday=6
     */
    public static int getDaysBetween(
            String sub,
            String sch) {

        sub = sub.toLowerCase().trim();
        sch = sch.toLowerCase().trim();

        List<String> days = Arrays.asList(
                "monday",
                "tuesday",
                "wednesday",
                "thursday",
                "friday",
                "saturday",
                "sunday");

        int subIndex = days.indexOf(sub);
        int schIndex = days.indexOf(sch);

        if (subIndex == -1 || schIndex == -1)
            return 0;

        int diff = schIndex - subIndex;

        if (diff < 0)
            diff += 7;

        return diff;
    }
}
