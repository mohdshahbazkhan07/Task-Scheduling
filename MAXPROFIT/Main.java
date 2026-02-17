import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static ProjectDoa dao = new ProjectDoa();
    static projectSheduler scheduler = new projectSheduler();

    public static void main(String[] args) throws Exception {

        while (true) {

            System.out.println("\n1. Add Project");
            System.out.println("2. View Projects");
            System.out.println("3. Generate Schedule");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();

            switch (choice) {

                case 1 -> addProject();
                case 2 -> viewProjects();
                case 3 -> generateSchedule();
                case 4 -> exitfunct();
            }
        }
    }

    static  void exitfunct()
    {
        System.out.println("Thank You For Using The Project Sheduler");
        System.exit(0);
    }
    static void addProject() throws Exception {

        scanner.nextLine();

        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Deadline : ");
        int deadline = scanner.nextInt();

        System.out.print("Revenue: ");
        double revenue = scanner.nextDouble();

        project project = new project(title, deadline, revenue);
        dao.addProject(project);
    }

    static void viewProjects() throws Exception {
        List<project> projects = dao.getAllProjects();
        projects.forEach(System.out::println);
    }

    static void generateSchedule() throws Exception {

        List<project> projects = dao.getAllProjects();
        List<project> schedule = scheduler.generateOptimalSchedule(projects);

        System.out.println("\nOptimal Weekly Schedule:");
        int day = 1;

        for (project p : schedule) {
            System.out.println("Day " + day++ + " -> " + p.getTitle());
        }
    }
}