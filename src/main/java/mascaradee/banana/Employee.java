package mascaradee.banana;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
class Employee {

	private @Id @GeneratedValue Long id;
	// private String name; // 클라이언트에 영향 없이 백엔드만 수정하기 위해 -> RESTful 방식 중 하나?
	private String firstName;
	private String lastName;
	private String role;

	Employee() {}

	Employee(/*String name,*/ String firstName, String lastName, String role) {

		// this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public String getName() {
//		return this.name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
	// old 버전도 받아들일 수 있도록 수정
	public String getName() {
		return this.firstName + " " + this.lastName;
	}

	public void setName(String name) {
		String[] parts = name.split(" ");
		this.firstName = parts[0];
		this.lastName = parts[1];
	}

	// 신규 버전도 받아들일 수 있도록 추가
	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (!(o instanceof Employee))
			return false;
		Employee employee = (Employee) o;
		return Objects.equals(this.id, employee.id) && Objects.equals(this.firstName, employee.firstName)
				&& Objects.equals(this.lastName, employee.lastName) && Objects.equals(this.role, employee.role);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.firstName, this.lastName, this.role);
	}

	@Override
	public String toString() {
		return "Employee{" + "id=" + this.id + ", firstName='" + this.firstName + '\'' + ", lastName='" + this.lastName + '\'' + ", role='"
				+ this.role + '\'' + '}';
	}
}