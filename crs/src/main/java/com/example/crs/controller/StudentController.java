
package com.example.crs.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crs.model.Student;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class StudentController {

    private static final Map<String, Student> students =
            new ConcurrentHashMap<>();

    static {
        Student defaultStudent = new Student(
                "1001",
                "123456",
                "Test Student",
                "student@example.com"
        );

        students.put(defaultStudent.getStudentId(), defaultStudent);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody Student student) {

        if (student == null
                || isBlank(student.getStudentId())
                || isBlank(student.getPassword())
                || isBlank(student.getName())
                || isBlank(student.getEmail())) {

            return ResponseEntity
                    .badRequest()
                    .body("All fields are required.");
        }

        Student existingStudent = students.putIfAbsent(
                student.getStudentId(), student);

        if (existingStudent != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Student ID already exists.");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Student registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody Student loginRequest) {

        if (loginRequest == null
                || isBlank(loginRequest.getStudentId())
                || isBlank(loginRequest.getPassword())) {

            return ResponseEntity
                    .badRequest()
                    .body("Student ID and password are required.");
        }

        Student student = students.get(loginRequest.getStudentId());

        if (student != null
                && student.getPassword()
                        .equals(loginRequest.getPassword())) {

            return ResponseEntity
                    .ok("Login successful.");
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid Student ID or password.");
    }

@PostMapping("/remove")
public ResponseEntity<Map<String, String>> removeAccount(
        @RequestBody Student removeRequest) {

    Map<String, String> response = new java.util.HashMap<>();

    if (removeRequest == null
            || isBlank(removeRequest.getStudentId())
            || isBlank(removeRequest.getPassword())) {

        response.put("message", "Student ID and password are required.");
        return ResponseEntity.badRequest().body(response);
    }

    String studentId = removeRequest.getStudentId();
    Student student = students.get(studentId);

    if (student == null) {
        response.put("message", "Student ID does not exist.");
        return ResponseEntity.badRequest().body(response);
    }

    if (!student.getPassword().equals(removeRequest.getPassword())) {
        response.put("message", "Incorrect password.");
        return ResponseEntity.badRequest().body(response);
    }

    if (!students.remove(studentId, student)) {
        response.put("message", "Unable to remove student account.");
        return ResponseEntity.badRequest().body(response);
    }

    response.put("message", "Student account removed successfully.");
    response.put("studentId", student.getStudentId());
    response.put("name", student.getName());
    response.put("email", student.getEmail());

    return ResponseEntity.ok(response);
}

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
