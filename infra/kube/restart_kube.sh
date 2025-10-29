#!/bin/bash

echo "=================================================="
echo "🚀 ЗАПУСК ТЕСТОВОГО KUBERNETES ОКРУЖЕНИЯ"
echo "=================================================="

# ШАГ 1: поднятие сервисов приложения
echo ""
echo "🔧 ШАГ 1: ЗАПУСК ПРИЛОЖЕНИЯ"
echo "--------------------------------------------------"

echo "📦 Запускаем Minikube кластер..."
minikube start --driver=docker
echo "✅ Minikube успешно запущен"

echo "📁 Создаем ConfigMap для Selenoid..."
kubectl create configmap selenoid-config --from-file=browsers.json=./nbank-chart/files/browsers.json --dry-run=client -o yaml | kubectl apply -f -
echo "✅ ConfigMap 'selenoid-config' создан"

echo "📦 Устанавливаем Helm чарт nbank..."
helm uninstall nbank 2>/dev/null || true
sleep 5
helm install nbank ./nbank-chart
echo "✅ Helm чарт 'nbank' установлен"

echo "⏳ Ожидаем 30 секунд для запуска подов..."
sleep 30

echo ""
echo "📊 СТАТУС СЕРВИСОВ:"
kubectl get svc

echo ""
echo "🐳 СТАТУС PODS:"
kubectl get pods

# Проверка frontend
echo ""
echo "🔧 ПРОВЕРЯЕМ FRONTEND..."
if kubectl get pods 2>/dev/null | grep -q "CrashLoopBackOff"; then
    echo "🔄 Перезапускаем frontend..."
    kubectl delete pod -l app=frontend
    echo "⏳ Ждем перезапуска..."
    sleep 30
    kubectl get pods -l app=frontend
fi

echo ""
echo "📝 Логи backend (последние строки):"
kubectl logs deployment/backend --tail=10

# Проброс портов приложения
echo ""
echo "🔌 ЗАПУСК ПРОБРОСА ПОРТОВ ПРИЛОЖЕНИЯ"
echo "--------------------------------------------------"

pkill -f "kubectl port-forward" || true
sleep 2

echo "🌐 Frontend:    http://localhost:3000"
kubectl port-forward svc/frontend 3000:80 &
echo "🔧 Backend:     http://localhost:4111"
kubectl port-forward svc/backend 4111:4111 &
echo "🤖 Selenoid:    http://localhost:4444"
kubectl port-forward svc/selenoid 4444:4444 &
echo "📊 Selenoid UI: http://localhost:8080"
kubectl port-forward svc/selenoid-ui 8080:8080 &

sleep 5

# ШАГ 2: мониторинг
echo ""
echo "📊 ШАГ 2: ЗАПУСК МОНИТОРИНГА"
echo "--------------------------------------------------"

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts || true
helm repo add elastic https://helm.elastic.co || true
helm repo update

helm uninstall monitoring -n monitoring 2>/dev/null || true
sleep 5

helm upgrade --install monitoring prometheus-community/kube-prometheus-stack -n monitoring --create-namespace -f monitoring-values.yaml
echo "✅ Мониторинг установлен"

echo "⏳ Ожидаем 60 секунд для запуска..."
sleep 60

echo ""
echo "🔌 ПРОБРОС ПОРТОВ МОНИТОРИНГА"
echo "--------------------------------------------------"
echo "📈 Prometheus:  http://localhost:3001"
kubectl port-forward svc/monitoring-kube-prometheus-prometheus -n monitoring 3001:9090 &
echo "📊 Grafana:     http://localhost:3002"
kubectl port-forward svc/monitoring-grafana -n monitoring 3002:80 &

sleep 10

echo ""
echo "🔐 НАСТРОЙКА БЕЗОПАСНОСТИ"
echo "--------------------------------------------------"
kubectl create secret generic backend-basic-auth --from-literal=username=admin --from-literal=password=admin -n monitoring --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f spring-monitoring.yaml
echo "✅ Spring Monitoring настроен"

# ШАГ 3: логирование
echo ""
echo "💡 ЛОГИРОВАНИЕ (Elasticsearch + Kibana)"
echo "--------------------------------------------------"

kubectl delete namespace logging 2>/dev/null || true
kubectl create namespace logging
sleep 3

helm repo add elastic https://helm.elastic.co || true
helm repo update

echo "📦 Устанавливаем Elasticsearch..."
helm upgrade --install elasticsearch elastic/elasticsearch -n logging \
  --version 7.17.3 \
  --set replicas=1 \
  --set minimumMasterNodes=1 \
  --set persistence.enabled=false \
  --set resources.requests.memory="512Mi" \
  --set resources.limits.memory="1Gi"

# Ждем готовности elasticsearch
echo "⏳ Ждем готовности Elasticsearch..."
kubectl wait --for=condition=ready pod -l app=elasticsearch-master -n logging --timeout=180s

echo "📦 Устанавливаем Kibana..."
helm upgrade --install kibana elastic/kibana -n logging \
  --version 7.17.3 \
  --set image.repository=docker.elastic.co/kibana/kibana \
  --set image.tag=7.17.3 \
  --set resources.requests.memory="256Mi" \
  --set resources.limits.memory="512Mi" \
  --set service.type=ClusterIP \
  --set elasticsearchHosts="http://elasticsearch-master.logging.svc.cluster.local:9200"

# Ждем готовности кибаны
echo "⏳ Ждем готовности Kibana..."
kubectl wait --for=condition=ready pod -l app=kibana -n logging --timeout=180s

# Проброс порта Kibana
echo "📄 Kibana:      http://localhost:5601"
kubectl port-forward svc/kibana-kibana -n logging 5601:5601 &

echo ""
echo "=================================================="
echo "🎯 ВСЕ СЕРВИСЫ ЗАПУЩЕНЫ!"
echo "=================================================="
echo "🌐 ПРИЛОЖЕНИЕ:"
echo "   Frontend:    http://localhost:3000"
echo "   Backend:     http://localhost:4111"
echo "   Selenoid:    http://localhost:4444"
echo "   Selenoid UI: http://localhost:8080"
echo ""
echo "📊 МОНИТОРИНГ:"
echo "   Prometheus:  http://localhost:3001"
echo "   Grafana:     http://localhost:3002"
echo ""
echo "📄 ЛОГИРОВАНИЕ:"
echo "   Kibana:      http://localhost:5601"
echo ""
echo "⚙️  КОМАНДЫ:"
echo "   kubectl get pods -A         # статус подов"
echo "   kubectl get svc -A          # статус сервисов"
echo "   kubectl logs -f <pod> -n ns # логи пода"
echo ""
echo "🛑 ОСТАНОВКА: pkill -f 'kubectl port-forward'"
echo "=================================================="
