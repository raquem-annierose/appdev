package src.main.java.com.example.model;

public class Student {

	private String firstName;
	private String lastName;
	private float midtermGrade;
	private float finalGrade;
	
	//constructor
	
	public Student (String firstName, String lastName,
			float midtermGrade,float finalGrade) {
		
		this.firstName = firstName;
		this.lastName =lastName; 
		this.midtermGrade = midtermGrade;
		this.finalGrade = finalGrade;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public float compute () {
		return (midtermGrade + finalGrade) / 2;
	}
	public String evaluate () {
		float average = this.compute();
		
		if (average >= 75 ) {
			return "Pass";
		} else {
			return "Failed";
		}
	}
	
	
}

