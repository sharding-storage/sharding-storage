package team.brown.sharding.storage.node.storage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.brown.sharding.storage.node.storage.model.KeyValueRequest;
import team.brown.sharding.storage.node.storage.model.ValueResponse;

/**
 * Контроллер для операций получения и установки значений.
 */
@RestController
@RequestMapping("/storage")
@Tag(name = "storage", description = "Доступ к хранилищу ключ-значение")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Возвращает значение по заданному ключу.
     *
     * @param key ключ
     * @return объект ValueReply со значением
     */
    @Operation(summary = "Возвращает значение по ключу", description = "Возвращает значение, связанное с данным ключом")
    @GetMapping("/{key}")
    public ValueResponse getValue(@PathVariable("key") String key) {
        String value = storageService.get(key);
        return new ValueResponse(value);
    }

    /**
     * Сохраняет значение для заданного ключа.
     *
     * @param key     ключ
     * @param request запрос с новым значением
     */
    @Operation(summary = "Устанавливает значение для ключа", description = "Сохраняет значение для данного ключа")
    @PutMapping("/{key}")
    public void setValue(@PathVariable("key") String key, @RequestBody KeyValueRequest request) {
        storageService.put(key, request.getValue());
    }
}
