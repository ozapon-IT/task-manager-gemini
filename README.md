# Task Manager Monorepo (Gemini CLI Edition)

Task Manager は **Nuxt 3 + Spring Boot** を統合したフルスタックなタスク管理アプリケーションです。  
バックエンドの Spring Boot REST API とフロントエンドの Nuxt 3 (TypeScript) SPA を Docker Compose で連携させ、  
プロジェクト／タスクの CRUD・進捗管理・ダークモード切替などを一貫した UI 上で実行できます。

---

## 🌐 プロジェクト概要

- **Spring Boot API**
  - `/api/projects` および `/api/projects/{project}/tasks` の REST エンドポイントを提供。
  - プロジェクトに紐づくタスクをネストされたリソースとして管理。
  - JPA / Hibernate による永続化層。
  - 統合 Feature テストを JUnit + MockMvc で実装。
- **Nuxt 3 SPA**
  - ダッシュボード・プロジェクト一覧・詳細ビューを備えたタスク管理 UI。
  - タスクのクイック完了、優先度・ステータス変更、モーダル編集を実装。
  - ダークモード切替（Tailwind CSS + prefers-color-scheme）。
- **Docker Compose**
  - Spring Boot + Nuxt + PostgreSQL + Nginx のマルチコンテナ構成。
  - `http://localhost:8080` → API  
    `http://localhost:3000` → フロント開発サーバー（Vite ホットリロード対応）。

---

## ⚙️ 技術スタック

| コンポーネント | バージョン / 備考 |
|----------------|------------------|
| **Spring Boot** | 3.3.x（安定版） |
| **Java** | 17（LTS） |
| **Gradle** | 8.7 |
| **Nuxt** | 3.11.x（安定版、strict TypeScript モード有効） |
| **Node.js** | 20.11.x（node:20-alpine ベース） |
| **TypeScript** | 5.4.x（`strict: true` 有効） |
| **Tailwind CSS** | 3.4.x |
| **Pinia** | 2.1.x |
| **Vee Validate** | 4.12.x |
| **Axios** | 1.6.x |
| **PostgreSQL** | 15 |
| **Docker Compose** | v2 |
| **テスト** | JUnit 5 / Mockito / Vitest 3 / Cypress 13 |

---

## 📂 主要ディレクトリ構成

```text
.
├── backend/   # Spring Boot API
│   ├── src/
│   │   ├── main/java/com/example/taskmanager/   # アプリケーション本体
│   │   └── test/java/com/example/taskmanager/   # Featureテスト
│   └── build.gradle                             # Gradle設定ファイル
│
├── frontend/                       # Nuxt 3 + TypeScript SPA
│   ├── src/                        # ページ・コンポーネント・ストア等
│   ├── types/                      # 共通型定義
│   └── nuxt.config.ts              # Nuxt設定ファイル
│
├── docker/                         # 各コンテナの設定
│
├── Makefile                        # Docker 操作用ショートカットコマンド
│
└── docker-compose.yml              # 開発環境構築用 Compose ファイル
```

---

## 🧱 前提条件

- Docker Desktop 4.20+（または Docker Engine + Compose v2）
- Node.js 20.x（フロントエンド単体起動用）
- JDK 17（Spring Boot 実行用）
- GNU Make 3.81+（任意）

---

## 🧪 テスト実行方法

本プロジェクトでは、Spring Boot + Gradle による自動テストを Docker 環境で実行できます。

### 実行コマンド

```bash
# バックエンドAPI（H2メモリDB使用）の自動テスト実行
docker run --rm \
  -v "$(pwd)/backend":/app \
  -w /app \
  gradle:8.4-jdk17 \
  ./gradlew clean test --no-daemon
```

### 実行内容

- Spring Boot の単体・結合テスト（FeatureTestを含む）を実行

- H2メモリDBを利用して Flyway マイグレーションをスキップ

- Project / Task / PingController など主要APIのCRUD検証を行う

- テスト結果レポートは`backend/build/reports/tests/test/index.html`に出力されます

### 成功時の出力例

```bash
BUILD SUCCESSFUL in 33s
6 actionable tasks: 6 executed
```

上記が表示されれば、すべてのテストが正常に通過しています