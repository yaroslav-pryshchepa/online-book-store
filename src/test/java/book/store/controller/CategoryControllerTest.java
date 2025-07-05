package book.store.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryControllerTest {

    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    void beforeEach(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/insert-books-and-categories.sql")
            );
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-books-and-categories-from-db.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category")
    void createCategory_validRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Adventures")
                .setDescription("Books about adventures");
        CategoryDto expected = new CategoryDto()
                .setName("Adventures")
                .setDescription("Books about adventures");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        System.out.println(expected);
        System.out.println(actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all categories")
    void getAllCategories_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("Fiction"));
        assertTrue(content.contains("Science"));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get category by id")
    void getCategoryById_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertNotNull(actual);
        assertEquals("Fiction", actual.getName());
        assertEquals("Fiction books", actual.getDescription());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category")
    void updateCategory_Success() throws Exception {
        CategoryDto expected = new CategoryDto()
                .setName("Updated Fiction")
                .setDescription("Updated fiction description");

        String jsonRequest = objectMapper.writeValueAsString(expected);

        MvcResult result = mockMvc.perform(put("/categories/{id}", 1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete category")
    void deleteCategory_Success() throws Exception {
        mockMvc.perform(delete("/categories/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get books by category id")
    void getBooksByCategoryId_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/{id}/books", 1)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("content"));
        assertTrue(content.contains("totalElements"));
        assertTrue(content.contains("totalPages"));
    }
}
