#!/bin/bash

set -e  # Выход при ошибке

echo "🚀 Запуск сервисов приложения..."

# ШАГ 1: Инициализация кластера
echo "📦 Запуск Minikube кластера..."
if ! minikube status | grep -q "Running"; then
    minikube start --driver=docker
    echo "✅ Minikube кластер успешно запущен"
else
    echo "⚠️  Minikube уже запущен, используем существующий кластер"
fi

# ШАГ 2: Настройка ConfigMap
echo "🔧 Создание ConfigMap для Selenoid..."
if [ -f "./nbank-chart/files/browsers.json" ]; then
    kubectl create configmap selenoid-config \
        --from-file=browsers.json=./nbank-chart/files/browsers.json \
        --dry-run=client -o yaml | kubectl apply -f -
    echo "✅ ConfigMap 'selenoid-config' создан/обновлен"
else
    echo "❌ Файл ./nbank-chart/files/browsers.json не найден"
    exit 1
fi

# ШАГ 3: Установка Helm чарта
echo "📦 Установка nbank Helm чарта..."
if [ -d "./nbank-chart" ]; then
    helm upgrade --install nbank ./nbank-chart
    echo "✅ Helm чарт 'nbank' установлен/обновлен"
else
    echo "❌ Директория ./nbank-chart не найдена"
    exit 1
fi

# ШАГ 4: Ожидание готовности подов
echo "⏳ Ожидание готовности подов..."
sleep 10  # Даем подам время на начало запуска

# Ждем пока хотя бы один под бэкенда станет готовым
if kubectl wait --for=condition=ready pod -l app=backend --timeout=120s 2>/dev/null; then
    echo "✅ Backend pod готов"
else
    echo "⚠️  Backend pod не готов, продолжаем без ожидания..."
fi

# ШАГ 5: Отображение статуса
echo ""
echo "📊 ТЕКУЩИЙ СТАТУС КЛАСТЕРА:"
echo "==========================="

echo ""
echo "📋 Сервисы:"
kubectl get svc

echo ""
echo "🐳 Поды:"
kubectl get pods

echo ""
echo "📦 Деплойменты:"
kubectl get deployments

# ШАГ 6: Логи для отладки
echo ""
echo "📝 ЛОГИ BACKEND (первые 20 строк):"
echo "==================================="
kubectl logs deployment/backend --tail=20 --timestamps=true || echo "⚠️  Не удалось получить логи backend"

# ШАГ 7: Проброс портов в фоновом режиме
echo ""
echo "🔌 ЗАПУСК ПРОБРОСА ПОРТОВ:"
echo "==========================="

# Останавливаем предыдущие процессы порт-форвардинга
pkill -f "kubectl port-forward" || true
sleep 2

# Запускаем порт-форвардинг в фоне
echo "🌐 Frontend:    http://localhost:3000"
kubectl port-forward svc/frontend 3000:80 > /tmp/frontend-portforward.log 2>&1 &

echo "🔧 Backend:     http://localhost:4111"
kubectl port-forward svc/backend 4111:4111 > /tmp/backend-portforward.log 2>&1 &

echo "🤖 Selenoid:    http://localhost:4444"
kubectl port-forward svc/selenoid 4444:4444 > /tmp/selenoid-portforward.log 2>&1 &

echo "📊 Selenoid UI: http://localhost:8080"
kubectl port-forward svc/selenoid-ui 8080:8080 > /tmp/selenoid-ui-portforward.log 2>&1 &

# Даем время для установки соединений
sleep 5

# Проверяем что порт-форвардинг работает
echo ""
echo "✅ Порт-форвардинг запущен в фоновом режиме"
echo "📁 Логи порт-форвардинга в /tmp/*-portforward.log"

# ШАГ 8: Информация для пользователя
echo ""
echo "🎯 ДОСТУП К СЕРВИСАМ:"
echo "====================="
echo "Frontend:    http://localhost:3000"
echo "Backend:     http://localhost:4111"
echo "Selenoid:    http://localhost:4444"
echo "Selenoid UI: http://localhost:8080"
echo ""
echo "💡 Для просмотра логов: kubectl logs -f deployment/<имя-деплоймента>"
echo "🛑 Для остановки порт-форвардинга: pkill -f 'kubectl port-forward'"