package org.example.studentmanagement.controller;

import jakarta.validation.Valid;
import org.example.studentmanagement.model.Student;
import org.example.studentmanagement.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // GET /student/register -> show the empty form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    // POST /student/register -> receive the form and save the student
    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("student") Student student,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Form rules broken (empty field, bad email, age below 1): show the form again
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            studentService.registerStudent(student);
        } catch (IllegalArgumentException e) {
            // Database rules broken (duplicate email, age 18 or below)
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (IllegalStateException e) {
            model.addAttribute("error", "Something went wrong. Please try again.");
            return "register";
        }

        // Success: redirect so refreshing the page does not save the student twice
        redirectAttributes.addFlashAttribute("success", "Student registered successfully!");
        return "redirect:/student/register";
    }

    // GET /student/all -> show all students
    @GetMapping("/all")
    public String showAllStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    // Bonus: GET /student/{id} -> show one student, reusing students.html
    @GetMapping("/{id}")
    public String showStudentById(@PathVariable int id, Model model) {
        Optional<Student> student = studentService.getStudentById(id);

        if (student.isPresent()) {
            model.addAttribute("students", List.of(student.get()));
        } else {
            model.addAttribute("students", List.of());
            model.addAttribute("message", "No student found with ID " + id);
        }
        return "students";
    }
}
