package br.com.tutorialspringboot.javaclient;

import br.com.tutorialspringboot.model.PageableResponse;
import br.com.tutorialspringboot.model.Student;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class JavaSpringClientTest {
    public static void main(String[] args) {

        Student studentPost = new Student();
        studentPost.setName("Fernando Soares");
        studentPost.setEmail("fernando@soares.com");
//        studentPost.setId(35L);

        JavaClientDAO dao = new JavaClientDAO();
//        System.out.println(dao.findById(111));
//        List<Student> students = dao.listAll();
//        System.out.println(students);
//        System.out.println(dao.save(studentPost));
//        dao.update(studentPost);
        dao.delete(35);

    }
}
