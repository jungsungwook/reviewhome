현재 데이터센터 화재로 인해 주소를 입력 받고 해당 주소를 통해 건물을 검색할 때 사용하는 API가 사용할 수 없게 됐어.
(http://apis.data.go.kr)(해당 API는 WebclientConfig.java에 지정되어있음.)

다행히 서울특별시는 검색이 다른곳에서 가능해. 서울특별시는 어떻게 검색할 지 알려줄게.

우선 application.properties 에서 is-open-api-temp 라는 값을 true로 설정할거야.

이 값이 true로 활성된 상태라면, 
http://openapi.seoul.go.kr:8088/(%EC%9D%B8%EC%A6%9D%ED%82%A4)/json/vBigDjrTitle
경로를 통해서 데이터를 얻을 수 있어.

요청인자와 출력값은 다음과 같아

[요청인자]
변수명	타입	변수설명	값설명
KEY	String(필수)	인증키	OpenAPI 에서 발급된 인증키
TYPE	String(필수)	요청파일타입	xml : xml, xml파일 : xmlf, 엑셀파일 : xls, json파일 : json
SERVICE	String(필수)	서비스명	vBigDjrTitle
START_INDEX	INTEGER(필수)	요청시작위치	정수 입력 (페이징 시작번호 입니다 : 데이터 행 시작번호)
END_INDEX	INTEGER(필수)	요청종료위치	정수 입력 (페이징 끝번호 입니다 : 데이터 행 끝번호)
BDRG_SN	STRING(선택)	건축물대장일련번호	

[출력값]
No	출력명	출력설명
공통	list_total_count	총 데이터 건수 (정상조회 시 출력됨)
공통	RESULT.CODE	요청결과 코드 (하단 메세지설명 참고)
공통	RESULT.MESSAGE	요청결과 메시지 (하단 메세지설명 참고)
1	PLAT_PLC	대지위치
2	SGG_CD_NM	시군구코드명
3	STDG_CD_NM	법정동코드명
4	PLOT_SE_CD_NM	대지구분코드명
5	MN_LOTNO	주지번
6	SUB_LOTNO	부지번
7	SPAREA_NM	특수지명
8	BLCK_NO	블록번호
9	LT_NO	로트번호
10	NA_ROAD_CD_NM	새주소도로코드명
11	NA_STDG_CD_NM	새주소법정동코드명
12	NA_GUGSE_CD_NM	새주소지상지하구분코드명
13	NA_MN_LOTNO	새주소주지번
14	NA_SUB_LOTNO	새주소부지번
15	BDRG_SN	건축물대장일련번호
16	LDGR_SE_CD_NM	대장구분코드명
17	LDGR_KIND_CD_NM	대장종류코드명
18	DNG_NM	동명
19	MANX_SE_CD_NM	주부속구분코드명
20	SIAR	대지면적
21	BDAR	건축면적
22	BDCVRT	건폐율
23	GFA	연면적
24	FART_CMPTTN_GFA	용적률산정연면적
25	FART	용적률
26	STRCT_CD_NM	구조코드명
27	ETC_STRCT_INFO	기타구조정보
28	MN_USG_CD_NM	주용도코드명
29	ETC_USG_CN	기타용도내용
30	ROOF_CD_NM	지붕코드명
31	ETC_ROOF_NM	기타지붕명
32	HH_CNT	세대수
33	FML_CNT	가구수
34	HO_CNT	호수
35	GRND_NOFL	지상층수
36	UDGD_NOFL	지하층수
37	HG	높이
38	PSNGR_ELVTR_CNT	승용승강기수
39	EUSE_ELVTR_CNT	비상용승강기수
40	ANX_BDST_CNT	부속건축물수
41	ANX_BDST_AREA	부속건축물면적
42	TOL_DNG_GFA	총동연면적
43	INDR_MCNCL_CNTOM	옥내기계식대수
44	INDR_MCNCL_AREA	옥내기계식면적
45	OTDR_MCNCL_CNTOM	옥외기계식대수
46	OTDR_MCNCL_AREA	옥외기계식면적
47	INDR_SFPRPL_CNTOM	옥내자주식대수
48	INDR_SFPRPL_AREA	옥내자주식면적
49	OTDR_SFPRPL_CNTOM	옥외자주식대수
50	OTDR_SFPRPL_AREA	옥외자주식면적
51	PRMSN_YMD	허가일자
52	BGNCST_YMD	착공일자
53	USE_APRV_YMD	사용승인일자
54	ENRG_EFCY_GRD_VL	에너지효율등급값
55	ENRG_RTRDT	에너지절감률
56	EPI_SCR	EPI점수
57	ECFRD_BDST_GRD_VL	친환경건축물등급값
58	ECFRD_BDST_CERT_SCR	친환경건축물인증점수
59	INTG_BDST_GRD_VL	지능형건축물등급값
60	INTG_BDST_CERT_SCR	지능형건축물인증점수
61	RSER_DESIGN_APLCN_YN	내진설계적용여부
62	RSER_ABLT_CN	내진능력내용

[실제 응답 예시]
{
  "vBigDjrTitle": {
    "list_total_count": 591944,
    "RESULT": {
      "CODE": "INFO-000",
      "MESSAGE": "정상 처리되었습니다"
    },
    "row": [
      {
        "PLAT_PLC": "서울특별시 강서구 방화동 246-66",
        "SGG_CD_NM": "서울특별시 강서구",
        "STDG_CD_NM": "방화동",
        "PLOT_SE_CD_NM": "대지",
        "MN_LOTNO": "0246",
        "SUB_LOTNO": "0066",
        "SPAREA_NM": "",
        "BLCK_NO": "",
        "LT_NO": "",
        "NA_ROAD_CD_NM": "방화대로",
        "NA_STDG_CD_NM": "방화동",
        "NA_GUGSE_CD_NM": "지상",
        "NA_MN_LOTNO": "309",
        "NA_SUB_LOTNO": "4",
        "BDRG_SN": 1017111257,
        "LDGR_SE_CD_NM": "일반",
        "LDGR_KIND_CD_NM": "일반건축물",
        "DNG_NM": "",
        "MANX_SE_CD_NM": "주건축물",
        "SIAR": 274,
        "BDAR": 99.48,
        "BDCVRT": 36.31,
        "GFA": 396.72,
        "FART_CMPTTN_GFA": 288.6,
        "FART": 105.33,
        "STRCT_CD_NM": "철근콘크리트구조",
        "ETC_STRCT_INFO": "철근콘크리트조",
        "MN_USG_CD_NM": "단독주택",
        "ETC_USG_CN": "주택 및 창고",
        "ROOF_CD_NM": "(철근)콘크리트",
        "ETC_ROOF_NM": "평스라브",
        "HH_CNT": 0,
        "FML_CNT": 0,
        "HO_CNT": 0,
        "GRND_NOFL": "3",
        "UDGD_NOFL": "1",
        "HG": 9.9,
        "PSNGR_ELVTR_CNT": 0,
        "EUSE_ELVTR_CNT": 0,
        "ANX_BDST_CNT": 0,
        "ANX_BDST_AREA": 0,
        "TOL_DNG_GFA": 396.72,
        "INDR_MCNCL_CNTOM": 0,
        "INDR_MCNCL_AREA": 0,
        "OTDR_MCNCL_CNTOM": 0,
        "OTDR_MCNCL_AREA": 0,
        "INDR_SFPRPL_CNTOM": 0,
        "INDR_SFPRPL_AREA": 0,
        "OTDR_SFPRPL_CNTOM": 3,
        "OTDR_SFPRPL_AREA": 34.5,
        "PRMSN_YMD": "1988-05-24",
        "BGNCST_YMD": "",
        "USE_APRV_YMD": "1989-06-30",
        "ENRG_EFCY_GRD_VL": "",
        "ENRG_RTRDT": 0,
        "EPI_SCR": 0,
        "ECFRD_BDST_GRD_VL": "",
        "ECFRD_BDST_CERT_SCR": 0,
        "INTG_BDST_GRD_VL": "",
        "INTG_BDST_CERT_SCR": 0,
        "RSER_DESIGN_APLCN_YN": "0",
        "RSER_ABLT_CN": ""
      }, ...
    ]
  }
}

GetBrTitleInfoResponseDto, PostAddressInfoDto, SearchAddressDto를 참고해서 새로 임시DTO를 만들어도 되고, 아니면 기존 DTO를 활용해도 돼.

다만, 만약 화재가 복구되고 기존에 사용하던 API가 돌아오면 단순히 is-open-api-temp 값을 수정해서 기존기능을 사용할 거니까, 컨트롤러상에서 해당 값을 참조하여 서비스를 분기처리하는게 제일 좋아보여. 즉 기존 서비스에 영향이 없도록 개발해서 분기처리하라는 뜻이야.

참고로 API키는 application.properties에 temp-open-api-key 로 선언해놨어.