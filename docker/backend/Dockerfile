# ---- 開発用Dockerfile ----
FROM gradle:8.7-jdk17 AS dev

# 作業ディレクトリ設定
WORKDIR /app

# Gradleキャッシュを活かす（依存取得の高速化）
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || return 0

# ソースコードをコピー（ボリュームマウントで上書きされる想定）
COPY . .

# DevToolsを利用するためのプロファイル（任意）
ENV SPRING_PROFILES_ACTIVE=local

# ホットリロード・デバッグ有効化
EXPOSE 8080 5005

# Spring Bootを直接実行（bootRunで即時反映）
CMD ["./gradlew", "bootRun", "--no-daemon"]
