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
        echo "   ❌ Ошибка"
    fi
done

echo ""
echo "✅ Все образы загружены"
echo "🚀 Запускаем Docker Compose..."

# Запускаем compose в зависимости от доступной команды
if command -v docker-compose &> /dev/null; then
    docker-compose up
else
    docker compose up
fi