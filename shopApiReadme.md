이제 우리는 위치 정보를 활용하여 주변 상권을 검색할거야.
주변 상권을 검색하는 방법을 알려줄게.
endpoint : https://apis.data.go.kr/B553077/api/open/sdsc2/storeListInRadius
url 파라미터:
ServiceKey, pageNo, numOfRows, radius, cx, cy
우선 serviceKey의 경우 application.properties 에 open-api-key 로 저장되어있어.
radius는 미터단위인데 최대 2000미터라니까 2000미터로 설정하면 될 거 같고, cx와 cy는 geo_location 테이블에 point_x 와 point_y로 저장되어있어.
연관관계를 설명해줄게.
geo_location 테이블에는 post_address_info_uuid라는 컬럼이 있는데, 이건 해당 post_address_info의 위치좌표란 뜻이야. 
post_address_info는 건물을 검색했을 때 우선 post_address를 저장하게 되고, 상세 정보를 담게 되는 곳이야. 예를들면 아파트의 경우 하나의 post_address를 갖게 되고 1동~n동까지는 post_address_info에 저장되겠지?

자 그러면 이제 우리는 상권정보를 우선 post_address_info가 저장될 때 상권정보도 함꼐 저장되야해. 상권정보의 응답값은 다음과 같아.

{
  "header": {
    "description": "소상공인시장진흥공단 반경내 상가업소정보",
    "columns": [
      "상가업소번호",
      "상호명",
      "지점명",
      "상권업종대분류코드",
      "상권업종대분류명",
      "상권업종중분류코드",
      "상권업종중분류명",
      "상권업종소분류코드",
      "상권업종소분류명",
      "표준산업분류코드",
      "표준산업분류명",
      "시도코드",
      "시도명",
      "시군구코드",
      "시군구명",
      "행정동코드",
      "행정동명",
      "법정동코드",
      "법정동명",
      "PNU코드",
      "대지구분코드",
      "대지구분명",
      "지번본번지",
      "지번부번지",
      "지번주소",
      "도로명코드",
      "도로명",
      "건물본번지",
      "건물부번지",
      "건물관리번호",
      "건물명",
      "도로명주소",
      "구우편번호",
      "신우편번호",
      "동정보",
      "층정보",
      "호정보",
      "경도",
      "위도"
    ],
    "stdrYm": "202506",
    "resultCode": "00",
    "resultMsg": "NORMAL SERVICE"
  },
  "body": {
    "items": [
      {
        "bizesId": "MA010120220800111901",
        "bizesNm": "SBS노래연습장",
        "brchNm": "",
        "indsLclsCd": "R1",
        "indsLclsNm": "예술·스포츠",
        "indsMclsCd": "R104",
        "indsMclsNm": "유원지·오락",
        "indsSclsCd": "R10407",
        "indsSclsNm": "노래방",
        "ksicCd": "R91223",
        "ksicNm": "노래 연습장 운영업",
        "ctprvnCd": "11",
        "ctprvnNm": "서울특별시",
        "signguCd": "11710",
        "signguNm": "송파구",
        "adongCd": "11710632",
        "adongNm": "가락2동",
        "ldongCd": "1171010700",
        "ldongNm": "가락동",
        "lnoCd": "1171010700101740027",
        "plotSctCd": "1",
        "plotSctNm": "대지",
        "lnoMnno": 174,
        "lnoSlno": 27,
        "lnoAdr": "서울특별시 송파구 가락동 174-27",
        "rdnmCd": "117104169403",
        "rdnm": "서울특별시 송파구 오금로46길",
        "bldMnno": 22,
        "bldSlno": "",
        "bldMngNo": "1171010700101740027005374",
        "bldNm": "",
        "rdnmAdr": "서울특별시 송파구 오금로46길 22",
        "oldZipcd": "138811",
        "newZipcd": "05769",
        "dongNo": "",
        "flrNo": "지",
        "hoNo": "",
        "lon": 127.13487420192,
        "lat": 37.495687165975
      }, ...
    ]
  }
}

우리는 이 정보를 저장할 때 body의 items 들만 저장하면 돼.
이걸 저장할 테이블도 없으니까 현재 코드에서 다른 DB를 선언하는 코드를 참조해서 비슷한 형식으로 만들어줘.(변수명 비슷하게, 스웨거 적용, 어노테이션 같게 등)