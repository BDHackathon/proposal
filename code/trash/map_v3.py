
# https://skopenapi.readme.io/reference/%EB%B3%B4%ED%96%89%EC%9E%90-%EA%B2%BD%EB%A1%9C%EC%95%88%EB%82%B4

import folium
import geopandas as gpd
import pandas as pd
from IPython.display import display
import matplotlib.pyplot as plt
import requests

import requests
import json



geojson_file = '../data/TL_SCCO_SIG.json' #대한민국 데이터 파일 (경로 다를 수 있음)
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



PATH = "../Database/"
file_name = "South_Sea_DB.csv"
file_name = PATH + file_name

df = pd.read_csv(file_name)
schemas = ['invs_area_nm','str_la','str_lo','end_la','end_lo'] # select할 column명
df = df.loc[:,schemas]
df = df.drop_duplicates()
df.reset_index(drop=True)


for index,row in df.iterrows():

    startY = row['str_la'] # latitude
    startX = row['str_lo'] # longitude
    endY = row['end_la']
    endX = row['end_lo']

    url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=function"

    # passList : 경유
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
    if 'error' in list(json_ob.keys()):
        continue
    geom_type = json_ob['type']

    if geom_type == 'FeatureCollection':
        data = json_ob['features']

        for i in range(len(data)):
            raw = data[i]['geometry']
            coordinates = folium_transform(raw['type'], raw['coordinates'])
            if raw['type'] == 'Point':
                folium.Marker(coordinates, popup="Point").add_to(map_korea)
            elif raw['type'] == 'LineString':
                folium.PolyLine(locations=coordinates, color="orange").add_to(map_korea)

    elif geom_type == 'Point':
        coordinates = folium_transform(geom_type,json_ob['coordinates'])
        folium.Marker(coordinates, popup="Point").add_to(map_korea)
    elif geom_type == 'LineString':
        coordinates = folium_transform(geom_type,json_ob['coordinates'])
        folium.PolyLine(locations=coordinates, color="orange").add_to(map_korea)

map_korea.save("map_v3.html")
#map_korea.show()
