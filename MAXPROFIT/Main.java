import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static ProjectDoa dao = new ProjectDoa();

    static ProjectScheduler scheduler = new ProjectScheduler();

    public static void main(String[] args) throws Exception {

        while (true) {

            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║   📈 ProfitMax Predictive Scheduler          ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. Add Project                              ║");
            System.out.println("║  2. View All Projects                        ║");
            System.out.println("║  3. Generate Schedule (This Week)            ║");
            System.out.println("║  4. Generate Schedule (Next Week)            ║");
            System.out.println("║  5. Mark Project Completed                   ║");
            System.out.println("║  6. View Week History & Averages             ║");
            System.out.println("║  7. Save This Week's Results                 ║");
            System.out.println("║  8. Exit                                     ║");
            System.out.println("╚══════════════════════════════════════════════╝");

            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    addProject();
                    break;

                case 2:
                    viewProjects();
                    break;

                case 3:
                    scheduler.generateThisWeekSchedule(dao);
                    break;

                case 4:
                    scheduler.generateNextWeekSchedule();
                    break;

                case 5:
                    markProjectCompleted();
                    break;

                case 6:
                    viewWeekHistory();
                    break;

                case 7:
                    saveWeekResults();
                    break;

                case 8:
                    System.out.println("Goodbye! 👋");
                    System.exit(0);
            }
        }
    }

    static void addProject() throws Exception {

        scanner.nextLine();

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Submission Day (e.g. monday, tuesday...): ");
        String submissionDay = scanner.nextLine();

        System.out.print("Enter Deadline (in calendar days, Sat/Sun included): ");
        int deadline = scanner.nextInt();

        System.out.print("Enter Expected Revenue: ");
        double revenue = scanner.nextDouble();

        project p = new project(title, deadline, revenue, submissionDay);

        dao.addProject(p);

        System.out.println("✅ Project added successfully.");
    }

    static void viewProjects() throws Exception {

        List<project> list = dao.getAllProjects();

        if (list.isEmpty()) {

            System.out.println("No projects found.");
            return;
        }

        System.out.println("\n===== All Projects =====");
        System.out.println(
                String.format("%-5s %-30s %-12s %-10s %-12s %-15s %-12s",
                        "ID", "Title", "SubDay",
                        "Deadline", "Revenue", "Status", "Actual Rev"));
        System.out.println("-".repeat(100));

        for (project p : list) {

            System.out.println(
                    String.format("%-5d %-30s %-12s %-10d ₹%-11.0f %-15s ₹%-11.0f",
                            p.getProjectId(),
                            p.getTitle(),
                            p.getSubmissionDay(),
                            p.getDeadline(),
                            p.getExpectedRevenue(),
                            p.getStatus(),
                            p.getActualRevenue()));
        }
    }

    static void markProjectCompleted() throws Exception {

        // Show pending projects first
        List<project> list = dao.getPendingProjects();

        if (list.isEmpty()) {
            System.out.println("No pending projects to complete.");
            return;
        }

        System.out.println("\n===== Pending Projects =====");

        for (project p : list) {
            System.out.println(
                    p.getProjectId()
                            + " | " + p.getTitle()
                            + " | Expected: ₹"
                            + String.format("%.0f", p.getExpectedRevenue()));
        }

        System.out.print("\nEnter Project ID to mark complete: ");
        int id = scanner.nextInt();

        System.out.print("Enter Actual Revenue earned: ");
        double actualRevenue = scanner.nextDouble();

        dao.markProjectCompleted(id, actualRevenue);

        System.out.println("✅ Project marked as completed!");
    }

    static void viewWeekHistory() throws Exception {

        System.out.println("\n===== 📊 Week History =====");

        List<String> report = dao.getWeekHistoryReport();

        if (report.isEmpty()) {
            System.out.println("No history found.");
            return;
        }

        for (String line : report) {
            System.out.println("  " + line);
        }

        // Show current averages
        double lastWeek = dao.getLastWeekAvgProfit();
        double last3Week = dao.getLast3WeekAvgProfit();
        double overall = dao.getOverallAvgWeeklyProfit();

        System.out.println("\n===== 📈 Averages Used for Prediction =====");
        System.out.println("  Last Week Avg/Project:    ₹"
                + String.format("%.0f", lastWeek));
        System.out.println("  Last 3 Weeks Avg/Project: ₹"
                + String.format("%.0f", last3Week));
        System.out.println("  Overall Avg Weekly Profit: ₹"
                + String.format("%.0f", overall));
    }

    static void saveWeekResults() throws Exception {

        List<project> scheduled = scheduler.getScheduledProjects();

        if (scheduled.isEmpty()) {
            System.out.println("No schedule generated yet. Generate schedule first (option 3).");
            return;
        }

        // Calculate totals
        double totalProfit = 0;
        int completed = 0;

        for (project p : scheduled) {
            totalProfit += p.getExpectedRevenue();
            completed++;
        }

        System.out.print("Enter current week number (1-52): ");
        int weekNum = scanner.nextInt();

        System.out.print("Enter year (e.g. 2026): ");
        int year = scanner.nextInt();

        dao.saveWeekHistory(weekNum, year, totalProfit, completed);

        // Auto carry-over missed projects
        List<project> missed = scheduler.getMissedProjects();

        for (project p : missed) {

            if (!"Expired".equals(p.getMissReason())
                    && p.getProjectId() > 0) {

                dao.markProjectCarriedOver(p.getProjectId());
            }
        }

        System.out.println("\n✅ Week results saved!");
        System.out.println("   Total Profit: ₹"
                + String.format("%.0f", totalProfit));
        System.out.println("   Projects Scheduled: " + completed);
        System.out.println("   Projects Carried Over: " + missed.size());
    }
}
