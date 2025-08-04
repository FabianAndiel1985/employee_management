--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-08-04 13:07:23

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
-- TOC entry 4967 (class 1262 OID 25286)
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
    address_id uuid,
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
-- TOC entry 4957 (class 0 OID 25646)
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


--
-- TOC entry 4958 (class 0 OID 25653)
-- Dependencies: 219
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.person VALUES (888888888, '44444444-dead-4fff-baaa-555555555555', 'dddd4444-eeee-4fff-baaa-888888888888', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'helen@example.com', 'Helen', 'Green', 'helenpass', 'ABSENT', 'helen', 25, 23, 40);
INSERT INTO public.person VALUES (123345, '44444444-dead-4fff-baaa-555555555555', '339ca6f0-acf4-4b48-8fd1-cd489feac9c2', '98ff1a6f-6fa7-467a-a52d-a719572e0090', 'email@email.com', 'fab', 'fab', 'fab', 'JUST_CREATED', 'fabian', 25, 25, 40);
INSERT INTO public.person VALUES (1234556, '00000000-0000-0000-0000-000000000001', 'ba1e6a30-33c5-460a-91e4-05a0ec13008f', NULL, 'mensch@mensch.de', 'feffwf', 'efqffqwfq2', 'mensch', 'JUST_CREATED', 'mensch', 25, 25, 40);
INSERT INTO public.person VALUES (12345, '44444444-dead-4fff-baaa-555555555555', 'b4556373-48dc-4422-8acc-683acbb1a359', NULL, 'a@a.com', 'fnewufgh', 'fewfwefwf', 'fefe', 'JUST_CREATED', 'aed3e', 25, 25, 40);
INSERT INTO public.person VALUES (1484851, '44444444-dead-4fff-baaa-555555555555', '97da5b0b-208a-47b0-bed4-9f71187919fd', NULL, 'Manager@Manager.com', 'Manager', 'Manager', 'Manager', 'JUST_CREATED', 'Manager', 25, 25, 40);
INSERT INTO public.person VALUES (1852128, '22222222-aaaa-4bbb-bccc-333333333333', '308ffe1b-9918-401f-8d2a-609038d49d62', NULL, 'fabiuan@fabian', 'Fabian', 'Fabian', 'fabian23', 'JUST_CREATED', 'fabian23', 25, 25, 40);
INSERT INTO public.person VALUES (13424242, '11111111-1111-1111-1111-111111111111', 'e4343e30-67fb-47f7-b8b3-b5d4b8a224ab', NULL, 'tom@tom.de', 'Tom', 'tom', 'tom', 'JUST_CREATED', 'tom', 25, 25, 40);
INSERT INTO public.person VALUES (516196, '54f12028-316d-41d5-961f-6fa9b6cb4387', '3c17d5db-6010-4870-b85a-be76bc7d2ae0', NULL, 'DerMeister@DerMeister.com', 'DerMeister', 'DerMeister', '151515fe', 'JUST_CREATED', 'feffefe', 25, 25, 40);
INSERT INTO public.person VALUES (123456, '44444444-dead-4fff-baaa-555555555555', '35053f6d-f4ed-46a5-aed1-bb1b6fb8c36b', '3c17d5db-6010-4870-b85a-be76bc7d2ae0', 'fab@fab.com', 'Maxl', 'Meister', 'fab', 'JUST_CREATED', 'fab', 25, 25, 40);
INSERT INTO public.person VALUES (123445, '33333333-abcd-4a34-bef0-444444444444', 'fbefb2f2-3d70-4a3f-b32e-b6fb8bf30287', NULL, 'super@super.com', 'Super', 'Super', 'super', 'JUST_CREATED', 'super', 25, 25, 40);
INSERT INTO public.person VALUES (418484, '22222222-aaaa-4bbb-bccc-333333333333', 'b18c79a5-2807-4a5e-9faf-6838540c82e4', NULL, 'evas@employee.com', 'Evas', 'Employee', 'Employee', 'JUST_CREATED', 'Evas', 25, 25, 40);
INSERT INTO public.person VALUES (123456789, '11111111-1111-1111-1111-111111111111', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', NULL, 'alice@example.com', 'Alicia', 'Anderson', 'hashed_password_1', 'JUST_CREATED', 'alice', 25, 25, 40);
INSERT INTO public.person VALUES (151515, '33333333-abcd-4a34-bef0-444444444444', 'c7d0c0d4-88e4-4dc0-b7ed-5cdd3f05ba0c', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'max@supermann.de', 'Max', 'SuperMann', 'maxpass', 'JUST_CREATED', 'maxsupermann', 25, 0, 0);
INSERT INTO public.person VALUES (333444555, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb05', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'lisa.maier2@example.com', 'Lisa', 'Maier', 'passlisa', 'PRESENT', 'lisa.maier2', 25, 25, 40);
INSERT INTO public.person VALUES (111222333, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb03', '98ff1a6f-6fa7-467a-a52d-a719572e0090', 'maria.novak2@example.com', 'Maria', 'Novak', 'passmaria', 'PRESENT', 'maria.novak2', 25, 25, 40);
INSERT INTO public.person VALUES (222333444, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb04', '98ff1a6f-6fa7-467a-a52d-a719572e0090', 'david.schmidt2@example.com', 'David', 'Schmidt', 'passdavid', 'PRESENT', 'david.schmidt2', 25, 25, 40);
INSERT INTO public.person VALUES (123789456, '99999999-aaaa-bbbb-cccc-eeeeeeeeeeee', 'eeee6666-ffff-4aaa-bbbb-dddd00000008', NULL, 'lisa@example.com', 'Lisa', 'Lerner', 'lisapass', 'PRESENT', 'lisa', 25, 25, 40);
INSERT INTO public.person VALUES (444555666, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb06', NULL, 'james.bauer2@example.com', 'James', 'Bauer', 'passjames', 'PRESENT', 'james.bauer2', 25, 25, 40);
INSERT INTO public.person VALUES (555666777, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb07', NULL, 'sophie.keller2@example.com', 'Sophie', 'Keller', 'passsophie', 'PRESENT', 'sophie.keller2', 25, 25, 40);
INSERT INTO public.person VALUES (987654321, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02', '308ffe1b-9918-401f-8d2a-609038d49d62', 'peter.solo2@example.com', 'Peter', 'Solo', 'testpass456', 'PRESENT', 'peter.solo2', 25, 25, 40);
INSERT INTO public.person VALUES (123456789, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01', '308ffe1b-9918-401f-8d2a-609038d49d62', 'anna.employee2@example.com', 'Anna', 'Employee', 'securepass123', 'PRESENT', 'anna.employee2', 25, 25, 40);
INSERT INTO public.person VALUES (666777888, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb08', '3c17d5db-6010-4870-b85a-be76bc7d2ae0', 'lukas.fischer2@example.com', 'Lukas', 'Fischer', 'passlukas', 'PRESENT', 'lukas.fischer2', 25, 25, 40);
INSERT INTO public.person VALUES (555123123, '33333333-abcd-4a34-bef0-444444444444', 'cccc3333-dddd-4eee-aaaa-bbbb00000005', NULL, 'eva@example.com', 'Eva', 'Evans', 'evapass', 'ABSENT', 'eva', 25, 25, 40);
INSERT INTO public.person VALUES (777777777, '44444444-dead-4fff-baaa-555555555555', 'dddd4444-eeee-4fff-baaa-777777777777', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'george@example.com', 'Georogos', 'Green', 'georgepass', 'JUST_CREATED', 'george', 25, 25, 40);
INSERT INTO public.person VALUES (181818, '99999999-aaaa-bbbb-cccc-eeeeeeeeeeee', '98ff1a6f-6fa7-467a-a52d-a719572e0090', NULL, 'max@max.com', 'dfeqwdfq', 'dwdfqwd', 'rolrol', 'JUST_CREATED', 'derer@der.com', 30, 30, 40);
INSERT INTO public.person VALUES (1258484, '33333333-abcd-4a34-bef0-444444444444', '67deff89-e5aa-4a57-a749-5f5a7a97e548', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'neuer1@neuer1.com', 'Neuer1', 'Neuer1', 'neuer1', 'JUST_CREATED', 'neuer1', 25, 25, 40);
INSERT INTO public.person VALUES (666321321, '33333333-abcd-4a34-bef0-444444444444', 'cccc3333-dddd-4eee-aaaa-bbbb00000006', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001', 'frank@example.com', 'Frank', 'Foster', 'frankpass', 'VACATION', 'frank', 25, 25, 40);
INSERT INTO public.person VALUES (987654321, '11111111-1111-1111-1111-111111111111', 'aaaa1111-bbbb-4ccc-ddee-eeee00000002', NULL, 'bob@example.com', 'Bob', 'Brown', 'hashed_password_2', 'INACTIVE', 'bob', 25, 25, 40);
INSERT INTO public.person VALUES (25345346, '22222222-aaaa-4bbb-bccc-333333333333', '98aa3d58-2004-43b6-8d5b-d6ced213c753', NULL, 'test8@test8.com', 'test8', 'test8', 'test8', 'INACTIVE', 'test8', 25, 25, 40);
INSERT INTO public.person VALUES (123456789, '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', NULL, 'anna.employee@example.com', 'Anna', 'Employee', 'securepass123', 'PRESENT', 'anna.employee', 25, 25, 40);
INSERT INTO public.person VALUES (987654321, '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', NULL, 'peter.employee@example.com', 'Peter', 'Solo', 'testpass456', 'PRESENT', 'peter.solo', 25, 25, 40);
INSERT INTO public.person VALUES (4588184, '44444444-dead-4fff-baaa-555555555555', '4117ea49-0d96-41db-8760-3af598bb64de', NULL, 'Raschad@Raschad.com', 'Raschad', 'Raschad', 'Raschad', 'INACTIVE', 'Raschad', 25, 25, 40);
INSERT INTO public.person VALUES (123123123, '22222222-aaaa-4bbb-bccc-333333333333', 'bbbb2222-cccc-4ddd-eeee-aaaa00000003', NULL, 'charlie@example.com', 'Charlie', 'Clark', 'charliepass', 'INACTIVE', 'charlie', 25, 25, 40);
INSERT INTO public.person VALUES (321321321, '22222222-aaaa-4bbb-bccc-333333333333', 'bbbb2222-cccc-4ddd-eeee-aaaa00000004', NULL, 'diana@example.com', 'Diana', 'Davis', 'dianapass', 'ABSENT', 'diana', 25, 25, 40);


--
-- TOC entry 4961 (class 0 OID 33699)
-- Dependencies: 222
-- Data for Name: request; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.request VALUES ('b0bcef39-e136-4fac-b9d0-b52f3e9add46', '2025-07-17 16:13:55.775119', '2025-07-18', '', '2025-07-10', 'EXPIRED', 'bbbb2222-cccc-4ddd-eeee-aaaa00000004');
INSERT INTO public.request VALUES ('16610da2-978f-4ba9-b98a-d8002a6412f7', '2025-07-19 12:53:51.220229', '2025-09-28', '', '2025-09-22', 'DENIED', 'bbbb2222-cccc-4ddd-eeee-aaaa00000004');
INSERT INTO public.request VALUES ('28190261-24cd-403f-8184-ac6f11c4a42c', '2025-07-19 12:53:41.266496', '2025-09-30', '', '2025-08-19', 'DENIED', 'bbbb2222-cccc-4ddd-eeee-aaaa00000004');
INSERT INTO public.request VALUES ('2f01cb51-f7de-4d5f-9479-6e3b957aa8bf', '2025-07-19 13:13:10.531925', '2025-10-14', '', '2025-10-01', 'PENDING', 'bbbb2222-cccc-4ddd-eeee-aaaa00000004');
INSERT INTO public.request VALUES ('3b0fbbb8-3829-497b-b95b-29e291a0ceee', '2025-07-19 14:25:53.731445', '2025-10-29', '', '2025-09-24', 'PENDING', 'bbbb2222-cccc-4ddd-eeee-aaaa00000004');
INSERT INTO public.request VALUES ('ff0efa0a-b9b1-416e-882a-bb6dcdfdbef6', '2025-07-26 16:27:19.134386', '2025-08-27', '', '2025-08-18', 'DENIED', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb05');
INSERT INTO public.request VALUES ('456b2f7a-c9c6-41de-98f4-d124ff7065b8', '2025-07-28 16:32:35.980617', '2025-07-30', '', '2025-07-29', 'ACCEPTED', 'aaaa1111-bbbb-4ccc-ddee-eeee00000001');


--
-- TOC entry 4959 (class 0 OID 25665)
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


--
-- TOC entry 4960 (class 0 OID 33639)
-- Dependencies: 221
-- Data for Name: time_stamp; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.time_stamp VALUES ('bdc5723b-8d7b-436d-ab0e-108ea6498eb9', '2025-07-01', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('a0ec3c2a-7dec-44b7-b9ac-96ef6a9c9e81', '2025-07-02', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('1790df3a-0e70-41b7-99af-522ee3c68827', '2025-07-03', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('b54d43a3-ca6c-432f-b85d-f3aea74f55d1', '2025-07-04', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('4af8aa57-eb61-4441-9efc-502192bc35d1', '2025-07-07', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('a5cb0c0c-168b-42de-a0a1-e2e7989b1960', '2025-07-08', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('af86a23a-30e1-4fa8-bcf6-f20ceb7aa772', '2025-07-09', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('bff055ee-e8e8-4ff9-9b56-5cdd7be63d0d', '2025-07-10', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('e91fdb5d-884d-4564-9e9c-66b6b5e98e69', '2025-07-11', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('8a6cd6d6-b232-4f8c-810f-e077fe9f6e83', '2025-07-14', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('542c1dfa-aea5-4734-bcdf-ae8cdf3194b4', '2025-07-15', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('ac337572-4b4d-42b2-8c23-514778c2bbad', '2025-07-16', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('2301a8b1-637d-4cd9-ba52-39a59c0a819a', '2025-07-17', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('895c9ca0-7cce-4052-971a-a8efcfb260a4', '2025-07-18', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('fc74e842-34e1-44f3-8498-5dc5968ce848', '2025-07-21', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('9208e04c-6b83-4c33-8eb1-3c34d62d7fb9', '2025-07-22', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('68c42ff8-ea36-4299-9ee9-bf50f5a0ba80', '2025-07-23', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('8b167752-cc9a-4b55-85a9-9cb57041940b', '2025-07-24', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('eacf1a67-11c1-4dc3-8241-e032738dc7df', '2025-07-25', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('3758956a-7cb4-40dd-ae93-37e69e3c64d1', '2025-07-28', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('fa6d937f-45f0-4634-b63b-23c890d63df0', '2025-07-29', '17:00:00', '09:00:00', 'dddd4444-eeee-4fff-baaa-888888888888', 8);
INSERT INTO public.time_stamp VALUES ('db124cf9-6067-43a0-9f03-0850aadeea3a', '2025-07-30', '17:06:38.799', '17:06:35.244', 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0);
INSERT INTO public.time_stamp VALUES ('4f76e7cf-626f-4a8e-84c3-e425248514aa', '2025-07-30', '17:08:51.935', '16:56:33.119', 'dddd4444-eeee-4fff-baaa-888888888888', 0.2);
INSERT INTO public.time_stamp VALUES ('69cb1fc0-0a17-4438-a276-6c2d3cb35bdb', '2025-07-31', '09:08:40.568', '09:05:09.604', 'dddd4444-eeee-4fff-baaa-888888888888', 0.05);
INSERT INTO public.time_stamp VALUES ('3354f679-4652-4258-a0fc-8c846bc960b6', '2025-07-31', NULL, NULL, 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0);
INSERT INTO public.time_stamp VALUES ('18810795-28d3-4028-b146-ffff2c044a58', '2025-08-04', NULL, NULL, 'cccc3333-dddd-4eee-aaaa-bbbb00000005', 0);


--
-- TOC entry 4796 (class 2606 OID 25652)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);


--
-- TOC entry 4798 (class 2606 OID 25662)
-- Name: person person_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_email_key UNIQUE (email);


--
-- TOC entry 4800 (class 2606 OID 25660)
-- Name: person person_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (person_id);


--
-- TOC entry 4802 (class 2606 OID 33737)
-- Name: person person_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_username_key UNIQUE (username);


--
-- TOC entry 4806 (class 2606 OID 33706)
-- Name: request request_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT request_pkey PRIMARY KEY (request_id);


--
-- TOC entry 4804 (class 2606 OID 33643)
-- Name: time_stamp time_stamp_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.time_stamp
    ADD CONSTRAINT time_stamp_pkey PRIMARY KEY (time_stamp_id);


--
-- TOC entry 4811 (class 2606 OID 33707)
-- Name: request fk8d8s5s9ctrikgw0le81j6p9r5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT fk8d8s5s9ctrikgw0le81j6p9r5 FOREIGN KEY (person_id) REFERENCES public.person(person_id);


--
-- TOC entry 4810 (class 2606 OID 33644)
-- Name: time_stamp fka0rgvyk5fsup6a2m9r6fvtq7f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.time_stamp
    ADD CONSTRAINT fka0rgvyk5fsup6a2m9r6fvtq7f FOREIGN KEY (person_id) REFERENCES public.person(person_id);


--
-- TOC entry 4809 (class 2606 OID 25679)
-- Name: roles fkbquq8tc0jejlrlfe3jf6gsfg0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT fkbquq8tc0jejlrlfe3jf6gsfg0 FOREIGN KEY (person_id) REFERENCES public.person(person_id);


--
-- TOC entry 4807 (class 2606 OID 25674)
-- Name: person fkh9os7soraiy7xdcepwuob0f6b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fkh9os7soraiy7xdcepwuob0f6b FOREIGN KEY (superior_id) REFERENCES public.person(person_id);


--
-- TOC entry 4808 (class 2606 OID 25669)
-- Name: person fkk7rgn6djxsv2j2bv1mvuxd4m9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fkk7rgn6djxsv2j2bv1mvuxd4m9 FOREIGN KEY (address_id) REFERENCES public.address(address_id);


-- Completed on 2025-08-04 13:07:23

--
-- PostgreSQL database dump complete
--

