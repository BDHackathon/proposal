# TMP

import folium
import geopandas as gpd
import pandas as pd
from IPython.display import display
import matplotlib.pyplot as plt
import requests

import requests
import json



geojson_file = '../data/TL_SCCO_SIG.json' #대한민국 데이터 파일을 json형태로 가지고있어야함
gdf = gpd.read_file(geojson_file)

center_lat, center_lon = 36.5, 127.5

map_korea = folium.Map(location=[center_lat, center_lon], zoom_start=7)

folium.GeoJson(gdf).add_to(map_korea)

circle_lat, circle_lon = 37.5, 127.0
circle_name = '서울'
circle_popup = f'<strong>{circle_name}</strong>'
circle_radius = 5000

folium.Circle(location=[circle_lat, circle_lon], radius=circle_radius, popup=circle_popup,
              color='red', fill=True, fill_color='red').add_to(map_korea)


#display(map_korea)



PATH = "../data/남해_환경피해_위협쓰레기/"
file_name = "2기 남해 14개정점(2022년 6차).csv"
file_name = PATH + file_name

df = pd.read_csv(file_name)
new_df = df.loc[df['INVS_AREA_NM']=='거제 두모',["STR_LA","STR_LO","END_LA","END_LO"]]
unique_df = new_df.drop_duplicates()

#print(unique_df)

startY = unique_df.loc[0,"STR_LA"]
startX = unique_df.loc[0,"STR_LO"]
endY = unique_df.loc[0,"END_LA"]
endX = unique_df.loc[0,"END_LO"]

print(startX,startY,endX,endY)

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
data = json_ob['features']


def folium_transform(type,coordinates):
    L = len(coordinates)
    if type=='Point':
        return list(reversed(coordinates))
    elif type=='LineString':
        for i in range(L):
            coordinates[i] =list(reversed(coordinates[i]))
        return coordinates


for i in range(len(data)):
    raw = data[i]['geometry']
    print(raw)
    coordinates = folium_transform(raw['type'],raw['coordinates'])
    if raw['type']=='Point':
        folium.Marker(coordinates,popup="점").add_to(map_korea)
    elif raw['type']=='LineString':
        folium.PolyLine(locations=coordinates,color="orange").add_to(map_korea)



map_korea.save("map.html")
#map_korea.show()
