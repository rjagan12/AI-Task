import java.util.*;
import java.util.stream.Collectors;

/**
 * Data class representing a student with their grades
 */
class Student {
    private String name;
    private List<Double> grades;
    
    public Student(String name, List<Double> grades) {
        this.name = name;
        this.grades = grades != null ? new ArrayList<>(grades) : new ArrayList<>();
    }
    
    public String getName() { return name; }
    public List<Double> getGrades() { return new ArrayList<>(grades); }
    
    @Override
    public String toString() {
        return "Student{name='" + name + "', grades=" + grades + "}";
    }
}

/**
 * Data class representing a problem student with their issue
 */
class ProblemStudent {
    private String name;
    private String issue;
    
    public ProblemStudent(String name, String issue) {
        this.name = name;
        this.issue = issue;
    }
    
    public String getName() { return name; }
    public String getIssue() { return issue; }
    
    @Override
    public String toString() {
        return "ProblemStudent{name='" + name + "', issue='" + issue + "'}";
    }
}

/**
 * Data class containing the processing results
 */
class ProcessingResults {
    private int totalStudents;
    private int passingStudents;
    private int failingStudents;
    private double averageGrade;
    private Map<String, Integer> gradeDistribution;
    private List<String> recommendations;
    private List<ProblemStudent> problemStudents;
    
    public ProcessingResults() {
        this.totalStudents = 0;
        this.passingStudents = 0;
        this.failingStudents = 0;
        this.averageGrade = 0.0;
        this.gradeDistribution = new HashMap<>();
        this.recommendations = new ArrayList<>();
        this.problemStudents = new ArrayList<>();
    }
    
    // Getters
    public int getTotalStudents() { return totalStudents; }
    public int getPassingStudents() { return passingStudents; }
    public int getFailingStudents() { return failingStudents; }
    public double getAverageGrade() { return averageGrade; }
    public Map<String, Integer> getGradeDistribution() { return new HashMap<>(gradeDistribution); }
    public List<String> getRecommendations() { return new ArrayList<>(recommendations); }
    public List<ProblemStudent> getProblemStudents() { return new ArrayList<>(problemStudents); }
    
    // Setters
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    public void setPassingStudents(int passingStudents) { this.passingStudents = passingStudents; }
    public void setFailingStudents(int failingStudents) { this.failingStudents = failingStudents; }
    public void setAverageGrade(double averageGrade) { this.averageGrade = averageGrade; }
    
    // Helper methods
    public void addRecommendation(String recommendation) {
        this.recommendations.add(recommendation);
    }
    
    public void addProblemStudent(ProblemStudent problemStudent) {
        this.problemStudents.add(problemStudent);
    }
    
    public void incrementGradeDistribution(String gradeRange) {
        this.gradeDistribution.merge(gradeRange, 1, Integer::sum);
    }
    
    @Override
    public String toString() {
        return "ProcessingResults{" +
                "totalStudents=" + totalStudents +
                ", passingStudents=" + passingStudents +
                ", failingStudents=" + failingStudents +
                ", averageGrade=" + averageGrade +
                ", gradeDistribution=" + gradeDistribution +
                ", recommendations=" + recommendations +
                ", problemStudents=" + problemStudents +
                '}';
    }
}

/**
 * Custom exception for invalid grade threshold
 */
class InvalidGradeThresholdException extends IllegalArgumentException {
    public InvalidGradeThresholdException(String message) {
        super(message);
    }
}

/**
 * Custom exception for empty student data
 */
class EmptyStudentDataException extends IllegalArgumentException {
    public EmptyStudentDataException(String message) {
        super(message);
    }
}

/**
 * Main class for processing student grade data and generating performance analytics.
 * This is the Java equivalent of the Python process_student_grades function.
 */
public class StudentGradeProcessor {
    
    /**
     * Process student grade data and generate performance analytics.
     * 
     * @param studentData List of Student objects containing student information
     * @param gradeThreshold Minimum passing grade (default: 70)
     * @param maxAttempts Maximum number of attempts allowed (default: 3)
     * @return ProcessingResults object with statistics and recommendations
     * @throws EmptyStudentDataException if student data is null or empty
     * @throws InvalidGradeThresholdException if grade threshold is invalid
     * @throws RuntimeException if any other error occurs during processing
     */
    public static ProcessingResults processStudentGrades(
            List<Student> studentData, 
            int gradeThreshold, 
            int maxAttempts) {
        
        // Input validation
        if (studentData == null || studentData.isEmpty()) {
            throw new EmptyStudentDataException("Student data cannot be empty");
        }
        
        if (gradeThreshold < 0 || gradeThreshold > 100) {
            throw new InvalidGradeThresholdException("Grade threshold must be between 0 and 100");
        }
        
        ProcessingResults results = new ProcessingResults();
        double totalGradeSum = 0.0;
        int validStudents = 0;
        
        try {
            for (Student student : studentData) {
                // Validate student data structure
                if (student == null || student.getName() == null || student.getGrades() == null) {
                    continue;
                }
                
                String studentName = student.getName();
                List<Double> grades = student.getGrades();
                
                // Validate grades data
                if (grades.isEmpty()) {
                    results.addProblemStudent(new ProblemStudent(studentName, "No valid grades found"));
                    continue;
                }
                
                // Calculate student statistics
                List<Double> validGrades = grades.stream()
                    .filter(grade -> grade != null && grade >= 0.0 && grade <= 100.0)
                    .collect(Collectors.toList());
                
                if (validGrades.isEmpty()) {
                    results.addProblemStudent(new ProblemStudent(studentName, "No valid grades found"));
                    continue;
                }
                
                // Calculate average grade for this student
                double studentAverage = validGrades.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
                
                totalGradeSum += studentAverage;
                validStudents++;
                
                // Update grade distribution - match Python format exactly
                int gradeRangeStart = (int) (studentAverage / 10) * 10;
                String gradeRange = String.format("%.1f-%.1f", (double)gradeRangeStart, (double)(gradeRangeStart + 9));
                results.incrementGradeDistribution(gradeRange);
                
                // Determine pass/fail status
                if (studentAverage >= gradeThreshold) {
                    results.setPassingStudents(results.getPassingStudents() + 1);
                } else {
                    results.setFailingStudents(results.getFailingStudents() + 1);
                }
                
                // Check for multiple attempts (regardless of pass/fail status)
                if (validGrades.size() > maxAttempts) {
                    results.addRecommendation(
                        String.format("Student %s has %d attempts. Consider academic intervention.", 
                            studentName, validGrades.size())
                    );
                }
            }
            
            // Calculate overall statistics
            results.setTotalStudents(validStudents);
            
            if (validStudents > 0) {
                double averageGrade = totalGradeSum / validStudents;
                // Round to 2 decimal places (equivalent to Python's round())
                results.setAverageGrade(Math.round(averageGrade * 100.0) / 100.0);
            } else {
                results.setAverageGrade(0.0);
            }
            
            return results;
            
        } catch (Exception e) {
            throw new RuntimeException("Error processing student data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Overloaded method with default parameters (Java doesn't support default parameters like Python)
     */
    public static ProcessingResults processStudentGrades(List<Student> studentData) {
        return processStudentGrades(studentData, 70, 3);
    }
    
    /**
     * Overloaded method with grade threshold only
     */
    public static ProcessingResults processStudentGrades(List<Student> studentData, int gradeThreshold) {
        return processStudentGrades(studentData, gradeThreshold, 3);
    }
    
    /**
     * Main method for testing and demonstration
     */
    public static void main(String[] args) {
        // Test data
        List<Student> testStudents = Arrays.asList(
            new Student("Alice Johnson", Arrays.asList(85.0, 92.0, 78.0, 88.0)),
            new Student("Bob Smith", Arrays.asList(65.0, 72.0, 68.0, 71.0)),
            new Student("Carol Davis", Arrays.asList(95.0, 98.0, 92.0, 96.0)),
            new Student("David Wilson", Arrays.asList(45.0, 52.0, 48.0, 55.0)),
            new Student("Eve Brown", Arrays.asList(75.0, 82.0, 79.0, 85.0))
        );
        
        try {
            ProcessingResults results = processStudentGrades(testStudents, 70, 3);
            System.out.println("Processing Results:");
            System.out.println("Total Students: " + results.getTotalStudents());
            System.out.println("Passing Students: " + results.getPassingStudents());
            System.out.println("Failing Students: " + results.getFailingStudents());
            System.out.println("Average Grade: " + results.getAverageGrade());
            System.out.println("Grade Distribution: " + results.getGradeDistribution());
            System.out.println("Recommendations: " + results.getRecommendations());
            System.out.println("Problem Students: " + results.getProblemStudents());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 