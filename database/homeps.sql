--
-- PostgreSQL database dump
--

\restrict iy6d2ZgsMGKxSIbL8q85HeDfp71RaEpZZI4QafbhWJxaQLMfBskVVWYqezpEd2l

-- Dumped from database version 15.17 (Debian 15.17-1.pgdg13+1)
-- Dumped by pg_dump version 15.17 (Debian 15.17-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- Name: chitiet_hoadon; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chitiet_hoadon (
    id integer NOT NULL,
    hoadonid integer NOT NULL,
    dichvuid integer NOT NULL,
    tendichvu character varying(120) NOT NULL,
    soluong integer DEFAULT 1 NOT NULL,
    dongia numeric(12,2) NOT NULL,
    thanhtien numeric(12,2) NOT NULL
);


ALTER TABLE public.chitiet_hoadon OWNER TO postgres;

--
-- Name: chitiet_hoadon_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.chitiet_hoadon_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.chitiet_hoadon_id_seq OWNER TO postgres;

--
-- Name: chitiet_hoadon_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.chitiet_hoadon_id_seq OWNED BY public.chitiet_hoadon.id;


--
-- Name: dichvu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dichvu (
    id integer NOT NULL,
    tendichvu character varying(120) NOT NULL,
    dongia numeric(12,2) NOT NULL,
    loai character varying(20) DEFAULT 'KHAC'::character varying NOT NULL,
    CONSTRAINT dichvu_dongia_check CHECK ((dongia >= (0)::numeric))
);


ALTER TABLE public.dichvu OWNER TO postgres;

--
-- Name: dichvu_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.dichvu_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dichvu_id_seq OWNER TO postgres;

--
-- Name: dichvu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.dichvu_id_seq OWNED BY public.dichvu.id;


--
-- Name: hoadon; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.hoadon (
    id integer NOT NULL,
    luotchoiid integer NOT NULL,
    ngaytao timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    tienchoi numeric(12,2) DEFAULT 0 NOT NULL,
    tiendichvu numeric(12,2) DEFAULT 0 NOT NULL,
    tienkhuyenmai numeric(12,2) DEFAULT 0 NOT NULL,
    tongtien numeric(12,2) DEFAULT 0 NOT NULL,
    trangthai character varying(30) DEFAULT 'CHUA_THANH_TOAN'::character varying NOT NULL
);


ALTER TABLE public.hoadon OWNER TO postgres;

--
-- Name: hoadon_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hoadon_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hoadon_id_seq OWNER TO postgres;

--
-- Name: hoadon_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.hoadon_id_seq OWNED BY public.hoadon.id;


--
-- Name: luotchoi; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.luotchoi (
    id integer NOT NULL,
    mayid integer NOT NULL,
    nhanvienid integer NOT NULL,
    thoigianbatdau timestamp without time zone NOT NULL,
    thoigianketthuc timestamp without time zone,
    dongiagio numeric(12,2) NOT NULL,
    tongtiengio numeric(12,2) DEFAULT 0 NOT NULL,
    trangthai character varying(20) NOT NULL
);


ALTER TABLE public.luotchoi OWNER TO postgres;

--
-- Name: luotchoi_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.luotchoi_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.luotchoi_id_seq OWNER TO postgres;

--
-- Name: luotchoi_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.luotchoi_id_seq OWNED BY public.luotchoi.id;


--
-- Name: mayps; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mayps (
    id integer NOT NULL,
    tenmay character varying(100) NOT NULL,
    tinhtrang character varying(30) DEFAULT 'BINH_THUONG'::character varying NOT NULL,
    ghichu character varying(255)
);


ALTER TABLE public.mayps OWNER TO postgres;

--
-- Name: mayps_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.mayps_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mayps_id_seq OWNER TO postgres;

--
-- Name: mayps_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.mayps_id_seq OWNED BY public.mayps.id;


--
-- Name: nhanvien; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nhanvien (
    id integer NOT NULL,
    tennhanvien character varying(120) NOT NULL,
    sodienthoai character varying(20),
    chucvu character varying(30) NOT NULL,
    trangthai character varying(20) DEFAULT 'DANG_LAM'::character varying NOT NULL
);


ALTER TABLE public.nhanvien OWNER TO postgres;

--
-- Name: nhanvien_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nhanvien_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.nhanvien_id_seq OWNER TO postgres;

--
-- Name: nhanvien_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.nhanvien_id_seq OWNED BY public.nhanvien.id;


--
-- Name: sukien; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sukien (
    id integer NOT NULL,
    tensukien character varying(200) NOT NULL,
    mota text,
    phantramgiamgia integer DEFAULT 0 NOT NULL,
    loaisukien character varying(30) NOT NULL,
    gioapdung character varying(20),
    ngayapdung character varying(20),
    ngaybatdau timestamp without time zone NOT NULL,
    ngayketthuc timestamp without time zone NOT NULL,
    trangthai boolean DEFAULT true,
    CONSTRAINT sukien_phantramgiamgia_check CHECK (((phantramgiamgia >= 0) AND (phantramgiamgia <= 100)))
);


ALTER TABLE public.sukien OWNER TO postgres;

--
-- Name: sukien_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sukien_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sukien_id_seq OWNER TO postgres;

--
-- Name: sukien_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sukien_id_seq OWNED BY public.sukien.id;


--
-- Name: chitiet_hoadon id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chitiet_hoadon ALTER COLUMN id SET DEFAULT nextval('public.chitiet_hoadon_id_seq'::regclass);


--
-- Name: dichvu id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dichvu ALTER COLUMN id SET DEFAULT nextval('public.dichvu_id_seq'::regclass);


--
-- Name: hoadon id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.hoadon ALTER COLUMN id SET DEFAULT nextval('public.hoadon_id_seq'::regclass);


--
-- Name: luotchoi id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.luotchoi ALTER COLUMN id SET DEFAULT nextval('public.luotchoi_id_seq'::regclass);


--
-- Name: mayps id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mayps ALTER COLUMN id SET DEFAULT nextval('public.mayps_id_seq'::regclass);


--
-- Name: nhanvien id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nhanvien ALTER COLUMN id SET DEFAULT nextval('public.nhanvien_id_seq'::regclass);


--
-- Name: sukien id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sukien ALTER COLUMN id SET DEFAULT nextval('public.sukien_id_seq'::regclass);


--
-- Data for Name: chitiet_hoadon; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.chitiet_hoadon (id, hoadonid, dichvuid, tendichvu, soluong, dongia, thanhtien) FROM stdin;
1	1	3	Tra da	1	5000.00	5000.00
2	2	1	My tom	2	15000.00	30000.00
3	3	1	My tom	1	15000.00	15000.00
4	3	1	My tom	3	15000.00	45000.00
5	5	1	My tom	2	15000.00	30000.00
6	6	3	Tra da	1	5000.00	5000.00
7	10	1	My tom	1	15000.00	15000.00
\.


--
-- Data for Name: dichvu; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dichvu (id, tendichvu, dongia, loai) FROM stdin;
1	My tom	15000.00	DO_AN
2	Coca	12000.00	NUOC
3	Tra da	5000.00	NUOC
\.


--
-- Data for Name: hoadon; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.hoadon (id, luotchoiid, ngaytao, tienchoi, tiendichvu, tienkhuyenmai, tongtien, trangthai) FROM stdin;
1	1	2026-06-13 07:32:01.885618	1500.00	5000.00	0.00	6500.00	CHUA_THANH_TOAN
3	2	2026-06-13 07:38:52.255935	2500.00	60000.00	0.00	62500.00	CHUA_THANH_TOAN
4	6	2026-06-13 10:09:48.747827	55500.00	0.00	0.00	55500.00	DA_THANH_TOAN
5	7	2026-06-13 10:10:06.127884	9000.00	30000.00	0.00	39000.00	DA_THANH_TOAN
7	5	2026-06-13 13:26:37.134869	154000.00	0.00	0.00	154000.00	DA_THANH_TOAN
2	3	2026-06-13 07:34:02.115347	176000.00	30000.00	0.00	206000.00	DA_THANH_TOAN
8	4	2026-06-13 13:26:49.128958	164500.00	0.00	0.00	164500.00	DA_THANH_TOAN
9	9	2026-06-13 13:26:54.45206	500.00	0.00	0.00	500.00	DA_THANH_TOAN
6	8	2026-06-13 10:34:34.305401	86000.00	5000.00	0.00	91000.00	DA_THANH_TOAN
10	10	2026-06-13 13:51:05.270005	0.00	15000.00	0.00	0.00	CHUA_THANH_TOAN
\.


--
-- Data for Name: luotchoi; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.luotchoi (id, mayid, nhanvienid, thoigianbatdau, thoigianketthuc, dongiagio, tongtiengio, trangthai) FROM stdin;
1	1	1	2026-06-13 07:28:22.313561	2026-06-13 07:32:11.700215	30000.00	1500.00	DA_KET_THUC
2	1	1	2026-06-13 07:33:47.926657	2026-06-13 07:39:06.864541	30000.00	2500.00	DA_KET_THUC
6	2	1	2026-06-13 08:18:21.497971	2026-06-13 10:09:48.747827	30000.00	55500.00	DA_KET_THUC
7	1	1	2026-06-13 10:09:59.671705	2026-06-13 10:28:43.025243	30000.00	9000.00	DA_KET_THUC
5	3	1	2026-06-13 08:17:58.43679	2026-06-13 13:26:37.134869	30000.00	154000.00	DA_KET_THUC
3	4	1	2026-06-13 07:33:53.945709	2026-06-13 13:26:44.010942	30000.00	176000.00	DA_KET_THUC
4	8	1	2026-06-13 07:57:28.110572	2026-06-13 13:26:49.128958	30000.00	164500.00	DA_KET_THUC
9	2	1	2026-06-13 13:26:31.577538	2026-06-13 13:26:54.45206	30000.00	500.00	DA_KET_THUC
8	1	1	2026-06-13 10:34:28.100695	2026-06-13 13:26:59.832144	30000.00	86000.00	DA_KET_THUC
10	2	1	2026-06-13 13:50:59.807355	\N	30000.00	0.00	DANG_CHOI
\.


--
-- Data for Name: mayps; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mayps (id, tenmay, tinhtrang, ghichu) FROM stdin;
5	May 5	BINH_THUONG	\N
6	May 6	BINH_THUONG	\N
7	May 7	BINH_THUONG	\N
9	May 9	BINH_THUONG	\N
10	May 10	BINH_THUONG	\N
11	May 11	BINH_THUONG	\N
12	May 12	BINH_THUONG	\N
13	May 13	BINH_THUONG	\N
14	May 14	BINH_THUONG	\N
15	May 15	BINH_THUONG	\N
16	May 16	BINH_THUONG	\N
17	May 17	BINH_THUONG	\N
18	May 18	BINH_THUONG	\N
19	May 19	BINH_THUONG	\N
20	May 20	BINH_THUONG	\N
3	May 3	BINH_THUONG	\N
4	May 4	BINH_THUONG	\N
8	May 8	BINH_THUONG	\N
1	May 1	BINH_THUONG	\N
23	May 30	BINH_THUONG	\N
2	May 2	DANG_CHOI	\N
\.


--
-- Data for Name: nhanvien; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nhanvien (id, tennhanvien, sodienthoai, chucvu, trangthai) FROM stdin;
2	Nhan vien 1	0000000001	NHAN_VIEN	DANG_LAM
1	Admin-Nguyen	0000000000	ADMIN	DANG_LAM
\.


--
-- Data for Name: sukien; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sukien (id, tensukien, mota, phantramgiamgia, loaisukien, gioapdung, ngayapdung, ngaybatdau, ngayketthuc, trangthai) FROM stdin;
\.


--
-- Name: chitiet_hoadon_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.chitiet_hoadon_id_seq', 7, true);


--
-- Name: dichvu_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.dichvu_id_seq', 4, true);


--
-- Name: hoadon_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hoadon_id_seq', 10, true);


--
-- Name: luotchoi_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.luotchoi_id_seq', 10, true);


--
-- Name: mayps_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mayps_id_seq', 23, true);


--
-- Name: nhanvien_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.nhanvien_id_seq', 3, true);


--
-- Name: sukien_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sukien_id_seq', 1, true);


--
-- Name: chitiet_hoadon chitiet_hoadon_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chitiet_hoadon
    ADD CONSTRAINT chitiet_hoadon_pkey PRIMARY KEY (id);


--
-- Name: dichvu dichvu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dichvu
    ADD CONSTRAINT dichvu_pkey PRIMARY KEY (id);


--
-- Name: hoadon hoadon_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.hoadon
    ADD CONSTRAINT hoadon_pkey PRIMARY KEY (id);


--
-- Name: luotchoi luotchoi_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.luotchoi
    ADD CONSTRAINT luotchoi_pkey PRIMARY KEY (id);


--
-- Name: mayps mayps_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mayps
    ADD CONSTRAINT mayps_pkey PRIMARY KEY (id);


--
-- Name: nhanvien nhanvien_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nhanvien
    ADD CONSTRAINT nhanvien_pkey PRIMARY KEY (id);


--
-- Name: sukien sukien_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sukien
    ADD CONSTRAINT sukien_pkey PRIMARY KEY (id);


--
-- Name: chitiet_hoadon chitiet_hoadon_dichvuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chitiet_hoadon
    ADD CONSTRAINT chitiet_hoadon_dichvuid_fkey FOREIGN KEY (dichvuid) REFERENCES public.dichvu(id);


--
-- Name: chitiet_hoadon chitiet_hoadon_hoadonid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chitiet_hoadon
    ADD CONSTRAINT chitiet_hoadon_hoadonid_fkey FOREIGN KEY (hoadonid) REFERENCES public.hoadon(id);


--
-- Name: hoadon hoadon_luotchoiid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.hoadon
    ADD CONSTRAINT hoadon_luotchoiid_fkey FOREIGN KEY (luotchoiid) REFERENCES public.luotchoi(id);


--
-- Name: luotchoi luotchoi_mayid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.luotchoi
    ADD CONSTRAINT luotchoi_mayid_fkey FOREIGN KEY (mayid) REFERENCES public.mayps(id);


--
-- Name: luotchoi luotchoi_nhanvienid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.luotchoi
    ADD CONSTRAINT luotchoi_nhanvienid_fkey FOREIGN KEY (nhanvienid) REFERENCES public.nhanvien(id);


--
-- PostgreSQL database dump complete
--

\unrestrict iy6d2ZgsMGKxSIbL8q85HeDfp71RaEpZZI4QafbhWJxaQLMfBskVVWYqezpEd2l

