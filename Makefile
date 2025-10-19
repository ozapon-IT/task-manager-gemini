# ==========================
# 🧑‍💻 開発環境コマンド群
# ==========================

# 開発環境を起動（HMR有効 / Nginxなし）
up-dev:
	docker compose -f docker-compose.dev.yml up -d

# 開発環境を停止
down-dev:
	docker compose -f docker-compose.dev.yml down

# 開発環境のログ監視
logs-dev:
	docker compose -f docker-compose.dev.yml logs -f

# 開発環境のビルド（キャッシュ利用）
build-dev:
	docker compose -f docker-compose.dev.yml build

# 開発環境を完全再構築（DB含む）
rebuild-dev:
	docker compose -f docker-compose.dev.yml down -v
	docker compose -f docker-compose.dev.yml build --no-cache
	docker compose -f docker-compose.dev.yml up -d

# ==========================
# 🚀 本番環境コマンド群
# ==========================

# 本番環境をビルド＆起動（Nginxあり）
up-prod:
	docker compose -f docker-compose.prod.yml up -d --build

# 本番環境を停止
down-prod:
	docker compose -f docker-compose.prod.yml down

# 本番環境のログ監視
logs-prod:
	docker compose -f docker-compose.prod.yml logs -f

# 本番環境を完全再構築（DB含む）
rebuild-prod:
	docker compose -f docker-compose.prod.yml down -v
	docker compose -f docker-compose.prod.yml build --no-cache
	docker compose -f docker-compose.prod.yml up -d

# ==========================
# 🧹 共通ユーティリティ
# ==========================

# 全コンテナ停止・削除
down-all:
	docker compose -f docker-compose.dev.yml down -v || true
	docker compose -f docker-compose.prod.yml down -v || true

# すべてのイメージ・キャッシュを削除（慎重に！）
clean:
	docker system prune -af --volumes

# すべてのログを監視
logs:
	docker compose logs -f

# ==========================
# 🎯 デフォルトターゲット
# ==========================

.PHONY: up-dev down-dev logs-dev build-dev rebuild-dev \
        up-prod down-prod logs-prod rebuild-prod \
        down-all clean logs
