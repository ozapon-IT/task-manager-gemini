# GEMINI.md — プロジェクト設定／ガイドライン for Gemini CLI

## 概要 / 目的  
このファイルは Gemini CLI にプロジェクト固有の文脈、ポリシー、スタイル／設計指針を与えるものです。  
Gemini に対して、このリポジトリで期待する振る舞いや守ってほしいルールを伝える「永続的コンテキスト」として機能します。  
大規模リポジトリや複数人開発で一貫性を保つために、GEMINI.md の記述内容は **定期的に見直すこと** を推奨します（ベストプラクティス）:contentReference[oaicite:1]{index=1}

---

## プロジェクト構成 & 技術スタック

- フロントエンド：Nuxt3（TypeScript モード有効）  
- バックエンド：Spring Boot（Java / Kotlin）  
- API 層を挟む構成（これまでの Nuxt3 monorepo 構成から API 部分を Spring Boot に置き換えるイメージ）  
- 共通型定義やインタフェースは可能な限り TypeScript 側で厳密に管理  
- 開発ツールチェーン例  
  - `pnpm` / `npm` / `yarn`（プロジェクトで統一）  
  - `tsconfig.json`：厳格モード（`strict: true` など）  
  - フロント側 linter / formatter（例：ESLint + Prettier）  
  - バックエンド側スタイル：Spring Boot 側は既存のコーディング規約を尊重

---

## コーディング規約 & スタイル

### フロント（Nuxt3／TypeScript 側）

- 常に **型安全性を重視**  
  - `strict: true`、`noImplicitAny: true` 等を有効  
  - any 型は例外的にのみ使う  
  - 型定義ファイル（`types/` 等）を活用  
- Vue コンポーネント：  
  - `<script setup lang="ts">` スタイル推奨  
  - Composition API 優先  
  - 明確な命名規則（例：`useXxx`、`XxxComposable`、`XxxService` など）  
- ESLint / Prettier は CI／開発時に自動整形・警告が出るように  
- CSS / スタイル：Tailwind や scoped CSS 等使用するならルールを明記  
- フロント側でバックエンド API を呼ぶ際、型安全 API クライアント（e.g. `axios` + 自動生成型 or `openapi‐generator`）を使う想定

### バックエンド（Spring Boot 側）

- プロジェクト既存のスタイルガイドを尊重  
- REST API 設計：標準的な HTTP ステータスコード、エラーハンドリングを明確に  
- DTO / モデル命名は明確に：`Request`, `Response`, `Entity` の区分  
- Validation（例：Bean Validation）を適切に使用  
- ロギング、例外管理、共通レスポンス形式の整備  
- ドキュメンテーション（OpenAPI / Swagger など）を整備し、フロントと共有

---

## Gemini に期待する振る舞い & 制約

### 振る舞い

1. Gemini にタスクを投げるときは **小さく分割された命令（サブタスク化）** で、
  　複雑な要求を一度に全部出さないようにする。これにより生成物の品質を高める。:contentReference[oaicite:2]{index=2}  
2. Gemini は既存コードを参照しながら変更を提案すること。コード構造／モジュール依存関係を壊さないように。  
3. 提案されたコード変更には常に差分（diff）として出力してもらい、レビューできる形を保つ。  
4. Gemini がテストを併せて生成できる場合は、テストケースを先に生成 → 実装（TDD スタイル）を優先。:contentReference[oaicite:3]{index=3}  
5. コード生成や修正時、`!` を使った shell コマンド実行や `read-file` コマンドの活用を許可する。ただし自動実行は抑制し、事前に確認する。:contentReference[oaicite:4]{index=4}  
6. プロンプトでは必ず “なぜこの変更をするか” や “このモジュールの責務” を明記し、意図を持たせる。

### 制約・禁止事項

- 機密情報（APIキー / .env の内容 / 認証情報など）を出力してはいけない。  
- 無関係なファイルに変更を加えない。指定されたモジュール／領域外の改変は避ける。  
- コードスタイル（インデント、命名、コメントの形式など）を既存規約と大きく乖離しないように。  
- 大規模リファクタ（構成の大転換など）は事前に設計意図を相談の上進める。

---

## 開発フロー & 実践例

以下は Gemini に指示を出すときのプロンプト（命令文）の例：

- 「モジュール X の既存コードを読み込んで、バグ箇所を修正し、修正前後の diff を示して。追加したユニットテストも一緒に出して。」
- 「新しい API エンドポイント `/api/users/search` を設計して。Request／Response 型定義も出して、フロント側の呼び出し例（型安全なクライアント付き）を提示して。」
- 「この Vue コンポーネントに型安全な props 定義を追加して。既存スタイルを壊さないように。差分で示してほしい。」
- 「CLI コマンド `!pnpm test` を実行してエラーが出たので、そのエラーを読み取り、修正案と diff を出して。」

また、Gemini には `/stats` やセッションチェックポイント、プロンプトのチェックなどの機能があるので、セッション状態を定期的に確認して使う。:contentReference[oaicite:5]{index=5}

---

## ツール & コマンド参照

- **preflight チェック**: 変更をリポジトリに適用する前に `gemini preflight` を実行してビルドやテストを確認。:contentReference[oaicite:6]{index=6}  
- **認証方法**:  
  - Google アカウントで OAuth ログイン  
  - 環境変数 `GEMINI_API_KEY` を使った API キー方式  
  - Vertex AI 経由（組織用途）:contentReference[oaicite:7]{index=7}  
- **MCP 拡張**: 必要に応じて MCP サーバ（外部ツール連携）を設定して Gemini に機能を拡張できる。:contentReference[oaicite:8]{index=8}  
- **除外対象 / 無視ファイル**:  
  - `node_modules/`, `dist/`, `build/` 等のビルド成果物  
  - 大容量バイナリやログファイル  
  - `.git/`, `.env*` 等の機密または管理外ファイル  
- **更新運用**:  
  - GEMINI.md はプロジェクト進行とともに更新  
  - 新しいモジュール追加時やルール変更時は都度内容反映  
  - チームでの運用ルールを明記（レビュー時に GEMINI.md を参照するなど）

---

## チーム運用／注意点

- チームメンバー全員がこの GEMINI.md を理解し、Gemini への指示出しに統一感を持てるようにミニガイドを共有  
- Gemini が出した変更は **必ずレビュー** を行う  
- 自動生成されたコードでも、セキュリティ・例外処理・性能面を見逃さずチェック  
- 大きな設計変更や構造改変時は、最初に Gemini に設計案を出してもらい、設計レビューをしながら進める  
- GEMINI.md を逐次改善し、不要に冗長になるセクションは整理（“Practical Gemini CLI: Structured approach to bloated GEMINI.md” の指摘を参考に）:contentReference[oaicite:9]{index=9}

---
