#!/bin/bash

echo "🚀 Подготовка и запуск тестовой среды..."

# Проверка установки Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker не установлен или не запущен"
    exit 1
fi

# Проверка установки Docker Compose
if ! command -v docker-compose &> /dev/null && ! command -v docker > /dev/null 2>&1 && ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose не установлен"
    exit 1
fi

echo "🛑 Останавливаем текущие контейнеры..."
if command -v docker-compose &> /dev/null; then
    docker-compose down
else
    docker compose down
fi

echo "📥 Загружаем образы браузеров..."

# Путь до файла
json_file="./config/browsers.json"

# Проверяем существование файла
if [ ! -f "$json_file" ]; then
    echo "❌ Файл конфигурации не найден: $json_file"
    exit 1
fi

# Проверяем, что jq установлен
if ! command -v jq &> /dev/null; then
    echo "❌ jq не установлен. Установите jq и попробуйте снова."
    exit 1
fi

# Извлекаем все образы
images=$(jq -r '.. | objects | select(.image) | .image' "$json_file")

if [ -z "$images" ]; then
    echo "❌ Не удалось извлечь образы из конфигурации"
    exit 1
fi

echo "📋 Загружаем следующие образы:"
for image in $images; do
    echo "   - $image"
done

echo ""
echo "⬇️  Начинаем загрузку..."

# Загружаем каждый образ
for image in $images; do
    echo "   Загружаем: $image"
    if docker pull "$image" > /dev/null 2>&1; then
        echo "   ✅ Успешно"
    else
        echo "   ❌ Ошибка загрузки $image"
    fi
done

echo ""
echo "✅ Все образы загружены"
echo "🚀 Запускаем Docker Compose в фоновом режиме..."

# Запускаем compose в фоновом режиме
if command -v docker-compose &> /dev/null; then
    docker-compose up -d
else
    docker compose up -d
fi

# Проверяем успешность запуска
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Docker Compose успешно запущен в фоновом режиме"
    echo ""
    echo "📊 Статус контейнеров:"

    # Показываем статус контейнеров
    if command -v docker-compose &> /dev/null; then
        docker-compose ps
    else
        docker compose ps
    fi

    echo ""
    echo "💡 Для просмотра логов выполните: docker-compose logs -f"
    echo "💡 Для остановки выполните: docker-compose down"
else
    echo "❌ Ошибка при запуске Docker Compose"
    exit 1
fi

# Дополнительная проверка через 3 секунды
echo ""
echo "⏳ Проверяем состояние контейнеров через 3 секунды..."
sleep 3

if command -v docker-compose &> /dev/null; then
    docker-compose ps
else
    docker compose ps
fi