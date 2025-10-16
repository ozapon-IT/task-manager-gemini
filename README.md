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

.
├── backend/ # Spring Boot API（JPA, Controller, Service, Repository 層を含む）
│ ├── src/main/java/com/example/taskmanager
│ ├── src/test/java/com/example/taskmanager (Featureテスト)
│ └── build.gradle
├── frontend/ # Nuxt 3 + TypeScript SPA
│ ├── src/
│ ├── types/ # 共通型定義
│ └── nuxt.config.ts
├── docker/ # 各コンテナ (nginx, spring, nuxt) の設定
├── Makefile # Docker 操作用ショートカット
└── docker-compose.yml

---

## 🧱 前提条件

- Docker Desktop 4.20+（または Docker Engine + Compose v2）
- Node.js 20.x（フロントエンド単体起動用）
- JDK 17（Spring Boot 実行用）
- GNU Make 3.81+（任意）

---


