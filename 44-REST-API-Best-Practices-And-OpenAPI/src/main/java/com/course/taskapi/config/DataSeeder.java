package com.course.taskapi.config;

import com.course.taskapi.task.Task;
import com.course.taskapi.task.TaskPriority;
import com.course.taskapi.task.TaskRepository;
import com.course.taskapi.task.TaskStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/*
 * Inserts 12 sample tasks at startup so pagination, filters and sorting
 * have something to show (12 tasks / page size 10 = 2 pages).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskRepository repository;

    public DataSeeder(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        List<Task> tasks = List.of(
                task("Prepare sprint demo", TaskPriority.HIGH, today.plusDays(2), TaskStatus.IN_PROGRESS),
                task("Write API documentation", TaskPriority.MEDIUM, today.plusDays(5), TaskStatus.TODO),
                task("Fix login bug", TaskPriority.HIGH, today.plusDays(1), TaskStatus.TODO),
                task("Update dependencies", TaskPriority.LOW, today.plusDays(14), TaskStatus.TODO),
                task("Code review for payments", TaskPriority.MEDIUM, today.plusDays(3), TaskStatus.DONE),
                task("Plan team lunch", TaskPriority.LOW, today.plusDays(7), TaskStatus.DONE),
                task("Design database indexes", TaskPriority.HIGH, today.plusDays(4), TaskStatus.TODO),
                task("Set up CI pipeline", TaskPriority.MEDIUM, today.plusDays(10), TaskStatus.IN_PROGRESS),
                task("Demo for customer", TaskPriority.HIGH, today.plusDays(6), TaskStatus.TODO),
                task("Refactor order service", TaskPriority.MEDIUM, today.plusDays(12), TaskStatus.TODO),
                task("Write unit tests", TaskPriority.MEDIUM, today.plusDays(8), TaskStatus.IN_PROGRESS),
                task("Clean up old branches", TaskPriority.LOW, today.plusDays(20), TaskStatus.DONE));
        repository.saveAll(tasks);
    }

    private static Task task(String title, TaskPriority priority, LocalDate due, TaskStatus status) {
        Task task = new Task(title, null, priority, due);
        task.setStatus(status);
        return task;
    }
}
