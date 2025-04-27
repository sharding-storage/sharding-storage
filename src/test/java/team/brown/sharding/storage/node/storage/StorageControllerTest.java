package team.brown.sharding.storage.node.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import team.brown.sharding.storage.node.storage.model.KeyValueRequest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для StorageController.
 */
@WebMvcTest(StorageController.class)
public class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;
    @MockBean
    private MigrationService migrationService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Тестирует сохранение значения по ключу.
     */
    @Test
    public void testSetValue() throws Exception {
        String key = "testKey";
        String value = "testValue";
        KeyValueRequest request = new KeyValueRequest(value);

        mockMvc.perform(put("/storage/{key}", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(storageService, times(1)).put(key, value);
    }

    /**
     * Тестирует получение значения по ключу.
     */
    @Test
    public void testGetValue() throws Exception {
        String key = "testKey";
        String value = "testValue";
        when(storageService.get(key)).thenReturn(value);

        mockMvc.perform(get("/storage/{key}", key))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(value));
    }
}