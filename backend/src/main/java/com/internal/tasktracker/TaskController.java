package com.internal.tasktracker;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<Map<String, Object>> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input
        String query = Objects.requireNonNullElse(q, "").trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // Parse status filter
        String normalizedStatus = null;

        if (status != null && !status.isBlank()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.toUpperCase()).name();
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid status: " + status));
            }
        }

        // Query complexity estimation for logging
        log.info("Task search - q='{}', status={}, page={}, pageSize={}",
                query, normalizedStatus, page, pageSize);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allResults.size());
        List<Task> pageResults = (start < allResults.size())
                ? allResults.subList(start, end)
                : Collections.emptyList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
