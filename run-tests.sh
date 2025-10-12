#!/bin/bash

# Настройки
IMAGE_NAME="nbank-tests"
TEST_PROFILE=${1:-api-tests} # аргумент запуска
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
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
mkdir -p "$TEST_OUTPUT_DIR/target"
mkdir -p "$TEST_OUTPUT_DIR/results"

echo "📁 Результаты будут сохранены в: $TEST_OUTPUT_DIR"
echo "   - logs/ (логи выполнения)"
echo "   - target/ (отчеты surefire)"
echo "   - results/ (HTML отчеты)"

# Запуск Docker контейнера
echo "🧪 Запуск тестов с профилем: $TEST_PROFILE"
echo "🔗 API: http://192.168.1.14:4111"
echo "🔗 UI: http://192.168.1.14:3000"

docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/target":/app/target \
  -v "$TEST_OUTPUT_DIR/results":/app/reports \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL="http://192.168.1.14:4111" \
  -e UIBASEURL="http://192.168.1.14:3000" \
  $IMAGE_NAME

EXIT_CODE=$?

echo " "
echo "📊 Выполнение тестов завершено с кодом выхода: $EXIT_CODE"

# Проверка создания результатов
if [ -f "$TEST_OUTPUT_DIR/logs/run.log" ]; then
    echo "✅ Файл логов создан: $TEST_OUTPUT_DIR/logs/run.log"
else
    echo "⚠️  Внимание: Файл логов не создан"
fi

if [ -f "$TEST_OUTPUT_DIR/results/surefire.html" ]; then
    echo "✅ HTML отчет создан: $TEST_OUTPUT_DIR/results/surefire.html"
    echo "   Открыть: open $TEST_OUTPUT_DIR/results/surefire.html"
else
    echo "⚠️  Внимание: HTML отчет не создан"
fi

# Покажем краткую статистику если есть результаты
if [ -d "$TEST_OUTPUT_DIR/target" ]; then
    echo " "
    echo "📈 Статистика тестов:"
    find "$TEST_OUTPUT_DIR/target" -name "*.txt" -exec grep -H "Tests run:" {} \; | head -5
fi

exit $EXIT_CODE