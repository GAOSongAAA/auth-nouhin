#!/bin/bash
set -e

# 定义颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# 定义路径
COMMON_DIR="/home/workspace/api/common"
SHOHO_DIR="/home/workspace/api/shoho"
TARGET_DIR="$SHOHO_DIR/target"
JAR_NAME="common-0.0.1-SNAPSHOT.jar"
SHOHO_JAR="shoho.jar"

echo -e "${CYAN}========================================${NC}"
echo -e "${WHITE}🚀 Spring Boot アプリケーション起動スクリプト${NC}"
echo -e "${CYAN}========================================${NC}"

echo -e "${YELLOW}📁 設定されたパス:${NC}"
echo -e "  ${BLUE}共通処理: ${COMMON_DIR}${NC}"
echo -e "  ${BLUE}メイン処理: ${SHOHO_DIR}${NC}"
echo -e "  ${BLUE}実行ディレクトリ: ${TARGET_DIR}${NC}"

echo -e "\n${PURPLE}🔧 共通処理をビルド実行...${NC}"
cd "$COMMON_DIR"
echo -e "${CYAN}📍 現在のディレクトリ: $(pwd)${NC}"
mvn clean install -DskipTests 
echo -e "${GREEN}✅ 共通処理のビルド完了!${NC}"

echo -e "\n${PURPLE}🔧 メインパッケージをビルド...${NC}"
cd "$SHOHO_DIR"
echo -e "${CYAN}📍 現在のディレクトリ: $(pwd)${NC}"
mvn clean package -DskipTests 
echo -e "${GREEN}✅ メインパッケージのビルド完了!${NC}"

echo -e "\n${RED}🚀 アプリケーション実行開始...${NC}"
cd "$TARGET_DIR"
echo -e "${CYAN}📍 実行ディレクトリ: $(pwd)${NC}"
echo -e "${YELLOW}📦 実行するJAR: ${SHOHO_JAR}${NC}"
echo -e "${CYAN}========================================${NC}"
java -jar "$SHOHO_JAR"
