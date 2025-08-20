--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-08-20 12:52:27

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4972 (class 1262 OID 25286)
-- Name: employee-administration; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "employee-administration" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'de-DE';


ALTER DATABASE "employee-administration" OWNER TO postgres;

\encoding SQL_ASCII
\connect -reuse-previous=on "dbname='employee-administration'"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 50099)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- TOC entry 4973 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 25646)
-- Name: address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.address (
    address_id uuid NOT NULL,
    city character varying(50) NOT NULL,
    country character varying(100) NOT NULL,
    doornumber character varying(50) NOT NULL,
    housenumber character varying(50) NOT NULL,
    street character varying(100) NOT NULL,
    zip character varying(50) NOT NULL
);


ALTER TABLE public.address OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 25653)
-- Name: person; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.person (
    telephone integer NOT NULL,
    address_id uuid NOT NULL,
    person_id uuid NOT NULL,
    superior_id uuid,
    email character varying(255) NOT NULL,
    firstname character varying(50) NOT NULL,
    lastname character varying(50) NOT NULL,
    password character varying(50) NOT NULL,
    status character varying(255) DEFAULT 'JUST_CREATED'::character varying,
    username character varying(50) NOT NULL,
    vacation_entitlement integer NOT NULL,
    vacation_remaining integer,
    week_work_hours smallint,
    CONSTRAINT person_status_check CHECK (((status)::text = ANY (ARRAY['PRESENT'::text, 'ABSENT'::text, 'MEDICAL_LEAVE'::text, 'VACATION'::text, 'INACTIVE'::text, 'JUST_CREATED'::text]))),
    CONSTRAINT person_vacation_remaining_check CHECK (((vacation_remaining <= 35) AND (vacation_remaining >= 0))),
    CONSTRAINT person_week_work_hours_check CHECK (((week_work_hours >= 0) AND (week_work_hours <= 60)))
);


ALTER TABLE public.person OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 33699)
-- Name: request; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.request (
    request_id uuid NOT NULL,
    creation_date timestamp(6) without time zone NOT NULL,
    end_date date NOT NULL,
    notes character varying(255),
    start_date date NOT NULL,
    status character varying(255) NOT NULL,
    person_id uuid,
    CONSTRAINT request_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'DENIED'::character varying, 'ACCEPTED'::character varying, 'EXPIRED'::character varying])::text[])))
);


ALTER TABLE public.request OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 25665)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    person_id uuid NOT NULL,
    role character varying(255),
    CONSTRAINT roles_role_check CHECK (((role)::text = ANY ((ARRAY['EMPLOYEE'::character varying, 'MANAGER'::character varying, 'ADMIN'::character varying])::text[])))
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 33639)
-- Name: time_stamp; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.time_stamp (
    time_stamp_id uuid NOT NULL,
    date date,
    end_time time(6) without time zone,
    start_time time(6) without time zone,
    person_id uuid,
    worked_hours double precision
);


ALTER TABLE public.time_stamp OWNER TO postgres;

--
-- TOC entry 4962 (class 0 OID 25646)
-- Dependencies: 218
-- Data for Name: address; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.address VALUES ('11111111-1111-1111-1111-111111111111', 'Vienna', 'Austria', '2B', '42', 'Ringstraße', '1010');
INSERT INTO public.address VALUES ('22222222-aaaa-4bbb-bccc-333333333333', 'Graz', 'Austria', '1A', '77', 'Hauptstraße', '8010');
INSERT INTO public.address VALUES ('33333333-abcd-4a34-bef0-444444444444', 'Linz', 'Austria', '3C', '12', 'Donauweg', '4020');
INSERT INTO public.address VALUES ('44444444-dead-4fff-baaa-555555555555', 'Salzburg', 'Austria', '5D', '88', 'Mozartstraße', '5020');
INSERT INTO public.address VALUES ('99999999-aaaa-bbbb-cccc-eeeeeeeeeeee', 'Innsbruck', 'Austria', '7C', '11', 'Tirolerstraße', '6020');
INSERT INTO public.address VALUES ('00000000-0000-0000-0000-000000000001', 'Vienna', 'Austria', '1', '10', 'Teststraße', '1010');
INSERT INTO public.address VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Graz', 'Austria', '2', '5', 'Hauptstraße', '8010');
INSERT INTO public.address VALUES ('896914e6-ec8f-4f6f-ad47-b04819a34802', 'Vienna', 'Austria', '1', '1', 'Street', '1070');
INSERT INTO public.address VALUES ('a61b2502-e104-4886-9f57-40c2a7a6b497', 'Vienna', 'Austria', '123', '123', 'Street123', '1230');
INSERT INTO public.address VALUES ('54f12028-316d-41d5-961f-6fa9b6cb4387', 'Vienna', 'Austria', '1', '1', 'Fabianstrasse', '1010');
INSERT INTO public.address VALUES ('df777443-d1eb-4b53-af15-d72661adf668', 'Wien', 'Austria', '1', '1', 'new street', '1050');


--
-- TOC entry 4963 (class 0 OID 25653)
-- Dependencies: 219
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.person VALUES (444555666, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb06', '6ccb3318-7478-4622-b918-45bc6bc886d4', 'james.bauer2@example.com', 'James', 'Bauer', 'passjames', 'PRESENT', 'james.bauer2', 25, 25, 40);
INSERT INTO public.person VALUES (555666777, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb07', '6ccb3318-7478-4622-b918-45bc6bc886d4', 'sophie.keller2@example.com', 'Sophie', 'Keller', 'passsophie', 'PRESENT', 'sophie.keller2', 25, 25, 40);
INSERT INTO public.person VALUES (123345, '44444444-dead-4fff-baaa-555555555555', '339ca6f0-acf4-4b48-8fd1-cd489feac9c2', 'fbefb2f2-3d70-4a3f-b32e-b6fb8bf30287', 'email@email.com', 'fab1', 'fab1', 'fab', 'JUST_CREATED', 'fabian', 25, 0, 40);
INSERT INTO public.person VALUES (1234556, '00000000-0000-0000-0000-000000000001', 'ba1e6a30-33c5-460a-91e4-05a0ec13008f', NULL, 'mensch@mensch.de', 'feffwf', 'efqffqwfq2', 'mensch', 'INACTIVE', 'mensch', 25, 25, 40);
INSERT INTO public.person VALUES (1484851, '44444444-dead-4fff-baaa-555555555555', '97da5b0b-208a-47b0-bed4-9f71187919fd', NULL, 'Manager@Manager.com', 'Manager', 'Manager', 'manager1', 'INACTIVE', 'manager1', 25, 0, 40);
INSERT INTO public.person VALUES (516196, '54f12028-316d-41d5-961f-6fa9b6cb4387', '3c17d5db-6010-4870-b85a-be76bc7d2ae0', NULL, 'DerMeister@DerMeister.com', 'DerMeister', 'DerMeister', '151515fe', 'JUST_CREATED', 'feffefe', 25, 25, 40);
INSERT INTO public.person VALUES (123456, '44444444-dead-4fff-baaa-555555555555', '35053f6d-f4ed-46a5-aed1-bb1b6fb8c36b', '3c17d5db-6010-4870-b85a-be76bc7d2ae0', 'fab@fab.com', 'Maxl', 'Meister', 'fab', 'JUST_CREATED', 'fab', 25, 25, 40);
INSERT INTO public.person VALUES (123445, '33333333-abcd-4a34-bef0-444444444444', 'fbefb2f2-3d70-4a3f-b32e-b6fb8bf30287', NULL, 'super@super.com', 'Super', 'Super', 'super', 'JUST_CREATED', 'super', 25, 25, 40);
INSERT INTO public.person VALUES (222333444, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb04', NULL, 'david.schmidt2@example.com', 'David', 'Schmidt', 'passdavid', 'PRESENT', 'david.schmidt2', 25, 25, 40);
INSERT INTO public.person VALUES (151518, '44444444-dead-4fff-baaa-555555555555', '02c7fb1d-f185-4523-aab4-21ddd25927a8', NULL, 'DAMANAGER@DAMANAGER.com', 'DAMANAGER', 'DAMANAGER', 'DAMANAGER', 'INACTIVE', 'DAMANAGER', 25, 0, 25);
INSERT INTO public.person VALUES (1852128, '22222222-aaaa-4bbb-bccc-333333333333', '308ffe1b-9918-401f-8d2a-609038d49d62', NULL, 'fabiuan@fabian.com', 'Fabian123', 'Fabian123', 'fabian23', 'PRESENT', 'fabian23', 25, 0, 43);
INSERT INTO public.person VALUES (151515, '33333333-abcd-4a34-bef0-444444444444', 'c7d0c0d4-88e4-4dc0-b7ed-5cdd3f05ba0c', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'max@supermann.de', 'Max', 'SuperMann', 'maxpass', 'PRESENT', 'maxsupermann', 25, 25, 25);
INSERT INTO public.person VALUES (123456789, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01', NULL, 'anna.employee2@example.com', 'Anna', 'Employee', 'securepass123', 'PRESENT', 'anna.employee2', 25, 25, 40);
INSERT INTO public.person VALUES (123456789, '11111111-1111-1111-1111-111111111111', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', NULL, 'alice@example.com', 'Alicia', 'Anderson', 'alicepass', 'PRESENT', 'alice', 25, 25, 40);
INSERT INTO public.person VALUES (333444555, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb05', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'lisa.maier2@example.com', 'Lisa', 'Maier', 'passlisa', 'PRESENT', 'lisa.maier2', 25, 25, 40);
INSERT INTO public.person VALUES (1811818, '44444444-dead-4fff-baaa-555555555555', 'd9d6c8a5-7aa3-440a-99f4-f8e79f9e23f2', NULL, 'MisterB@MisterB.com', 'MisterB', 'MisterB', 'MisterB', 'INACTIVE', 'MisterB', 23, 23, 23);
INSERT INTO public.person VALUES (987654321, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02', NULL, 'peter.solo2@example.com', 'Peter', 'Solo', 'testpass456', 'PRESENT', 'peter.solo2', 25, 17, 40);
INSERT INTO public.person VALUES (123789456, '99999999-aaaa-bbbb-cccc-eeeeeeeeeeee', 'eeee6666-ffff-4aaa-bbbb-dddd00000008', NULL, 'lisa@example.com', 'Lisa', 'Lerner', 'lisapass', 'PRESENT', 'lisa', 25, 25, 40);
INSERT INTO public.person VALUES (12345, '44444444-dead-4fff-baaa-555555555555', 'b4556373-48dc-4422-8acc-683acbb1a359', NULL, 'a@a.com', 'fnewufgh', 'fewfwefwf', 'fefe', 'JUST_CREATED', 'aed3e', 25, 25, 40);
INSERT INTO public.person VALUES (13424242, '11111111-1111-1111-1111-111111111111', 'e4343e30-67fb-47f7-b8b3-b5d4b8a224ab', NULL, 'tom@tom.de', 'Tom1', 'tom1', 'tom', 'INACTIVE', 'tom', 25, 0, 40);
INSERT INTO public.person VALUES (418484, '22222222-aaaa-4bbb-bccc-333333333333', 'b18c79a5-2807-4a5e-9faf-6838540c82e4', NULL, 'evas@employee.com', 'Evas', 'Employee', 'Employee', 'JUST_CREATED', 'Evas', 25, 25, 40);
INSERT INTO public.person VALUES (111222333, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb03', NULL, 'maria.novak2@example.com', 'Maria', 'Novak', 'passmaria', 'PRESENT', 'maria.novak2', 25, 25, 40);
INSERT INTO public.person VALUES (888888888, '44444444-dead-4fff-baaa-555555555555', 'dddd4444-eeee-4fff-baaa-888888888888', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'helen@example.com', 'Helen', 'Green', 'helenpass', 'ABSENT', 'helen', 25, 23, 40);
INSERT INTO public.person VALUES (666777888, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb08', '3c17d5db-6010-4870-b85a-be76bc7d2ae0', 'lukas.fischer2@example.com', 'Lukas', 'Fischer', 'passlukas', 'PRESENT', 'lukas.fischer2', 25, 25, 40);
INSERT INTO public.person VALUES (1258484, '33333333-abcd-4a34-bef0-444444444444', '67deff89-e5aa-4a57-a749-5f5a7a97e548', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'neuer1@neuer1.com', 'Neuer1', 'Neuer1', 'neuer1', 'JUST_CREATED', 'neuer1', 25, 25, 40);
INSERT INTO public.person VALUES (666321321, '33333333-abcd-4a34-bef0-444444444444', 'cccc3333-dddd-4eee-aaaa-bbbb00000006', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'frank@example.com', 'Frank', 'Foster', 'frankpass', 'VACATION', 'frank', 25, 25, 40);
INSERT INTO public.person VALUES (25345346, '22222222-aaaa-4bbb-bccc-333333333333', '98aa3d58-2004-43b6-8d5b-d6ced213c753', NULL, 'test8@test8.com', 'test8', 'test8', 'test8', 'INACTIVE', 'test8', 25, 25, 40);
INSERT INTO public.person VALUES (123123123, '22222222-aaaa-4bbb-bccc-333333333333', 'bbbb2222-cccc-4ddd-eeee-aaaa00000003', NULL, 'charlie@example.com', 'Charlie', 'Clark', 'charliepass', 'INACTIVE', 'charlie', 25, 25, 40);
INSERT INTO public.person VALUES (321321321, '22222222-aaaa-4bbb-bccc-333333333333', 'bbbb2222-cccc-4ddd-eeee-aaaa00000004', NULL, 'diana@example.com', 'Diana', 'Davis', 'dianapass', 'ABSENT', 'diana', 25, 25, 40);
INSERT INTO public.person VALUES (777777777, '44444444-dead-4fff-baaa-555555555555', 'dddd4444-eeee-4fff-baaa-777777777777', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'george@example.com', 'Georogos1', 'Green1', 'georgepass', 'JUST_CREATED', 'george', 25, 0, 44);
INSERT INTO public.person VALUES (18144818, '33333333-abcd-4a34-bef0-444444444444', '6ccb3318-7478-4622-b918-45bc6bc886d4', NULL, 'MisterD@MisterD.com', 'MisterD', 'MisterD', 'MisterD', 'JUST_CREATED', 'MisterD', 30, 0, 30);
INSERT INTO public.person VALUES (181818, '99999999-aaaa-bbbb-cccc-eeeeeeeeeeee', '98ff1a6f-6fa7-467a-a52d-a719572e0090', NULL, 'max@max.com', 'dfeqwdfq', 'dwdfqwd', 'rolrol', 'INACTIVE', 'derer@der.com', 30, 30, 40);
INSERT INTO public.person VALUES (1515158, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '4cca86db-9166-4ea4-a56a-400e353204a0', NULL, 'MEISATER1@MEISATER1.com', 'MEISATER1', 'MEISATER1', 'MEISATER1', 'INACTIVE', 'MEISATER1', 25, 0, 25);
INSERT INTO public.person VALUES (555123123, '33333333-abcd-4a34-bef0-444444444444', 'cccc3333-dddd-4eee-aaaa-bbbb00000005', NULL, 'eva@example.com', 'Eva', 'Evans', 'evapass', 'ABSENT', 'eva', 25, 25, 40);
INSERT INTO public.person VALUES (987654321, '11111111-1111-1111-1111-111111111111', 'aaaa1111-bbbb-4ccc-ddee-eeee00000002', NULL, 'bob@example.com', 'Bob', 'Brown', 'hashed_password_2', 'INACTIVE', 'bob', 25, 25, 40);
INSERT INTO public.person VALUES (123456789, '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', NULL, 'anna.employee@example.com', 'Anna', 'Employee', 'securepass123', 'PRESENT', 'anna.employee', 25, 25, 40);
INSERT INTO public.person VALUES (987654321, '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', NULL, 'peter.employee@example.com', 'Meister', 'Mann', 'testpass456', 'PRESENT', 'peter.solo', 25, 25, 40);
INSERT INTO public.person VALUES (4588184, '44444444-dead-4fff-baaa-555555555555', '4117ea49-0d96-41db-8760-3af598bb64de', NULL, 'Raschad@Raschad.com', 'Raschad', 'Raschad', 'Raschad', 'INACTIVE', 'Raschad', 25, 25, 40);


--
-- TOC entry 4966 (class 0 OID 33699)
-- Dependencies: 222
-- Data for Name: request; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.request VALUES ('347d0d3d-7f15-4eec-96ee-f25d0e861f99', '2025-08-19 13:21:27.16205', '2025-08-21', '', '2025-08-20', 'ACCEPTED', 'dddd4444-eeee-4fff-baaa-888888888888');
INSERT INTO public.request VALUES ('6fd4b612-f581-44ca-92da-97e5a7c7bda1', '2025-08-19 16:59:24.787878', '2025-09-04', '', '2025-08-27', 'PENDING', 'dddd4444-eeee-4fff-baaa-888888888888');


--
-- TOC entry 4964 (class 0 OID 25665)
-- Dependencies: 220
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.roles VALUES ('aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'MANAGER');
INSERT INTO public.roles VALUES ('aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('aaaa1111-bbbb-4ccc-ddee-eeee00000002', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbb2222-cccc-4ddd-eeee-aaaa00000003', 'MANAGER');
INSERT INTO public.roles VALUES ('bbbb2222-cccc-4ddd-eeee-aaaa00000003', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbb2222-cccc-4ddd-eeee-aaaa00000004', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('cccc3333-dddd-4eee-aaaa-bbbb00000005', 'ADMIN');
INSERT INTO public.roles VALUES ('cccc3333-dddd-4eee-aaaa-bbbb00000005', 'MANAGER');
INSERT INTO public.roles VALUES ('cccc3333-dddd-4eee-aaaa-bbbb00000005', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('cccc3333-dddd-4eee-aaaa-bbbb00000006', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('dddd4444-eeee-4fff-baaa-777777777777', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('dddd4444-eeee-4fff-baaa-888888888888', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('eeee6666-ffff-4aaa-bbbb-dddd00000008', 'MANAGER');
INSERT INTO public.roles VALUES ('eeee6666-ffff-4aaa-bbbb-dddd00000008', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('10000000-0000-0000-0000-000000000001', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('10000000-0000-0000-0000-000000000002', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb03', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb04', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb05', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb06', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb07', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb08', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('35053f6d-f4ed-46a5-aed1-bb1b6fb8c36b', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('339ca6f0-acf4-4b48-8fd1-cd489feac9c2', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('ba1e6a30-33c5-460a-91e4-05a0ec13008f', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('b4556373-48dc-4422-8acc-683acbb1a359', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('98aa3d58-2004-43b6-8d5b-d6ced213c753', 'MANAGER');
INSERT INTO public.roles VALUES ('98aa3d58-2004-43b6-8d5b-d6ced213c753', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('308ffe1b-9918-401f-8d2a-609038d49d62', 'MANAGER');
INSERT INTO public.roles VALUES ('308ffe1b-9918-401f-8d2a-609038d49d62', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('e4343e30-67fb-47f7-b8b3-b5d4b8a224ab', 'MANAGER');
INSERT INTO public.roles VALUES ('e4343e30-67fb-47f7-b8b3-b5d4b8a224ab', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('fbefb2f2-3d70-4a3f-b32e-b6fb8bf30287', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('fbefb2f2-3d70-4a3f-b32e-b6fb8bf30287', 'MANAGER');
INSERT INTO public.roles VALUES ('4117ea49-0d96-41db-8760-3af598bb64de', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('97da5b0b-208a-47b0-bed4-9f71187919fd', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('97da5b0b-208a-47b0-bed4-9f71187919fd', 'MANAGER');
INSERT INTO public.roles VALUES ('b18c79a5-2807-4a5e-9faf-6838540c82e4', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('3c17d5db-6010-4870-b85a-be76bc7d2ae0', 'MANAGER');
INSERT INTO public.roles VALUES ('3c17d5db-6010-4870-b85a-be76bc7d2ae0', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('98ff1a6f-6fa7-467a-a52d-a719572e0090', 'MANAGER');
INSERT INTO public.roles VALUES ('98ff1a6f-6fa7-467a-a52d-a719572e0090', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('c7d0c0d4-88e4-4dc0-b7ed-5cdd3f05ba0c', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('67deff89-e5aa-4a57-a749-5f5a7a97e548', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('d9d6c8a5-7aa3-440a-99f4-f8e79f9e23f2', 'MANAGER');
INSERT INTO public.roles VALUES ('d9d6c8a5-7aa3-440a-99f4-f8e79f9e23f2', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('6ccb3318-7478-4622-b918-45bc6bc886d4', 'MANAGER');
INSERT INTO public.roles VALUES ('6ccb3318-7478-4622-b918-45bc6bc886d4', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('308ffe1b-9918-401f-8d2a-609038d49d62', 'ADMIN');
INSERT INTO public.roles VALUES ('02c7fb1d-f185-4523-aab4-21ddd25927a8', 'MANAGER');
INSERT INTO public.roles VALUES ('02c7fb1d-f185-4523-aab4-21ddd25927a8', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('4cca86db-9166-4ea4-a56a-400e353204a0', 'EMPLOYEE');
INSERT INTO public.roles VALUES ('4cca86db-9166-4ea4-a56a-400e353204a0', 'MANAGER');


--
-- TOC entry 4965 (class 0 OID 33639)
-- Dependencies: 221
-- Data for Name: time_stamp; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.time_stamp VALUES ('a2d301ca-f6a1-44f2-91bb-83be08fd296e', '2025-08-12', NULL, NULL, 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0);
INSERT INTO public.time_stamp VALUES ('5bd90a8c-5f98-4772-9804-5aa022ef5ecb', '2025-08-12', NULL, NULL, 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0);
INSERT INTO public.time_stamp VALUES ('5f5ab762-214b-4cb9-9343-1410d2eae2be', '2025-08-12', NULL, NULL, 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0);
INSERT INTO public.time_stamp VALUES ('5e716431-c04d-43cb-875d-b89df61d9fc2', '2025-08-13', '17:50:00.891', '17:31:48.604', 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0.3);
INSERT INTO public.time_stamp VALUES ('33db114a-9e8a-4bef-aba9-e7c83bd73753', '2025-08-18', NULL, NULL, 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0);
INSERT INTO public.time_stamp VALUES ('700db913-4cbf-4baf-8814-4514a011cd79', '2025-08-19', '13:02:27.815', '13:01:33.639', 'dddd4444-eeee-4fff-baaa-888888888888', 0);
INSERT INTO public.time_stamp VALUES ('dc7c9126-3ab4-44e0-8ded-57fbb36546d8', '2025-08-19', '13:14:49.667', '13:10:25.455', 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0.07);
INSERT INTO public.time_stamp VALUES ('d80dd38e-cac4-4f89-8f75-5c2241a06264', '2025-08-19', NULL, NULL, 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 0);
INSERT INTO public.time_stamp VALUES ('43f63eed-d170-4854-8d73-ae479e712bfc', '2025-08-11', '15:43:09.819', '15:43:02.575', 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0);


--
-- TOC entry 4801 (class 2606 OID 25652)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);


--
-- TOC entry 4803 (class 2606 OID 25662)
-- Name: person person_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_email_key UNIQUE (email);


--
-- TOC entry 4805 (class 2606 OID 25660)
-- Name: person person_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (person_id);


--
-- TOC entry 4807 (class 2606 OID 33737)
-- Name: person person_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_username_key UNIQUE (username);


--
-- TOC entry 4811 (class 2606 OID 33706)
-- Name: request request_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT request_pkey PRIMARY KEY (request_id);


--
-- TOC entry 4809 (class 2606 OID 33643)
-- Name: time_stamp time_stamp_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.time_stamp
    ADD CONSTRAINT time_stamp_pkey PRIMARY KEY (time_stamp_id);


--
-- TOC entry 4816 (class 2606 OID 33707)
-- Name: request fk8d8s5s9ctrikgw0le81j6p9r5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT fk8d8s5s9ctrikgw0le81j6p9r5 FOREIGN KEY (person_id) REFERENCES public.person(person_id);


--
-- TOC entry 4815 (class 2606 OID 33644)
-- Name: time_stamp fka0rgvyk5fsup6a2m9r6fvtq7f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.time_stamp
    ADD CONSTRAINT fka0rgvyk5fsup6a2m9r6fvtq7f FOREIGN KEY (person_id) REFERENCES public.person(person_id);


--
-- TOC entry 4814 (class 2606 OID 25679)
-- Name: roles fkbquq8tc0jejlrlfe3jf6gsfg0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT fkbquq8tc0jejlrlfe3jf6gsfg0 FOREIGN KEY (person_id) REFERENCES public.person(person_id);


--
-- TOC entry 4812 (class 2606 OID 25674)
-- Name: person fkh9os7soraiy7xdcepwuob0f6b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fkh9os7soraiy7xdcepwuob0f6b FOREIGN KEY (superior_id) REFERENCES public.person(person_id);


--
-- TOC entry 4813 (class 2606 OID 25669)
-- Name: person fkk7rgn6djxsv2j2bv1mvuxd4m9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fkk7rgn6djxsv2j2bv1mvuxd4m9 FOREIGN KEY (address_id) REFERENCES public.address(address_id);


-- Completed on 2025-08-20 12:52:27

--
-- PostgreSQL database dump complete
--

