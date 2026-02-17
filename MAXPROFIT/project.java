public class project {

    private int projectId;
    private String title;
    private int deadline;        // 1 to 5 days
    private double expectedRevenue;

    // Constructor
    public project(String title, int deadline, double expectedRevenue) {
        this.title = title;
        this.deadline = deadline;
        this.expectedRevenue = expectedRevenue;
    }

    // Getters and Setters
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public double getExpectedRevenue() {
        return expectedRevenue;
    }

    public void setExpectedRevenue(double expectedRevenue) {
        this.expectedRevenue = expectedRevenue;
    }

    // Optional: toString() for printing
    @Override
    public String toString() {
        return "Project{" +
                "ID=" + projectId +
                ", Title='" + title + '\'' +
                ", Deadline=" + deadline +
                ", Revenue=" + expectedRevenue +
                '}';
    }
}