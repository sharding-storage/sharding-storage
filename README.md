# sharding-storage

Репозиторий для основного приложения in-memory шардированного хранилища

## 🚀 Быстрый старт

### Установка

```bash
git clone <ваш-репозиторий>
cd sharding-cluster
chmod +x starter-cluster.sh
```

### Основное использование

#### Запуск кластера с N хранилищами (по умолчанию 1)

``` ./starter-cluster.sh [N] ```

#### Пример с 3 узлами:

```./starter-cluster.sh 3```

### 🌐 Веб-интерфейсы

| Сервис          | URL                                         | Описание             |
|:----------------|:--------------------------------------------|:---------------------|
| API мастер-узла | http://localhost:9090/swagger-ui/index.html | Документация Swagger |

### 🛠 Команды управления

#### Работа с контейнерами

- Подключиться к клиентскому контейнеру:
```docker attach sharding-client```

#### Просмотр логов:

```
docker logs -f master-node
docker logs -f sharding-storage-1
```