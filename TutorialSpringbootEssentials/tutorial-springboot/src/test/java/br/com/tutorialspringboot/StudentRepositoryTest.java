package br.com.tutorialspringboot;

import br.com.tutorialspringboot.model.Student;
import br.com.tutorialspringboot.repository.StudentRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createShouldPersistData() {
        Student student = new Student("fernando", "fernando@porto.com.br");
        this.studentRepository.save(student);

        assertThat(student.getId()).isNotNull();
        assertThat(student.getName()).isEqualTo("fernando");
        assertThat(student.getEmail()).isEqualTo("fernando@porto.com.br");
    }

    @Test
    public void deleteShouldRemoveData() {
        Student student = new Student("fernando", "fernando@porto.com.br");
        this.studentRepository.save(student);
        studentRepository.delete(student);

        assertThat(studentRepository.findOne(student.getId())).isNull();
    }

    @Test
    public void updateShouldChangeAndPersistData() {
        Student student = new Student("fernando", "fernando@porto.com.br");
        this.studentRepository.save(student);
        student.setName("fernando22");
        student.setEmail("fernando22@porto.com.br");
        this.studentRepository.save(student);
        student = this.studentRepository.findOne(student.getId());

        assertThat(student.getName()).isEqualTo("fernando22");
        assertThat(student.getEmail()).isEqualTo("fernando22@porto.com.br");
    }

    @Test
    public void findByNameIgnoreCaseContainingShouldIgnoreCase() {
        Student student = new Student("Fernando", "fernando@porto.com.br");
        Student student2 = new Student("fernando", "fernando22@porto.com.br");
        this.studentRepository.save(student);
        this.studentRepository.save(student2);
        List<Student> studentList = studentRepository.findByNameIgnoreCaseContaining("fernando");

        assertThat(studentList.size()).isEqualTo(2);
    }

    @Test
    public void createWhenNameIsNullShouldThrowConstraintViolationException(){
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("O campo nome do estudante é obrigatório");
        this.studentRepository.save(new Student());
    }

    @Test
    public void createWhenEmailIsNullShouldThrowConstraintViolationException(){
        thrown.expect(ConstraintViolationException.class);
        //thrown.expectMessage("O campo email do estudante é obrigatório");
        Student student = new Student();
        student.setName("Fernando");
        this.studentRepository.save(student);
    }

    @Test
    public void createWhenEmailIsNotValidShouldThrowConstraintViolationException(){
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("Digite um email válido");
        Student student = new Student();
        student.setName("Fernando");
        student.setEmail("fernando");
        this.studentRepository.save(student);
    }

}
