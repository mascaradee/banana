package mascaradee.banana;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {

	private final EmployeeRepository repository;
	private final EmployeeModelAssembler assembler;

	EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}

//	// Aggregate root
//	// tag::get-aggregate-root[]
//	@GetMapping("/employees")
//	List<Employee> all() {
//		return repository.findAll();
//	}
//	// end::get-aggregate-root[]

	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {

		List<EntityModel<Employee>> employees = repository.findAll().stream()
//				.map(employee -> EntityModel.of(employee, linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
//						linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
				.map(assembler::toModel) // 위 코드와 같음. RESTful1- 링크를 붙인다. -> 별도 메서드를 만들어서 호출한다.
				.collect(Collectors.toList());

		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}

//	@PostMapping("/employees")
//	Employee newEmployee(@RequestBody Employee newEmployee) {
//		return repository.save(newEmployee);
//	}

	// RESTful2 - 저장하는 방법은 같지만 결과를 object로 감싸서 리턴한다. ?
	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

		EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

		return ResponseEntity //
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) // HTTP 201 created 상태메시지를 세팅한다.
				.body(entityModel);
	}

	// Single item
//	@GetMapping("/employees/{id}")
//	Employee one(@PathVariable Long id) {
//
//		return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
//	}

	@GetMapping("/employees/{id}")
	EntityModel<Employee> one(@PathVariable Long id) {

		Employee employee = repository.findById(id) //
				.orElseThrow(() -> new EmployeeNotFoundException(id));

//		return EntityModel.of(employee, //
//				linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
//				linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
		return assembler.toModel(employee); // 위와 같은 결과
	}

//	@PutMapping("/employees/{id}")
//	Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
//
//		return repository.findById(id).map(employee -> {
//			employee.setName(newEmployee.getName());
//			employee.setRole(newEmployee.getRole());
//			return repository.save(employee);
//		}).orElseGet(() -> {
//			newEmployee.setId(id);
//			return repository.save(newEmployee);
//		});
//	}

	/*
	 {
	"id": 3,
	"firstName": "Samwise",
	"lastName": "Gamgee",
	"role": "ring bearer",
	"name": "Samwise Gamgee"
	}	 
	 * */

	@PutMapping("/employees/{id}")
	ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

		Employee updatedEmployee = repository.findById(id) //
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				}) //
				.orElseGet(() -> {
					newEmployee.setId(id);
					return repository.save(newEmployee);
				});

		EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

		return ResponseEntity //
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}
	/*
	 {
	    "id": 3,
	    "firstName": "Samwise",
	    "lastName": "Gamgee",
	    "role": "ring bearer",
	    "name": "Samwise Gamgee",
	    "_links": {
	        "self": {
	            "href": "http://localhost:8080/employees/3"
	        },
	        "employees": {
	            "href": "http://localhost:8080/employees"
	        }
	    }
	}
	*/

//	@DeleteMapping("/employees/{id}")
//	void deleteEmployee(@PathVariable Long id) {
//		repository.deleteById(id);
//	}
	// 응답: 200 OK

	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable Long id) {

		repository.deleteById(id);

		return ResponseEntity.noContent().build(); // HTTP 204 No Content 세팅
	}
	// 응답: 204 No Content

}