# Базовый докер образ
FROM maven:3.9.9-eclipse-temurin-21

# Дефолтные значения аргументов
ARG TEST_PROFILE=api-tests
ARG APIBASEURL=http://localhost:4111
ARG UIBASEURL=http://localhost:3000

# Переменные окружения для контейнера
ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}

# Работаем из папки /app
WORKDIR /app

# Копируем pom.xml
COPY pom.xml .

# Загружаем зависимости и кешируем
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Теперь внутри есть зависимости, есть исходный код и мы готовы запускать тесты

# Команда для запуска тестов и генерации отчета
CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>> Running tests with profile: ${TEST_PROFILE}' ; \
    echo '>>> API URL: ${APIBASEURL}' ; \
    echo '>>> UI URL: ${UIBASEURL}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    \
    echo '>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
    } 2>&1 | tee /app/logs/run.log"