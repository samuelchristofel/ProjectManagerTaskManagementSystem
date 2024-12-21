import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

class Task {
    String id;
    String name;
    String description;
    int priority;
    String dateAdded;
    Set<Task> dependencies; // Task dependencies
    boolean isCompleted;

    public Task(String id, String name, String description, int priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.dateAdded = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.dependencies = new HashSet<>();
        this.isCompleted = false;
    }

    public void markCompleted() {
        this.isCompleted = true;
    }
    public void addDependency(Task dependentTask) {
        dependencies.add(dependentTask);
    }

    @Override
    public String toString() {
        return "Task ID: " + id + ", Name: " + name + ", Priority: " + priority +
                ", Date Added: " + dateAdded + ", Description: " + description +
                ", Completed: " + isCompleted;
    }
}

class Employee {
    String name;
    List<Task> assignedTasks;

    public Employee(String name) {
        this.name = name;
        this.assignedTasks = new ArrayList<>();
    }

    public void assignTask(Task task) {
        assignedTasks.add(task);
    }
}

public class TaskManager {
    private Map<String, Task> taskMap = new HashMap<>();
    private List<Employee> employees = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    // Task Creation with Dependencies Only
    // Task Creation with Dependencies Only
    public void createTask() {
        System.out.print("Enter Task ID: ");
        String id = scanner.nextLine();

        if (taskMap.containsKey(id)) {
            System.out.println("Task ID already exists.");
            return;
        }

        System.out.print("Enter Task Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Task Description: ");
        String description = scanner.nextLine();

        System.out.print("Enter Task Priority (lower value = higher priority): ");
        int priority = Integer.parseInt(scanner.nextLine());

        Task newTask = new Task(id, name, description, priority);
        taskMap.put(id, newTask);

        // Add Dependencies
        addTaskDependencies(newTask);

        // Display Task Details Immediately
        System.out.println("\n--- Task Created ---");
        System.out.println(newTask);

        // Assign Task to Employee Automatically
        assignTaskToEmployee(newTask);
        System.out.println("Task creation and assignment complete!");

        // Automatically Display Sorted Task List
        displaySortedTasks();
    }
    private void displaySortedTasks() {
        List<Task> sortedTasks = new ArrayList<>(taskMap.values());
        sortedTasks.sort(Comparator.comparingInt(t -> t.priority));

        System.out.println("\n--- Current Task List (Sorted by Priority) ---");
        for (Task task : sortedTasks) {
            System.out.println(task);
        }
    }

    // Add Dependencies
    private void addTaskDependencies(Task task) {
        System.out.print("Do you want to add dependencies? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            while (true) {
                System.out.print("Enter Dependent Task ID (or 'done' to finish): ");
                String dependencyId = scanner.nextLine();

                if (dependencyId.equalsIgnoreCase("done")) break;

                Task dependentTask = taskMap.get(dependencyId);
                if (dependentTask != null) {
                    task.addDependency(dependentTask);
                    System.out.println("Dependency added.");
                } else {
                    System.out.println("Task not found.");
                }
            }
        }
    }


    // Delete Task
    public void deleteTask() {
        System.out.print("Enter Task ID to delete: ");
        String id = scanner.nextLine();

        if (taskMap.remove(id) != null) {
            System.out.println("Task deleted successfully.");
        } else {
            System.out.println("Task not found.");
        }
    }

    // Mark Task as Completed
    public void markTaskCompleted() {
        System.out.print("Enter Task ID to mark as completed: ");
        String id = scanner.nextLine();

        Task task = taskMap.get(id);
        if (task != null) {
            task.markCompleted();
            System.out.println("Task marked as completed.");
        } else {
            System.out.println("Task not found.");
        }
    }

    // View Completed Tasks
    public void viewCompletedTasks() {
        System.out.println("\n--- Completed Tasks ---");
        boolean hasCompletedTasks = false;

        for (Task task : taskMap.values()) {
            if (task.isCompleted) {
                System.out.println(task);
                hasCompletedTasks = true;
            }
        }

        if (!hasCompletedTasks) {
            System.out.println("No completed tasks.");
        }
    }

    // Assign Task to Employee
    private void assignTaskToEmployee(Task task) {
        if (employees.isEmpty()) {
            System.out.println("No employees available for task assignment. Add employees first.");
            return;
        }

        // Select the employee with the least assigned tasks
        Employee selectedEmployee = employees.stream()
                .min(Comparator.comparingInt(e -> e.assignedTasks.size()))
                .orElse(null);

        if (selectedEmployee != null) {
            selectedEmployee.assignTask(task);
            System.out.println("Task '" + task.name + "' assigned to " + selectedEmployee.name);
        }
    }
    private void assignTaskManually() {
        if (employees.isEmpty()) {
            System.out.println("No employees available. Please add employees first.");
            return;
        }

        System.out.print("Enter Task ID to assign: ");
        String taskId = scanner.nextLine();
        Task task = taskMap.get(taskId);

        if (task == null) {
            System.out.println("Task not found.");
            return;
        }

        System.out.println("\nAvailable Employees:");
        for (int i = 0; i < employees.size(); i++) {
            System.out.println((i + 1) + ". " + employees.get(i).name);
        }

        System.out.print("Select Employee by number: ");
        int empIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline

        if (empIndex >= 0 && empIndex < employees.size()) {
            Employee selectedEmployee = employees.get(empIndex);
            selectedEmployee.assignTask(task);
            System.out.println("Task '" + task.name + "' manually assigned to " + selectedEmployee.name);
        } else {
            System.out.println("Invalid employee selection.");
        }
    }

    // Add Employee
    private void addEmployee() {
        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();

        // Check if employee already exists
        boolean exists = employees.stream()
                .anyMatch(e -> e.name.equalsIgnoreCase(name));

        if (exists) {
            System.out.println("Employee already exists.");
            return;
        }

        Employee newEmployee = new Employee(name);
        employees.add(newEmployee);
        System.out.println("Employee " + name + " added successfully.");
    }

    // View Employee Workload
    private void viewEmployeeWorkload() {
        if (employees.isEmpty()) {
            System.out.println("No employees added yet.");
            return;
        }

        System.out.println("\n--- Employee Workload ---");
        for (Employee employee : employees) {
            System.out.println("Employee: " + employee.name);
            if (employee.assignedTasks.isEmpty()) {
                System.out.println("  No tasks assigned");
            } else {
                System.out.println("  Assigned Tasks:");
                for (Task task : employee.assignedTasks) {
                    System.out.println("  - " + task.name + " (Priority: " + task.priority + ")");
                }
            }
            System.out.println("  Total Tasks: " + employee.assignedTasks.size());
        }
    }

    // Main Menu
    public void run() {
        while (true) {
            System.out.println("\n--- Task Management System ---");
            System.out.println("1. Create Task");
            System.out.println("2. Delete Task");
            System.out.println("3. Mark Task as Completed");
            System.out.println("4. View Completed Tasks");
            System.out.println("5. Add Employee");
            System.out.println("6. View Employee Workload");
            System.out.println("7. Assign Task to Employee");
            System.out.println("8. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createTask();
                    break;
                case 2:
                    deleteTask();
                    break;
                case 3:
                    markTaskCompleted();
                    break;
                case 4:
                    viewCompletedTasks();
                    break;
                case 5:
                    addEmployee();
                    break;
                case 6:
                    viewEmployeeWorkload();
                    break;
                case 7:
                    assignTaskManually();
                    break;
                case 8:
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.run();
    }
}
