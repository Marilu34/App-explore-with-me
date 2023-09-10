DROP TABLE IF EXISTS stat_records;

CREATE TABLE stat_records
(
    record_id  BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1) NOT NULL,
    app_name   VARCHAR(32)                                        NOT NULL,
    uri        VARCHAR(64)                                        NOT NULL,
    ip         VARCHAR(15)                                        NOT NULL,
    time_stamp TIMESTAMP WITHOUT TIME ZONE                        NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (record_id)
);
