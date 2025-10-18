#!/bin/bash

# Настройки
IMAGE_NAME="nbank-tests"
TEST_PROFILE=${1:-api-tests}
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR="./test-output/$TIMESTAMP"

# Очистка старых результатов (старше 3 дней)
echo "🧹 Очистка старых результатов тестов (старше 3 дней)..."
find ./test-output -type d -mtime +3 -exec rm -rf {} + 2>/dev/null || true

# Проверка Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker не установлен или не запущен"
    exit 1
fi

# Сборка Docker образа
echo "🚀 Сборка Docker образа..."
docker build -t $IMAGE_NAME . || {
    echo "❌ Ошибка сборки Docker образа"
    exit 1
}

# Создаем директории для результатов
mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

echo "📁 Результаты будут сохранены в: $TEST_OUTPUT_DIR"
echo "   - logs/ (логи выполнения)"
echo "   - results/ (отчеты surefire)"
echo "   - report/ (HTML отчеты)"

# Запуск Docker контейнера
echo "🧪 Запуск тестов с профилем: $TEST_PROFILE"
echo "🔗 API: http://192.168.1.11:4111"
echo "🔗 UI: http://192.168.1.11:3000"

docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL="http://192.168.1.11:4111" \
  -e UIBASEURL="http://192.168.1.11:3000" \
  $IMAGE_NAME

EXIT_CODE=$?

echo " "
echo "📊 Выполнение тестов завершено"

# Проверка создания результатов
echo " "
echo "📋 Созданные файлы:"

if [ -f "$TEST_OUTPUT_DIR/logs/run.log" ]; then
    echo "✅ Логи: $TEST_OUTPUT_DIR/logs/run.log"
    # Покажем последние 5 строк лога для быстрой диагностики
    echo "   Последние строки лога:"
    tail -5 "$TEST_OUTPUT_DIR/logs/run.log" | sed 's/^/   > /'
else
    echo "❌ Логи не созданы"
fi

if [ -d "$TEST_OUTPUT_DIR/results" ] && [ "$(ls -A "$TEST_OUTPUT_DIR/results")" ]; then
    echo "✅ Результаты: $TEST_OUTPUT_DIR/results"
    # Покажем количество тестовых файлов
    COUNT=$(find "$TEST_OUTPUT_DIR/results" -name "*.xml" -o -name "*.txt" | wc -l)
    echo "   Найдено файлов результатов: $COUNT"
else
    echo "❌ Результаты тестов не созданы"
fi

if [ -d "$TEST_OUTPUT_DIR/report" ] && [ "$(ls -A "$TEST_OUTPUT_DIR/report")" ]; then
    echo "✅ Отчет: $TEST_OUTPUT_DIR/report"
    if [ -f "$TEST_OUTPUT_DIR/report/surefire-report.html" ]; then
        echo "   HTML отчет: $TEST_OUTPUT_DIR/report/surefire-report.html"
    fi
else
    echo "❌ HTML отчет не создан"
fi

# Краткая статистика если есть результаты
if [ -d "$TEST_OUTPUT_DIR/results" ]; then
    echo " "
    echo "📈 Краткая статистика:"
    find "$TEST_OUTPUT_DIR/results" -name "*.txt" -exec grep -l "Tests run:" {} \; | head -3 | while read file; do
        echo "   $(basename "$file"): $(grep "Tests run:" "$file")"
    done
fi

echo " "
if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ Тесты завершены успешно"
else
    echo "❌ Тесты завершены с ошибками (код: $EXIT_CODE)"
fi

exit $EXIT_CODE