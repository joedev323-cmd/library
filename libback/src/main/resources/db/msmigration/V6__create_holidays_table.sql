CREATE TABLE HOLIDAYS (
                          ID BIGINT IDENTITY(1,1) PRIMARY KEY,
                          HOLIDAY_DATE DATE NOT NULL,
                          NAME VARCHAR(150) NOT NULL,
                          DESCRIPTION VARCHAR(500),
                          CREATED_AT DATETIMEOFFSET NOT NULL,
                          UPDATED_AT DATETIMEOFFSET,

                          CONSTRAINT UK_HOLIDAYS_DATE
                              UNIQUE (HOLIDAY_DATE)
);