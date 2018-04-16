CREATE TABLE  USERS(
   USERNAME TEXT PRIMARY KEY     NOT NULL,
   PASSWORD           TEXT    NOT NULL,
   EMAIL            TEXT,
   FIRSTNAME            TEXT,
   LASTNAME            TEXT,
   ALIAS            TEXT,
   PROFILE_IMG_URL TEXT,
   GENDER INTEGER,
   SOCIALTYPE INTEGER,
   CREATEDAT bigint,
   UPDATEDAT bigint

);

CREATE TABLE  ACCOUNTS(
   ID TEXT PRIMARY KEY     NOT NULL,
   NAME           TEXT    NOT NULL,
   DESCRIPTION            TEXT,
   STATUS            SMALLINT,
   DEFAULT_USER TEXT,
   CREATEDAT bigint,
   UPDATEDAT bigint

);

CREATE TABLE  USER_ACCOUNT(
   ID_USER TEXT   NOT NULL,
   ID_ACCOUNT  TEXT      NOT NULL,
   IS_ADMIN           BOOLEAN DEFAULT FALSE,
   CREATEDAT bigint
	PRIMARY KEY (ID_USER, ID_ACCOUNT)
);

 CREATE TABLE  CATEGORIES(
   ID TEXT PRIMARY KEY     NOT NULL,
   TYPE           SMALLINT    NOT NULL,
   USER_VALUE TEXT,
   IS_EDITABLE           BOOLEAN,
   ACCOUNT_ID            TEXT,
   ICONID SMALLINT,
   CREATEDAT bigint,
   UPDATEDAT bigint

);


 CREATE TABLE  MOVEMENTS(
   ID TEXT PRIMARY KEY     NOT NULL,
   TYPE           SMALLINT    NOT NULL,
   AMOUNT FLOAT,
   EXECUTEDBY           TEXT,
   ACCOUNTID           TEXT,
   NOTE            TEXT,
   CATEGORYID TEXT,
   EXECUTEDAT bigint,
   CREATEDAT bigint,
   UPDATEDAT bigint,
   EXEC_DAY SMALLINT,
   EXEC_MONTH SMALLINT,
   EXEC_YEAR SMALLINT,
   IS_AUTO            BOOLEAN DEFAULT FALSE

);

CREATE TABLE  AUTO_MOVEMENT_SETTINGS(
   ID TEXT PRIMARY KEY     NOT NULL,
   NAME           TEXT    NOT NULL,
   DESCRIPTION            TEXT,
   FROM_DATE            bigint,
   END_DATE            bigint,
   FREQUENCY            SMALLINT,
   ACCOUNT  TEXT,
   AMOUNT FLOAT,
   CATEGORYID TEXT,
   TYPE           SMALLINT    NOT NULL,
   last_exec bigint,
   multiplier int,
   username TEXT,
   CREATEDAT bigint,
   UPDATEDAT bigint

);

CREATE TABLE  USER_ACCOUNT_INVITE(
   ID TEXT PRIMARY KEY     NOT NULL,
   username TEXT,
   invitedBy TEXT,
   ACCOUNT_ID TEXT,
   CREATEDAT bigint,
   UPDATEDAT bigint

);

CREATE UNIQUE INDEX USERNAME_ci_idx ON users ((lower(USERNAME)));
CREATE  INDEX EMAIL_ci_idx ON users ((lower(EMAIL)));
CREATE SEQUENCE serial START 101;

CREATE OR REPLACE FUNCTION stamp() RETURNS trigger AS $stamp$
    BEGIN

        -- Remember who changed the payroll when
        IF (TG_OP = 'INSERT') THEN
        	NEW.CREATEDAT := trunc(extract(epoch from now() at time zone 'utc'));
        END IF;
        NEW.UPDATEDAT := trunc(extract(epoch from now() at time zone 'utc'));
        RETURN NEW;
    END;
$stamp$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION stampMovement() RETURNS trigger AS $stampMovement$
    BEGIN

        IF (TG_OP = 'INSERT') THEN
        	NEW.CREATEDAT := trunc(extract(epoch from now() at time zone 'utc'));
        END IF;
        NEW.UPDATEDAT := trunc(extract(epoch from now() at time zone 'utc'));
        NEW.EXEC_DAY = EXTRACT(DAY from (to_timestamp(NEW.EXECUTEDAT)::timestamp with time zone at time zone 'utc'));
        NEW.EXEC_MONTH = EXTRACT(MONTH from (to_timestamp(NEW.EXECUTEDAT)::timestamp with time zone at time zone 'utc'));
        NEW.EXEC_YEAR = EXTRACT(YEAR from (to_timestamp(NEW.EXECUTEDAT)::timestamp with time zone at time zone 'utc'));
        RETURN NEW;
    END;
$stampMovement$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION unixTimePlusFrequency(FROM_DATE BIGINT, FREQUENCY SMALLINT, multiplier int) RETURNS bigint AS $$
declare ret bigint;
unitInterval int;
        BEGIN
                IF FREQUENCY = 0 THEN
                	unitInterval := multiplier *7;
                	ret :=  EXTRACT (epoch FROM ((to_timestamp(FROM_DATE)::timestamp with time zone at time zone 'utc')+  ( unitInterval || ' days')::interval));
                END IF;
                IF FREQUENCY = 1 THEN
                	unitInterval := multiplier *1;
                	ret :=  EXTRACT (epoch FROM ((to_timestamp(FROM_DATE)::timestamp with time zone at time zone 'utc')+  ( unitInterval || ' month')::interval));
                END IF;
                IF FREQUENCY = 2 THEN
                 	unitInterval := multiplier *3;
                	ret :=  EXTRACT (epoch FROM ((to_timestamp(FROM_DATE)::timestamp with time zone at time zone 'utc')+  ( unitInterval || ' month')::interval));
                END IF;

                IF FREQUENCY = 3 THEN
                    unitInterval := multiplier *6;
                	ret :=  EXTRACT (epoch FROM ((to_timestamp(FROM_DATE)::timestamp with time zone at time zone 'utc')+  ( unitInterval || ' month')::interval));
                END IF;

               IF FREQUENCY = 4 THEN
                     unitInterval := multiplier *1;
                	ret :=  EXTRACT (epoch FROM ((to_timestamp(FROM_DATE)::timestamp with time zone at time zone 'utc')+  ( unitInterval || ' year')::interval));
                END IF;

                return ret;
        END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER users_stamp BEFORE INSERT OR UPDATE ON users
    FOR EACH ROW EXECUTE PROCEDURE stamp();


CREATE TRIGGER ACCOUNTS_stamp BEFORE INSERT OR UPDATE ON ACCOUNTS
    FOR EACH ROW EXECUTE PROCEDURE stamp();

CREATE TRIGGER categories_stamp BEFORE INSERT OR UPDATE ON CATEGORIES
    FOR EACH ROW EXECUTE PROCEDURE stamp();


CREATE TRIGGER MOVENENTS_stamp BEFORE INSERT OR UPDATE ON MOVEMENTS
    FOR EACH ROW EXECUTE PROCEDURE stampMovement();

CREATE TRIGGER MOV_SETT_stamp BEFORE INSERT OR UPDATE ON AUTO_MOVEMENT_SETTINGS
    FOR EACH ROW EXECUTE PROCEDURE stamp();

CREATE TRIGGER USER_ACC_stamp BEFORE INSERT OR UPDATE ON USER_ACCOUNT_INVITE
    FOR EACH ROW EXECUTE PROCEDURE stamp();
