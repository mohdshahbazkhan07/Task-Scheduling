import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDoa {

    // ==================== PROJECT CRUD ====================

    public void addProject(project project) throws SQLException {

        String sql = "INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = DBconnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, project.getTitle());
        stmt.setInt(2, project.getDeadline());
        stmt.setDouble(3, project.getExpectedRevenue());
        stmt.setString(4, project.getSubmissionDay());
        stmt.setString(5, "pending");
        stmt.setDouble(6, 0);

        stmt.executeUpdate();

        System.out.println("Project added successfully!");

        conn.close();
    }

    public List<project> getAllProjects() throws SQLException {

        List<project> list = new ArrayList<>();

        String sql = "SELECT * FROM projects";

        Connection conn = DBconnection.getConnection();

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {

            project p = new project(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("submission_day"),
                    rs.getInt("deadline"),
                    rs.getDouble("revenue"),
                    rs.getString("status"),
                    rs.getDouble("actual_revenue"));

            list.add(p);
        }

        conn.close();

        return list;
    }

    /**
     * Get only pending and carried_over projects (for scheduling)
     */
    public List<project> getPendingProjects() throws SQLException {

        List<project> list = new ArrayList<>();

        String sql = "SELECT * FROM projects WHERE status IN ('pending', 'carried_over')";

        Connection conn = DBconnection.getConnection();

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {

            project p = new project(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("submission_day"),
                    rs.getInt("deadline"),
                    rs.getDouble("revenue"),
                    rs.getString("status"),
                    rs.getDouble("actual_revenue"));

            list.add(p);
        }

        conn.close();

        return list;
    }

    /**
     * Mark a project as completed with actual revenue
     */
    public void markProjectCompleted(int projectId, double actualRevenue)
            throws SQLException {

        String sql = "UPDATE projects SET status = 'completed', actual_revenue = ? WHERE id = ?";

        Connection conn = DBconnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setDouble(1, actualRevenue);
        stmt.setInt(2, projectId);

        int rows = stmt.executeUpdate();

        if (rows > 0)
            System.out.println("Project marked as completed!");
        else
            System.out.println("Project not found.");

        conn.close();
    }

    /**
     * Mark a project as carried over to next week
     */
    public void markProjectCarriedOver(int projectId)
            throws SQLException {

        String sql = "UPDATE projects SET status = 'carried_over' WHERE id = ?";

        Connection conn = DBconnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setInt(1, projectId);

        stmt.executeUpdate();

        conn.close();
    }

    // ==================== WEEK HISTORY ====================

    /**
     * Get last week's average profit (most recent week entry with profit > 0)
     */
    public double getLastWeekAvgProfit() throws SQLException {

        String sql = "SELECT total_profit / NULLIF(projects_completed, 0) AS avg_profit "
                + "FROM week_history "
                + "WHERE total_profit > 0 "
                + "ORDER BY year DESC, week_number DESC "
                + "LIMIT 1";

        Connection conn = DBconnection.getConnection();

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);

        double avg = 0;

        if (rs.next())
            avg = rs.getDouble("avg_profit");

        conn.close();

        return avg;
    }

    /**
     * Get last 3 weeks' average profit
     */
    public double getLast3WeekAvgProfit() throws SQLException {

        String sql = "SELECT AVG(total_profit / NULLIF(projects_completed, 0)) AS avg_profit "
                + "FROM ("
                + "  SELECT total_profit, projects_completed "
                + "  FROM week_history "
                + "  WHERE total_profit > 0 "
                + "  ORDER BY year DESC, week_number DESC "
                + "  LIMIT 3"
                + ") sub";

        Connection conn = DBconnection.getConnection();

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);

        double avg = 0;

        if (rs.next())
            avg = rs.getDouble("avg_profit");

        conn.close();

        return avg;
    }

    /**
     * Get overall average weekly profit (all history)
     */
    public double getOverallAvgWeeklyProfit() throws SQLException {

        String sql = "SELECT AVG(total_profit) AS avg_profit "
                + "FROM week_history "
                + "WHERE total_profit > 0";

        Connection conn = DBconnection.getConnection();

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);

        double avg = 0;

        if (rs.next())
            avg = rs.getDouble("avg_profit");

        conn.close();

        return avg;
    }

    /**
     * Save this week's results to history
     */
    public void saveWeekHistory(int weekNumber, int year,
            double totalProfit, int projectsCompleted)
            throws SQLException {

        // Check if entry already exists
        String checkSql = "SELECT id FROM week_history WHERE week_number = ? AND year = ?";

        Connection conn = DBconnection.getConnection();

        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setInt(1, weekNumber);
        checkStmt.setInt(2, year);

        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {

            // Update existing
            String updateSql = "UPDATE week_history SET total_profit = ?, projects_completed = ? "
                    + "WHERE week_number = ? AND year = ?";

            PreparedStatement updateStmt = conn.prepareStatement(updateSql);

            updateStmt.setDouble(1, totalProfit);
            updateStmt.setInt(2, projectsCompleted);
            updateStmt.setInt(3, weekNumber);
            updateStmt.setInt(4, year);

            updateStmt.executeUpdate();

            System.out.println("Week history updated!");

        } else {

            // Insert new
            String insertSql = "INSERT INTO week_history (week_number, year, total_profit, projects_completed) "
                    + "VALUES (?, ?, ?, ?)";

            PreparedStatement insertStmt = conn.prepareStatement(insertSql);

            insertStmt.setInt(1, weekNumber);
            insertStmt.setInt(2, year);
            insertStmt.setDouble(3, totalProfit);
            insertStmt.setInt(4, projectsCompleted);

            insertStmt.executeUpdate();

            System.out.println("Week history saved!");
        }

        conn.close();
    }

    /**
     * Get all week history entries
     */
    public List<String> getWeekHistoryReport() throws SQLException {

        List<String> report = new ArrayList<>();

        String sql = "SELECT * FROM week_history ORDER BY year, week_number";

        Connection conn = DBconnection.getConnection();

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {

            int wk = rs.getInt("week_number");
            int yr = rs.getInt("year");
            double tp = rs.getDouble("total_profit");
            int pc = rs.getInt("projects_completed");

            double avgPerProject = pc > 0 ? tp / pc : 0;

            report.add(
                    "Week " + wk + "/" + yr
                            + " | Profit: ₹" + String.format("%.0f", tp)
                            + " | Projects: " + pc
                            + " | Avg/Project: ₹" + String.format("%.0f", avgPerProject));
        }

        conn.close();

        return report;
    }
}
