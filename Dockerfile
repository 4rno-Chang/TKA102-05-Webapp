# 階段 1：Maven 編譯與打包 (Build Stage)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 快取相依套件：先複製 pom.xml 並預先下載依賴
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# 複製原始碼並進行編譯打包（略過測試以加快建置速度）
COPY src ./src
RUN mvn clean package -DskipTests

# 階段 2：輕量運行環境 (Runtime Stage)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 設定時區為亞洲/台北
ENV TZ=Asia/Taipei
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 將階段 1 編譯出的 WAR 檔案複製進來並命名為 app.war
COPY --from=builder /app/target/*.war app.war

# 宣告預設對外埠號
EXPOSE 8080

# 啟動 Spring Boot 應用程式
ENTRYPOINT ["java", "-jar", "app.war"]
