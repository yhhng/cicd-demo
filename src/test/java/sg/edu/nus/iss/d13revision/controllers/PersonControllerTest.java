package sg.edu.nus.iss.d13revision.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import sg.edu.nus.iss.d13revision.models.Person;
import sg.edu.nus.iss.d13revision.services.PersonService;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    private List<Person> personList;

    @BeforeEach
    void setUp() {
        personList = new ArrayList<>();
        personList.add(new Person("John", "Doe"));
        personList.add(new Person("Jane", "Smith"));
    }

    @Test
    public void index_shouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/person/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"));
    }

    // @Test
    // public void getAllPersons_shouldReturnPersonListJson() throws Exception {
    //     when(personService.getPersons()).thenReturn(personList);

    //     mockMvc.perform(get("/person/testRetrieve"))
    //             .andExpect(status().isOk())
    //             .andExpect(view().name("personList"))
    //             .andExpect(model().attribute("persons", personList));
    // }

    @Test
    public void personList_shouldReturnPersonListView() throws Exception {
        when(personService.getPersons()).thenReturn(personList);

        mockMvc.perform(get("/person/personList"))
                .andExpect(status().isOk())
                .andExpect(view().name("personList"))
                .andExpect(model().attribute("persons", personList));
    }

    @Test
    public void showAddPersonPage_shouldReturnAddPersonView() throws Exception {
        mockMvc.perform(get("/person/addPerson"))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("personForm"));
    }

    @Test
    public void savePerson_shouldRedirect_whenValid() throws Exception {
        mockMvc.perform(post("/person/addPerson")
                .param("firstName", "Test")
                .param("lastName", "User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).addPerson(any(Person.class));
    }

    @Test
    public void savePerson_shouldReturnAddPersonView_whenInvalid() throws Exception {
        mockMvc.perform(post("/person/addPerson")
                .param("firstName", "")
                .param("lastName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(personService, times(0)).addPerson(any(Person.class));
    }

    @Test
    public void personToEdit_shouldReturnEditPersonView() throws Exception {
        Person p = new Person("Edit", "Me");
        mockMvc.perform(post("/person/personToEdit").flashAttr("per", p))
                .andExpect(status().isOk())
                .andExpect(view().name("editPerson"))
                .andExpect(model().attribute("per", p));
    }

    @Test
    public void personEdit_shouldRedirect() throws Exception {
        Person p = new Person("Edited", "User");
        mockMvc.perform(post("/person/personEdit").flashAttr("per", p))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).updatePerson(any(Person.class));
    }

    @Test
    public void personDelete_shouldRedirect() throws Exception {
        Person p = new Person("Delete", "Me");
        mockMvc.perform(post("/person/personDelete").flashAttr("per", p))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).removePerson(any(Person.class));
    }
}
