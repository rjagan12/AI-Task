import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive unit tests for StudentGradeProcessor class.
 * Tests cover all functionality including edge cases and error conditions.
 */
class StudentGradeProcessorTest {
    
    private List<Student> validStudents;
    private List<Student> emptyStudents;
    private List<Student> studentsWithInvalidData;
    
    @BeforeEach
    void setUp() {
        // Valid test data
        validStudents = Arrays.asList(
            new Student("Alice Johnson", Arrays.asList(85.0, 92.0, 78.0, 88.0)),
            new Student("Bob Smith", Arrays.asList(65.0, 72.0, 68.0, 71.0)),
            new Student("Carol Davis", Arrays.asList(95.0, 98.0, 92.0, 96.0)),
            new Student("David Wilson", Arrays.asList(45.0, 52.0, 48.0, 55.0)),
            new Student("Eve Brown", Arrays.asList(75.0, 82.0, 79.0, 85.0))
        );
        
        // Empty list
        emptyStudents = new ArrayList<>();
        
        // Students with invalid data
        studentsWithInvalidData = Arrays.asList(
            new Student("Valid Student", Arrays.asList(80.0, 85.0)),
            new Student(null, Arrays.asList(70.0, 75.0)), // null name
            new Student("No Grades", new ArrayList<>()), // empty grades
            new Student("Invalid Grades", Arrays.asList(110.0, -5.0, 75.0)), // out of range
            new Student("Mixed Grades", Arrays.asList(85.0, null, 90.0)) // null grade
        );
    }
    
    @Nested
    @DisplayName("Valid Input Tests")
    class ValidInputTests {
        
        @Test
        @DisplayName("Should process valid student data correctly")
        void testValidStudentData() {
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(validStudents, 70, 3);
            
            assertEquals(5, results.getTotalStudents());
            assertEquals(3, results.getPassingStudents());
            assertEquals(2, results.getFailingStudents());
            assertEquals(76.05, results.getAverageGrade(), 0.01);
        }
        
        @Test
        @DisplayName("Should calculate correct grade distribution")
        void testGradeDistribution() {
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(validStudents, 70, 3);
            Map<String, Integer> distribution = results.getGradeDistribution();
            
            assertEquals(1, distribution.get("50.0-59.0")); // David Wilson (50.0)
            assertEquals(1, distribution.get("60.0-69.0")); // Bob Smith (69.0)
            assertEquals(2, distribution.get("80.0-89.0")); // Alice Johnson (85.75) and Eve Brown (80.25)
            assertEquals(1, distribution.get("90.0-99.0")); // Carol Davis (95.25)
        }
        
        @Test
        @DisplayName("Should generate appropriate recommendations")
        void testRecommendations() {
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(validStudents, 70, 3);
            List<String> recommendations = results.getRecommendations();
            
            // Should have recommendations about multiple attempts
            assertTrue(recommendations.stream()
                .anyMatch(rec -> rec.contains("Bob Smith") && rec.contains("4 attempts")));
            assertTrue(recommendations.stream()
                .anyMatch(rec -> rec.contains("David Wilson") && rec.contains("4 attempts")));
        }
        
        @Test
        @DisplayName("Should handle different grade thresholds")
        void testDifferentGradeThresholds() {
            // With threshold 80
            ProcessingResults results80 = StudentGradeProcessor.processStudentGrades(validStudents, 80, 3);
            assertEquals(3, results80.getPassingStudents());
            assertEquals(2, results80.getFailingStudents());
            
            // With threshold 60
            ProcessingResults results60 = StudentGradeProcessor.processStudentGrades(validStudents, 60, 3);
            assertEquals(4, results60.getPassingStudents());
            assertEquals(1, results60.getFailingStudents());
        }
        
        @Test
        @DisplayName("Should handle students with multiple attempts")
        void testMultipleAttempts() {
            List<Student> studentsWithManyAttempts = Arrays.asList(
                new Student("Test Student", Arrays.asList(60.0, 65.0, 70.0, 75.0, 80.0)) // 5 attempts
            );
            
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(studentsWithManyAttempts, 70, 3);
            
            assertTrue(results.getRecommendations().stream()
                .anyMatch(rec -> rec.contains("5 attempts") && rec.contains("academic intervention")));
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle single student")
        void testSingleStudent() {
            List<Student> singleStudent = Arrays.asList(
                new Student("Single Student", Arrays.asList(85.0, 90.0))
            );
            
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(singleStudent, 70, 3);
            
            assertEquals(1, results.getTotalStudents());
            assertEquals(1, results.getPassingStudents());
            assertEquals(0, results.getFailingStudents());
            assertEquals(87.5, results.getAverageGrade(), 0.01);
        }
        
        @Test
        @DisplayName("Should handle students with exactly threshold grade")
        void testThresholdGrade() {
            List<Student> thresholdStudent = Arrays.asList(
                new Student("Threshold Student", Arrays.asList(70.0, 70.0))
            );
            
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(thresholdStudent, 70, 3);
            
            assertEquals(1, results.getPassingStudents());
            assertEquals(0, results.getFailingStudents());
        }
        
        @Test
        @DisplayName("Should handle students with exactly max attempts")
        void testMaxAttempts() {
            List<Student> maxAttemptsStudent = Arrays.asList(
                new Student("Max Attempts", Arrays.asList(60.0, 65.0, 70.0)) // exactly 3 attempts
            );
            
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(maxAttemptsStudent, 70, 3);
            
            // Should not generate recommendation since it's exactly at max attempts
            assertTrue(results.getRecommendations().stream()
                .noneMatch(rec -> rec.contains("academic intervention")));
        }
        
        @Test
        @DisplayName("Should handle students with all failing grades")
        void testAllFailingGrades() {
            List<Student> failingStudents = Arrays.asList(
                new Student("Failing Student 1", Arrays.asList(45.0, 50.0)),
                new Student("Failing Student 2", Arrays.asList(55.0, 60.0))
            );
            
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(failingStudents, 70, 3);
            
            assertEquals(0, results.getPassingStudents());
            assertEquals(2, results.getFailingStudents());
            
            // Python version doesn't generate this recommendation, so we don't test for it
        }
    }
    
    @Nested
    @DisplayName("Invalid Input Tests")
    class InvalidInputTests {
        
        @Test
        @DisplayName("Should throw exception for null student data")
        void testNullStudentData() {
            assertThrows(EmptyStudentDataException.class, () -> {
                StudentGradeProcessor.processStudentGrades(null, 70, 3);
            });
        }
        
        @Test
        @DisplayName("Should throw exception for empty student data")
        void testEmptyStudentData() {
            assertThrows(EmptyStudentDataException.class, () -> {
                StudentGradeProcessor.processStudentGrades(emptyStudents, 70, 3);
            });
        }
        
        @Test
        @DisplayName("Should throw exception for invalid grade threshold")
        void testInvalidGradeThreshold() {
            assertThrows(InvalidGradeThresholdException.class, () -> {
                StudentGradeProcessor.processStudentGrades(validStudents, -10, 3);
            });
            
            assertThrows(InvalidGradeThresholdException.class, () -> {
                StudentGradeProcessor.processStudentGrades(validStudents, 150, 3);
            });
        }
        
        @Test
        @DisplayName("Should handle students with invalid data gracefully")
        void testInvalidStudentData() {
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(studentsWithInvalidData, 70, 3);
            
            // Should only process valid students
            assertEquals(3, results.getTotalStudents()); // "Valid Student", "Invalid Grades" (has 1 valid grade), "Mixed Grades" (has 2 valid grades)
            assertEquals(3, results.getPassingStudents());
            assertEquals(0, results.getFailingStudents());
            
            // Should have problem students
            assertEquals(1, results.getProblemStudents().size());
            
            // Check specific problem students
            assertTrue(results.getProblemStudents().stream()
                .anyMatch(ps -> ps.getName().equals("No Grades") && ps.getIssue().equals("No valid grades found")));
        }
        
        @Test
        @DisplayName("Should handle null grades list")
        void testNullGrades() {
            List<Student> studentsWithNullGrades = Arrays.asList(
                new Student("Null Grades", null)
            );
            
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(studentsWithNullGrades, 70, 3);
            
            assertEquals(0, results.getTotalStudents());
            assertEquals(1, results.getProblemStudents().size());
        }
    }
    
    @Nested
    @DisplayName("Overloaded Method Tests")
    class OverloadedMethodTests {
        
        @Test
        @DisplayName("Should work with default parameters (no arguments)")
        void testDefaultParameters() {
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(validStudents);
            
            // Should use default values: gradeThreshold=70, maxAttempts=3
            assertEquals(5, results.getTotalStudents());
            assertEquals(3, results.getPassingStudents());
            assertEquals(2, results.getFailingStudents());
        }
        
        @Test
        @DisplayName("Should work with grade threshold only")
        void testGradeThresholdOnly() {
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(validStudents, 80);
            
            // Should use gradeThreshold=80, maxAttempts=3 (default)
            assertEquals(5, results.getTotalStudents());
            assertEquals(3, results.getPassingStudents());
            assertEquals(2, results.getFailingStudents());
        }
    }
    
    @Nested
    @DisplayName("Performance and Memory Tests")
    class PerformanceTests {
        
        @Test
        @DisplayName("Should handle large number of students efficiently")
        void testLargeDataSet() {
            List<Student> largeDataSet = new ArrayList<>();
            Random random = new Random(42); // Fixed seed for reproducible tests
            
            for (int i = 0; i < 1000; i++) {
                List<Double> grades = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    grades.add(random.nextDouble() * 100);
                }
                largeDataSet.add(new Student("Student " + i, grades));
            }
            
            long startTime = System.currentTimeMillis();
            ProcessingResults results = StudentGradeProcessor.processStudentGrades(largeDataSet, 70, 3);
            long endTime = System.currentTimeMillis();
            
            // Should complete within reasonable time (less than 1 second)
            assertTrue(endTime - startTime < 1000);
            assertEquals(1000, results.getTotalStudents());
        }
        
        @Test
        @DisplayName("Should not modify input data")
        void testInputImmutability() {
            List<Student> originalStudents = new ArrayList<>(validStudents);
            List<Student> copyBeforeProcessing = new ArrayList<>();
            
            // Create deep copy
            for (Student student : originalStudents) {
                copyBeforeProcessing.add(new Student(student.getName(), new ArrayList<>(student.getGrades())));
            }
            
            StudentGradeProcessor.processStudentGrades(originalStudents, 70, 3);
            
            // Original data should remain unchanged
            assertEquals(copyBeforeProcessing.size(), originalStudents.size());
            for (int i = 0; i < originalStudents.size(); i++) {
                assertEquals(copyBeforeProcessing.get(i).getName(), originalStudents.get(i).getName());
                assertEquals(copyBeforeProcessing.get(i).getGrades(), originalStudents.get(i).getGrades());
            }
        }
    }
} 