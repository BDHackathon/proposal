# https://skopenapi.readme.io/reference/%EB%B3%B4%ED%96%89%EC%9E%90-%EA%B2%BD%EB%A1%9C%EC%95%88%EB%82%B4

import folium
import geopandas as gpd
import pandas as pd
import mpld3
import matplotlib.pyplot as plt
import requests
import json
import webbrowser

# 1. 대한민국 지리 정보를 지도 형식으로 불러오기

geojson_file = '../Database/TL_SCCO_SIG.json'  #대한민국 데이터 파일 (경로 다를 수 있음)
gdf = gpd.read_file(geojson_file)

center_lat, center_lon = 36.5, 127.5

map_korea = folium.Map(location=[center_lat, center_lon], zoom_start=10)

folium.GeoJson(gdf).add_to(map_korea)

circle_lat, circle_lon = 37.5, 127.0
circle_name = '서울'
circle_popup = f'<strong>{circle_name}</strong>'
circle_radius = 5000

folium.Circle(location=[circle_lat, circle_lon], radius=circle_radius, popup=circle_popup,
              color='red', fill=True, fill_color='red').add_to(map_korea)



def folium_transform(type,coordinates): # Folium에 맞는 plotting을 위한 변환 함수
    L = len(coordinates)
    if type=='Point':
        return list(reversed(coordinates))
    elif type=='LineString':
        for i in range(L):
            coordinates[i] =list(reversed(coordinates[i]))
        return coordinates

# 2. 해양 쓰레기 발원지의 위도 경도 데이터 추출
# 시작점과 끝점을 잇는 코스 생성 후 위의 대한민국 지도 위에 추가

PATH = "../Database/"
file_name = "trash_location.csv"
file_name = PATH + file_name
df = pd.read_csv(file_name)

# TMAP API를 이용해 도보 경로 불러오기

for index,row in df.iterrows():

    startY = row['str_la']
    startX = row['str_lo']
    endY = row['end_la']
    endX = row['end_lo']
    if True in list(row.isna()):
        continue

    url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=function"

    payload = {
        "startX": startX,
        "startY": startY,
        "endX": endX,
        "endY": endY,
        "reqCoordType": "WGS84GEO",
        "startName": "%EC%B6%9C%EB%B0%9C",
        "endName": "%EB%8F%84%EC%B0%A9",
        "searchOption": "0",
        "resCoordType": "WGS84GEO",
        "sort": "index"
    }
    headers = {
        "accept": "application/json",
        "content-type": "application/json",
        "appKey": "e8wHh2tya84M88aReEpXCa5XTQf3xgo01aZG39k5"
    }

    response = requests.post(url, json=payload, headers=headers)
    contents = response.text
    json_ob = json.loads(contents)

    if 'error' in list(json_ob.keys()): # 예외 처리
        continue

    geom_type = json_ob['type']

    # PopUp 예시 : 지역마다 연도별 쓰레기 개수 그래프 생성 (코스에 popup 형식으로 추가)

    loc_name = row['지역']
    year_counts = row.filter(like='count')
    year_weights = row.filter(like='weight')

    year_counts.index =[name[5:9] for name in list(year_counts.index)]
    year_weights.index = [name[5:9] for name in list(year_weights.index)]

    x = [int(year) for year in list(year_counts.index)]
    y = list(year_counts.values)

    fig, ax = plt.subplots()

    ax.plot(x,y)
    ax.set_xlabel("year")
    ax.set_ylabel("trash_counts")
    ax.set_title('yearly trash counts')

    mpld3.save_html(fig,'fig.html')
    popup_html = open('fig.html','r').read()

    from folium import IFrame

    popup = folium.Popup(IFrame(popup_html, width=600, height=600), max_width=700)

    # 보행자 도보 대한미국 지도에 추가

    if geom_type == 'FeatureCollection':
        data = json_ob['features']

        for i in range(len(data)):
            raw = data[i]['geometry']
            coordinates = folium_transform(raw['type'], raw['coordinates'])
            if raw['type'] == 'Point':
                folium.Marker(location=coordinates, popup=loc_name).add_to(map_korea)
            elif raw['type'] == 'LineString':
                folium.PolyLine(locations=coordinates, popup=popup, color="orange").add_to(map_korea)
                popup = None

    elif geom_type == 'Point':
        coordinates = folium_transform(geom_type, json_ob['coordinates'])
        folium.Marker(coordinates, popup=loc_name).add_to(map_korea)
    elif geom_type == 'LineString':
        coordinates = folium_transform(geom_type, json_ob['coordinates'])
        folium.PolyLine(locations=coordinates, popup=popup, color="orange").add_to(map_korea)

# 4. HTML 형식으로 결과 저장

map_korea.save("map_path.html")
webbrowser.open("map_path.html")
