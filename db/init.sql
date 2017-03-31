DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

CREATE TABLE public.person (
  identification_code VARCHAR(60)  NOT NULL,
  first_name          VARCHAR(50),
  last_name           VARCHAR(50),
  email               VARCHAR(256) NOT NULL,
  pswd_hash           VARCHAR(255) NOT NULL,
  language            VARCHAR(2)   NOT NULL,
  creator             VARCHAR(60),
  CONSTRAINT person_pk PRIMARY KEY (identification_code)
);

CREATE SEQUENCE public.person_role_list_id_seq;

CREATE TABLE public.person_role_list (
  id                         INTEGER      NOT NULL DEFAULT nextval('public.person_role_list_id_seq'),
  person_identification_code VARCHAR(60)  NOT NULL,
  role_list                  VARCHAR(100) NOT NULL,
  CONSTRAINT person_role_list_pk PRIMARY KEY (id)
);

ALTER SEQUENCE public.person_role_list_id_seq OWNED BY public.person_role_list.id;

CREATE TABLE public.examination (
  sample_number         VARCHAR(100) NOT NULL,
  patient_id            VARCHAR(60),
  physician_id          VARCHAR(60),
  creator               VARCHAR(60),
  expert_id             VARCHAR(60),
  patient_date_of_birth DATE,
  pregnancy_length      INTEGER,
  date_of_examination   DATE,
  bmi                   DOUBLE PRECISION,
  number_of_fetuses     INTEGER,
  has_one_fetus         BOOLEAN,
  has_tumor             BOOLEAN,
  has_ivf               BOOLEAN,
  has_hsct              BOOLEAN,
  nationality           VARCHAR(2),
  date_of_sampling      DATE,
  sample_arrival_date   DATE,
  email                 VARCHAR(256),
  was_prognosis_true    BOOLEAN,
  expert_assessment     VARCHAR(5000),
  decision              VARCHAR(20),
  CONSTRAINT examination_pk PRIMARY KEY (sample_number)
);
COMMENT ON COLUMN public.examination.bmi IS 'Body Mass Index';


CREATE SEQUENCE public.db_data_data_id_seq;

CREATE TABLE public.db_data (
  data_id              INTEGER      NOT NULL DEFAULT nextval('public.db_data_data_id_seq'),
  sample_number        VARCHAR(100) NOT NULL,
  raw_data             VARCHAR(1000),
  barcode              VARCHAR(15),
  raw_reads            INTEGER,
  snp                  INTEGER,
  pcr_redundancy       DOUBLE PRECISION,
  informative_snp      INTEGER,
  unique_reads_per_snp DOUBLE PRECISION,
  lot                  VARCHAR(1000),
  dna_separation       DATE,
  CONSTRAINT db_data_pk PRIMARY KEY (data_id)
);

ALTER SEQUENCE public.db_data_data_id_seq OWNED BY public.db_data.data_id;

CREATE TABLE public.run (
  run_id         INTEGER     NOT NULL,
  test_type      VARCHAR(20) NOT NULL,
  data_id        INTEGER     NOT NULL,
  date_of_run    DATE,
  q_score        DOUBLE PRECISION,
  gender         VARCHAR(1),
  fetal_fraction DOUBLE PRECISION,
  CONSTRAINT run_pk PRIMARY KEY (run_id)
);

CREATE SEQUENCE public.chromosome_id_seq;

CREATE TABLE public.chromosome (
  id        INTEGER NOT NULL DEFAULT nextval('public.chromosome_id_seq'),
  run_id    INTEGER NOT NULL,
  ch_number INTEGER,
  score_1   DOUBLE PRECISION,
  score_2   DOUBLE PRECISION,
  score_3   DOUBLE PRECISION,
  score_4   DOUBLE PRECISION,
  Plot      VARCHAR(1000),
  CONSTRAINT chromosome_pk PRIMARY KEY (id)
);

ALTER SEQUENCE public.chromosome_id_seq OWNED BY public.chromosome.id;

ALTER TABLE public.person_role_list
  ADD CONSTRAINT person_person_role_list_fk
FOREIGN KEY (person_identification_code)
REFERENCES public.person (identification_code)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;

ALTER TABLE public.db_data
  ADD CONSTRAINT physical_examination_data_fk
FOREIGN KEY (sample_number)
REFERENCES public.examination (sample_number)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;

ALTER TABLE public.run
  ADD CONSTRAINT data_run_fk
FOREIGN KEY (data_id)
REFERENCES public.db_data (data_id)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;

ALTER TABLE public.chromosome
  ADD CONSTRAINT run_ch21_fk
FOREIGN KEY (run_id)
REFERENCES public.run (run_id)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;

INSERT INTO public.person (identification_code, creator, email, first_name, language, last_name, pswd_hash) VALUES
  ('22222222222', '', 'admin@admin', 'Admin', 'ET', 'Admin','$2a$10$p8C4rYB35UO/i18W9v5IeexQ.A.7CE41/fxczeofEdTcTN6iGV8Rm');
INSERT INTO public.person_role_list (person_identification_code, role_list) VALUES ('22222222222', 'VIEW_REPORT');
INSERT INTO public.person_role_list (person_identification_code, role_list) VALUES ('22222222222', 'CREATE_REPORT');
INSERT INTO public.person_role_list (person_identification_code, role_list) VALUES ('22222222222', 'CREATE_EXAMINATION');
INSERT INTO public.person_role_list (person_identification_code, role_list) VALUES ('22222222222', 'ADD_USERS');

INSERT INTO public.examination (sample_number,
                                patient_id,
                                physician_id,
                                creator,
                                expert_id,
                                patient_date_of_birth,
                                pregnancy_length,
                                date_of_examination,
                                bmi,
                                number_of_fetuses,
                                has_one_fetus,
                                has_tumor,
                                has_ivf,
                                has_hsct,
                                nationality,
                                date_of_sampling,
                                sample_arrival_date,
                                email,
                                was_prognosis_true,
                                expert_assessment,
                                decision)
VALUES ('ASFG18012017',
  '11111111111',
  '47810012744',
  'pr11t@tdl.ee',
  NULL,
  '1978-10-01',
  9,
  '2017-01-18',
  25,
  1,
  TRUE,
  FALSE,
  FALSE,
  FALSE,
  'EE',
  '2017-01-18',
  '2017-01-18',
  'pr11t@tdl.ee',
  NULL,
  NULL,
  NULL);

INSERT INTO public.db_data (data_id, sample_number, raw_data, barcode, raw_reads, snp, pcr_redundancy, informative_snp, unique_reads_per_snp, lot, dna_separation)
VALUES (1, 'ASFG18012017', NULL, 'ABC234', 124923, 23478, 57, 2500, 8, 'FWIOJE93842', '2017-01-18');
INSERT INTO public.db_data (data_id, sample_number, raw_data, barcode, raw_reads, snp, pcr_redundancy, informative_snp, unique_reads_per_snp, lot, dna_separation)
VALUES (2, 'ASFG18012017', NULL, 'FGW444', 423489, 3546, 87, 4234, 10, 'WFE78243I', '2017-01-18');

INSERT INTO public.run (run_id, test_type, data_id, date_of_run, q_score, gender, fetal_fraction)
VALUES (1, 'NIPT', 1, '2017-01-18', 98, 'M', 0.15);

INSERT INTO public.chromosome (id, run_id, ch_number, score_1, score_2, score_3, score_4, plot) VALUES
  (1, 1, 2, 98, 99, 100, 98,
   'http://kodu.ut.ee/~ppaluoja/hmm1.PNG');
INSERT INTO public.chromosome (id, run_id, ch_number, score_1, score_2, score_3, score_4, plot) VALUES
  (2, 1, 3, 87, 98, 99, 97,
   'http://kodu.ut.ee/~ppaluoja/hmm2.PNG');
INSERT INTO public.chromosome (id, run_id, ch_number, score_1, score_2, score_3, score_4, plot) VALUES
  (3, 1, 13, 87, 78, 88, 67,
   'http://kodu.ut.ee/~ppaluoja/hmm1.PNG');

INSERT INTO public.person (identification_code, first_name, last_name, email, pswd_hash, language, creator) VALUES
  ('11111111111', 'Test', 'Aavik', 'pr11t@tdl.ee', '$2a$10$EDrzTU9uuUYS89ZKIt54K.bzinFFpcd.9SM0GaT1hUs2NC1kAmqFy', 'ET',
   '22222222222');

INSERT INTO public.person_role_list (id, person_identification_code, role_list) VALUES (5, '11111111111', 'VIEW_REPORT');
INSERT INTO public.person_role_list (id, person_identification_code, role_list) VALUES (6, '11111111111', 'CREATE_REPORT');
INSERT INTO public.person_role_list (id, person_identification_code, role_list) VALUES (7, '11111111111', 'CREATE_EXAMINATION');
INSERT INTO public.person_role_list (id, person_identification_code, role_list) VALUES (8, '11111111111', 'ADD_USERS');