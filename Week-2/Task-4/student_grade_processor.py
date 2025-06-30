def process_student_grades(student_data, grade_threshold=70, max_attempts=3):
    """
    Process student grade data and generate performance analytics.
    
    Args:
        student_data (list): List of dictionaries containing student information
        grade_threshold (int): Minimum passing grade (default: 70)
        max_attempts (int): Maximum number of attempts allowed (default: 3)
    
    Returns:
        dict: Processed results with statistics and recommendations
    """
    if not student_data:
        raise ValueError("Student data cannot be empty")
    
    if grade_threshold < 0 or grade_threshold > 100:
        raise ValueError("Grade threshold must be between 0 and 100")
    
    results = {
        'total_students': 0,
        'passing_students': 0,
        'failing_students': 0,
        'average_grade': 0.0,
        'grade_distribution': {},
        'recommendations': [],
        'problem_students': []
    }
    
    total_grade_sum = 0
    valid_students = 0
    
    try:
        for student in student_data:
            # Validate student data structure
            if not isinstance(student, dict):
                continue
                
            if 'name' not in student or 'grades' not in student:
                continue
            
            student_name = student['name']
            grades = student['grades']
            
            # Validate grades data
            if not isinstance(grades, list) or len(grades) == 0:
                results['problem_students'].append({
                    'name': student_name,
                    'issue': 'No valid grades found'
                })
                continue
            
            # Calculate student statistics
            valid_grades = []
            for grade in grades:
                if isinstance(grade, (int, float)) and 0 <= grade <= 100:
                    valid_grades.append(grade)
            
            if len(valid_grades) == 0:
                results['problem_students'].append({
                    'name': student_name,
                    'issue': 'No valid grades found'
                })
                continue
            
            # Calculate average grade for this student
            student_average = sum(valid_grades) / len(valid_grades)
            total_grade_sum += student_average
            valid_students += 1
            
            # Update grade distribution
            grade_range = f"{(student_average // 10) * 10}-{(student_average // 10) * 10 + 9}"
            results['grade_distribution'][grade_range] = results['grade_distribution'].get(grade_range, 0) + 1
            
            # Determine pass/fail status
            if student_average >= grade_threshold:
                results['passing_students'] += 1
            else:
                results['failing_students'] += 1
                
                # Check for multiple attempts
                if len(valid_grades) > max_attempts:
                    results['recommendations'].append(
                        f"Student {student_name} has {len(valid_grades)} attempts. "
                        f"Consider academic intervention."
                    )
        
        # Calculate overall statistics
        results['total_students'] = valid_students
        
        if valid_students > 0:
            results['average_grade'] = round(total_grade_sum / valid_students, 2)
        else:
            results['average_grade'] = 0.0
        
        # Generate additional recommendations
        if results['failing_students'] > results['passing_students']:
            results['recommendations'].append(
                "More than 50% of students are failing. Consider curriculum review."
            )
        
        if results['average_grade'] < grade_threshold:
            results['recommendations'].append(
                f"Class average ({results['average_grade']}) is below threshold ({grade_threshold}). "
                f"Consider additional support."
            )
        
        return results
        
    except Exception as e:
        raise RuntimeError(f"Error processing student data: {str(e)}")


# Example usage and test data
if __name__ == "__main__":
    # Test data
    test_students = [
        {
            'name': 'Alice Johnson',
            'grades': [85, 92, 78, 88]
        },
        {
            'name': 'Bob Smith',
            'grades': [65, 72, 68, 71]
        },
        {
            'name': 'Carol Davis',
            'grades': [95, 98, 92, 96]
        },
        {
            'name': 'David Wilson',
            'grades': [45, 52, 48, 55]
        },
        {
            'name': 'Eve Brown',
            'grades': [75, 82, 79, 85]
        }
    ]
    
    try:
        results = process_student_grades(test_students, grade_threshold=70, max_attempts=3)
        print("Processing Results:")
        print(f"Total Students: {results['total_students']}")
        print(f"Passing Students: {results['passing_students']}")
        print(f"Failing Students: {results['failing_students']}")
        print(f"Average Grade: {results['average_grade']}")
        print(f"Grade Distribution: {results['grade_distribution']}")
        print(f"Recommendations: {results['recommendations']}")
        print(f"Problem Students: {results['problem_students']}")
    except Exception as e:
        print(f"Error: {e}") 