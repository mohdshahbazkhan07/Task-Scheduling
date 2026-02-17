import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDoa {

    // Insert Project
    public void addProject(project project) throws SQLException {

        String sql = "INSERT INTO projects (title, deadline, revenue) VALUES (?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getTitle());
            stmt.setInt(2, project.getDeadline());
            stmt.setDouble(3, project.getExpectedRevenue());

            stmt.executeUpdate();
            System.out.println("Project added successfully!");
        }
    }

    // Fetch All Projects
    public List<project> getAllProjects() throws SQLException {

        List<project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";

        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                project project = new project(
                        rs.getString("title"),
                        rs.getInt("deadline"),
                        rs.getDouble("revenue")
                );
                project.setProjectId(rs.getInt("id"));

                projects.add(project);
            }
        }

        return projects;
    }
}