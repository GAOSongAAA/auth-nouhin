-- 納品システム SQL Server 數據庫初始化腳本
-- 在容器啟動時，數據庫已經由環境變量創建

USE master;
GO

-- 檢查數據庫是否存在，如果不存在則創建
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'nouhin_db')
BEGIN
    CREATE DATABASE nouhin_db;
    PRINT 'Database nouhin_db created successfully.';
END
ELSE
BEGIN
    PRINT 'Database nouhin_db already exists.';
END
GO

-- 切換到目標數據庫
USE nouhin_db;
GO

-- 創建登錄用戶（如果需要的話，SA用戶已經存在）
-- 這裡可以創建額外的用戶，但通常在容器環境中使用SA用戶即可

PRINT 'Database initialization completed successfully.';
GO 