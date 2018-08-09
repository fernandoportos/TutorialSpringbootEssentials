package br.com.tutorialspringboot;

import br.com.tutorialspringboot.model.Student;
import br.com.tutorialspringboot.repository.StudentRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpMethod.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEndpointTest {

    @LocalServerPort
    private int port;
    @MockBean
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestRestTemplate restTemplate;

    @TestConfiguration
    static class Config{
        @Bean
        public RestTemplateBuilder restTemplateBuilder(){
            return new RestTemplateBuilder().basicAuthorization("fernando", "porto");
        }
    }

    @Before
    public void setup(){
        Student student = new Student(1L, "Larissa", "larissa@cunha.com.br");
        BDDMockito.when(studentRepository.findOne(student.getId())).thenReturn(student);
    }

    @Test
    public void listStudentsWhenUsernameAndPasswordAreIncorrectShouldReturnStatusCode401(){
        restTemplate = restTemplate.withBasicAuth("1","1");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/", String.class);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(401);
    }

    @Test
    public void getStudentsByIdWhenUsernameAndPasswordAreIncorrectShouldReturnStatusCode401(){
        restTemplate = restTemplate.withBasicAuth("1","1");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/1", String.class);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(401);
    }

    @Test
    public void listStudentsWhenUsernameAndPasswordAreCorrectShouldReturnStatusCode200(){
        List<Student> students = asList(new Student(1L, "Larissa", "larissa@cunha.com.br")
                , new Student(2L, "Azenir", "azenir@porto.com.br"));

        BDDMockito.when(studentRepository.findAll()).thenReturn(students);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/", String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getStudentsByIdWhenUsernameAndPasswordAreCorrectShouldReturnStatusCode200(){
        ResponseEntity<Student> responseEntity = restTemplate.getForEntity("/v1/protected/students/{id}", Student.class, 1L);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getStudentsWhenUsernameAndPasswordAreCorrectAndStudentDoesNotExistShouldReturnStatusCode404(){
        ResponseEntity<Student> responseEntity = restTemplate.getForEntity("/v1/protected/students/{id}", Student.class, -1);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deleteWhenUserHasRoleAdminAndStudentExistsShouldReturnStatusCode200(){
        BDDMockito.doNothing().when(studentRepository).delete(1L);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", DELETE, null, String.class, 1L);

        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @WithMockUser(username = "xx", password = "xx", roles = {"USER", "ADMIN"})
    public void deleteWhenUserHasRoleAdminAndStudentDoesNotExistShouldReturnStatusCode404() throws Exception {
        BDDMockito.doNothing().when(studentRepository).delete(-1L);
        /*ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", DELETE, null, String.class, -1L);

        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);*/

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/admin/students/{id}", -1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "xx", password = "xx")
    public void deleteWhenUserDoesNotHaveRoleAdminShouldReturnStatusCode404() throws Exception {
        BDDMockito.doNothing().when(studentRepository).delete(1L);
        /*ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", DELETE, null, String.class, -1L);

        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);*/

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/admin/students/{id}", -1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void createWhenNameIsNullShouldReturnStatusCode400BadRequest() throws Exception{

        Student student = new Student(3L, null, "azenir@porto.com.br");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/v1/admin/students/", student, String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(400);
        assertThat(responseEntity.getBody()).contains("fieldMessage", "O campo nome do estudante é obrigatório");
    }

    @Test
    public void createShouldPersistDataAndReturnStatusCode201() throws Exception{

        Student student = new Student(3L, "Azenir", "azenir@porto.com.br");
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<Student> responseEntity = restTemplate.postForEntity("/v1/admin/students/", student, Student.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
        assertThat(responseEntity.getBody().getId()).isNotNull();
    }

}
