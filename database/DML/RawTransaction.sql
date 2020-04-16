CREATE TABLE `RAW_TRANSACTION`
(
    `GUID`       BIGINT      NOT NULL,
    `SOURCE_ID`     BIGINT      NULL,
    `RAW_RECORD`    TEXT        NULL,
    `SEQUENCE`      BIGINT      NOT NULL,
    `MSG_ID`        VARCHAR(100),
    `VERSION`       INT         NOT NULL,
    `DUPLICATE_FLG` INT         NOT NULL,
    `CREATED_AT`    TIMESTAMP   NULL DEFAULT CURRENT_TIMESTAMP,
    `CREATED_BY`    VARCHAR(30) NULL,
    `CREATED_FROM`  VARCHAR(30) NULL,
    `UPDATED_AT`    TIMESTAMP   NULL
        ON UPDATE CURRENT_TIMESTAMP,
    `UPDATED_BY`    VARCHAR(30),
    `UPDATED_FROM`  VARCHAR(30) NULL,
    PRIMARY KEY (`GUID`),
    INDEX `RAW_TRANSACTION_NX1` (`CREATED_AT` DESC)
)
    ENGINE = InnoDB;
