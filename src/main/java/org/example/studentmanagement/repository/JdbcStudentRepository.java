package org.example.studentmanagement.repository;

import org.example.studentmanagement.model.Student;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcStudentRepository implements StudentRepository {

    private final DataSource dataSource;

    public JdbcStudentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Student student) {
        String sql = "INSERT INTO students (full_name, age, email) VALUES (?, ?, ?)";
        String fullName = student.getFirstName().trim() + " " + student.getLastName().trim();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fullName);
            statement.setInt(2, student.getAge());
            statement.setString(3, student.getEmail());
            statement.executeUpdate();

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalArgumentException("This email is already registered.");
            }
            if ("23514".equals(e.getSQLState())) {
                throw new IllegalArgumentException("Age must be greater than 18.");
            }
            throw new IllegalStateException("Could not save the student: " + e.getMessage());
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT id, full_name, age, email FROM students ORDER BY id";
        List<Student> students = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                students.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load students: " + e.getMessage());
        }
        return students;
    }

    @Override
    public Optional<Student> findById(int id) {
        String sql = "SELECT id, full_name, age, email FROM students WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load the student: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Student mapRow(ResultSet resultSet) throws SQLException {
        String fullName = resultSet.getString("full_name");
        String[] parts = fullName.split(" ", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        return new Student(
                resultSet.getInt("id"),
                firstName,
                lastName,
                resultSet.getString("email"),
                resultSet.getInt("age"));
    }
}