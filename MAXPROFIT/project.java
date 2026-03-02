public class project {

    private int projectId;
    private String title;
    private String submissionDay;
    private int deadline;
    private double expectedRevenue;

    private int remainingDeadline;
    private String missReason;

    // New fields for predictive analysis
    private String status; // "pending", "completed", "carried_over"
    private double actualRevenue;
    private double predictedScore;

    // Constructor used when fetching from database
    public project(String title, int deadline, double expectedRevenue, String submissionDay) {

        this.title = title;
        this.deadline = deadline;
        this.expectedRevenue = expectedRevenue;
        this.submissionDay = submissionDay;
        this.remainingDeadline = deadline;
        this.status = "pending";
        this.actualRevenue = 0;
    }

    // Constructor with ID (optional use)
    public project(int projectId, String title, String submissionDay, int deadline, double expectedRevenue) {

        this.projectId = projectId;
        this.title = title;
        this.submissionDay = submissionDay;
        this.deadline = deadline;
        this.expectedRevenue = expectedRevenue;
        this.remainingDeadline = deadline;
        this.status = "pending";
        this.actualRevenue = 0;
    }

    // Full constructor with status and actual revenue
    public project(int projectId, String title, String submissionDay,
            int deadline, double expectedRevenue,
            String status, double actualRevenue) {

        this.projectId = projectId;
        this.title = title;
        this.submissionDay = submissionDay;
        this.deadline = deadline;
        this.expectedRevenue = expectedRevenue;
        this.remainingDeadline = deadline;
        this.status = status;
        this.actualRevenue = actualRevenue;
    }

    // Getter and Setter for projectId
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubmissionDay() {
        return submissionDay;
    }

    public int getDeadline() {
        return deadline;
    }

    public double getExpectedRevenue() {
        return expectedRevenue;
    }

    public int getRemainingDeadline() {
        return remainingDeadline;
    }

    public void setRemainingDeadline(int remainingDeadline) {
        this.remainingDeadline = remainingDeadline;
    }

    public String getMissReason() {
        return missReason;
    }

    public void setMissReason(String missReason) {
        this.missReason = missReason;
    }

    // New getters/setters for predictive analysis
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getActualRevenue() {
        return actualRevenue;
    }

    public void setActualRevenue(double actualRevenue) {
        this.actualRevenue = actualRevenue;
    }

    public double getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(double predictedScore) {
        this.predictedScore = predictedScore;
    }
}
